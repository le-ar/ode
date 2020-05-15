package com.example.andrei.ode;


import android.annotation.SuppressLint;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.AsyncTask;
import android.util.Log;

import com.example.andrei.ode.MainActivity;
import com.example.andrei.ode.ShowQRFragment;
import com.google.android.gms.vision.barcode.Barcode;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class BarcodeGraphic extends GraphicOverlay.Graphic {

    Map<String, Integer> QRS = new HashMap<>();

    private int mId;

    private static final int COLOR_CHOICES[] = {
            Color.CYAN
    };

    private static int mCurrentColorIndex = 0;

    private Paint mRectPaint;
    private Paint mTextPaint;
    private volatile Barcode mBarcode;

    BarcodeGraphic(GraphicOverlay overlay) {
        super(overlay);

        mCurrentColorIndex = (mCurrentColorIndex + 1) % COLOR_CHOICES.length;
        final int selectedColor = COLOR_CHOICES[mCurrentColorIndex];

        mRectPaint = new Paint();
        mRectPaint.setColor(selectedColor);
        mRectPaint.setStyle(Paint.Style.STROKE);
        mRectPaint.setStrokeWidth(4.0f);

        mTextPaint = new Paint();
        mTextPaint.setColor(selectedColor);
        mTextPaint.setTextSize(36.0f);
    }

    public int getId() {
        return mId;
    }

    public void setId(int id) {
        this.mId = id;
    }

    public Barcode getBarcode() {
        return mBarcode;
    }

    void updateItem(Barcode barcode) {
        mBarcode = barcode;
        postInvalidate();
    }

    @SuppressLint("StaticFieldLeak")
    @Override
    public void draw(Canvas canvas) {
        Barcode barcode = mBarcode;
        if (barcode == null) {
            return;
        }

        RectF rect = new RectF(barcode.getBoundingBox());
        rect.left = translateX(rect.left);
        rect.top = translateY(rect.top);
        rect.right = translateX(rect.right);
        rect.bottom = translateY(rect.bottom);
        canvas.drawRect(rect, mRectPaint);

        if (!QRS.containsKey(barcode.rawValue)) {
            canvas.drawText("Проверяется...", rect.left, rect.bottom, mTextPaint);
            QRS.put(barcode.rawValue, 0);
            final String QRText = barcode.rawValue;
            new AsyncTask<Void, Void, String>() {
                @Override
                protected String doInBackground(Void... voids) {
                    String s = "";
                    try {
                        s = com.example.andrei.ode.MainActivity.doGet(com.example.andrei.ode.MainActivity.Domain
                                + "/check_qr?event_id=" + EventFragment.CurrID
                                + "&qr_token=" + QRText
                                + "&token=" + MainActivity.MyToken);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return s;
                }

                @Override
                protected void onPostExecute(final String result) {
                    try {
                        JSONObject jsonObject = new JSONObject(result);
                        if (jsonObject.has("error")) {
                            QRS.put(QRText, 2);
                            MainActivity.ToastError(result);
                            return;
                        }
                        if (jsonObject.has("status")
                                && jsonObject.getString("status").equals("OK")) {
                            jsonObject.getString("count");
                            jsonObject.getString("rating");
                            if (Event.EventsID.containsKey(EventFragment.CurrID)) {
                                Event.Events.get(Event.EventsID.get(EventFragment.CurrID)).Pa_count = jsonObject.getLong("count");
                                Event.Events.get(Event.EventsID.get(EventFragment.CurrID)).Pa_rating = jsonObject.getLong("rating");
                            }
                            QRS.put(QRText, 1);

                        } else {
                            QRS.put(QRText, 2);
                        }
                    } catch (JSONException e) {
                        Log.e("JSON", e.getMessage());
                        e.printStackTrace();
                    }
                }
            }.execute();
        } else if (QRS.get(barcode.rawValue) == 0) {
            canvas.drawText("Проверяется...", rect.left, rect.bottom, mTextPaint);
        } else if (QRS.get(barcode.rawValue) == 2) {
            canvas.drawText("QR неверный", rect.left, rect.bottom, mTextPaint);
        } else if (QRS.get(barcode.rawValue) == 1) {
            canvas.drawText("ОК", rect.left, rect.bottom, mTextPaint);
        }
    }
}
