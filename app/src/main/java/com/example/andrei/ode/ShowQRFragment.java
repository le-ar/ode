package com.example.andrei.ode;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Collections;
import java.util.TimeZone;

public class ShowQRFragment extends Fragment {

    static ShowQRFragment This;
    ImageView Img;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_show_qr, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Img = ((ImageView) view.findViewById(R.id.imageView12));
        This = this;
        (new RefreshQR()).execute();

        ((ConstraintLayout) view.findViewById(R.id.constraintLayout2)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                (new RefreshQR()).execute();
            }
        });
    }

    static class RefreshQR extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... voids) {
            try {
                String s = MainActivity.doGet(MainActivity.Domain + "/get_qr?id="
                        + MainActivity.MyID + "&token=" + MainActivity.MyToken);
                JSONObject jsonObject = new JSONObject(s);
                if (jsonObject.has("error")) {
                    MainActivity.ToastError(s);
                    MainActivity.This.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            MainActivity.This.onNavigationItemSelected(MainActivity.navigationView.getMenu().getItem(0));
                        }
                    });
                    return "";
                }
                if (jsonObject.has("status")
                        && jsonObject.getString("status").equals("OK")) {
                    ShowQRFragment.This.setQRImage(jsonObject.getString("token"));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return "";
        }

        @Override
        protected void onPostExecute(final String result) {
        }
    }

    void setQRImage(String content1) {
        final String content = content1;
        MainActivity.This.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                QRCodeWriter writer = new QRCodeWriter();
                try {
                    BitMatrix bitMatrix = writer.encode(content, BarcodeFormat.QR_CODE, 512, 512);
                    int width = bitMatrix.getWidth();
                    int height = bitMatrix.getHeight();
                    Bitmap bmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
                    for (int x = 0; x < width; x++) {
                        for (int y = 0; y < height; y++) {
                            bmp.setPixel(x, y, bitMatrix.get(x, y) ? Color.BLACK : Color.WHITE);
                        }
                    }
                    Img.setImageBitmap(bmp);

                } catch (WriterException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
