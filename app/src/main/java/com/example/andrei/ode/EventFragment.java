package com.example.andrei.ode;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GestureDetectorCompat;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TabHost;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

public class EventFragment extends Fragment {

    public static int CurrID = 0;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_event, container, false);
    }

    @SuppressLint("StaticFieldLeak")
    @Override
    public void onViewCreated(final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        TabHost tabHost = (TabHost) view.findViewById(R.id.tabHost);

        tabHost.setup();

        TabHost.TabSpec tabSpec = tabHost.newTabSpec("tag1");

        tabSpec.setContent(R.id.tab3);
        tabSpec.setIndicator("Описание");
        tabHost.addTab(tabSpec);

        tabSpec = tabHost.newTabSpec("tag2");
        tabSpec.setContent(R.id.tab4);
        tabSpec.setIndicator("Комментарии");
        tabHost.addTab(tabSpec);

        tabHost.setCurrentTab(0);

        ((TextView) view.findViewById(R.id.textName)).setText(Event.Events.get(CurrID).Name);

        Date date = new java.util.Date(Event.Events.get(CurrID).TimeBegin * 1000L);
        SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd.MM.yyyy\nHH:mm");
        String timeZone = Calendar.getInstance().getTimeZone().getID();
        ((TextView) (view.findViewById(R.id.textTime))).setText(sdf.format(date));

        date = new java.util.Date(Event.Events.get(CurrID).TimeEnd * 1000L);
        timeZone = Calendar.getInstance().getTimeZone().getID();
        ((TextView) (view.findViewById(R.id.textTime3))).setText(sdf.format(date));

        if (Event.Events.get(CurrID).Cost > 0)
            ((TextView) (view.findViewById(R.id.textCost))).setText(String.valueOf(Event.Events.get(CurrID).Cost));
        else
            ((TextView) (view.findViewById(R.id.textCost))).setText("Бесплатно");

        ((TextView) view.findViewById(R.id.textView10)).setText(Event.Events.get(CurrID).LongDescription);

        ((ImageView) view.findViewById(R.id.imageView8)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String comment = ((EditText) view.findViewById(R.id.editText2)).getText().toString();
                new AsyncTask<Void, String, String>() {
                    @Override
                    protected String doInBackground(Void... voids) {
                        String s = "";
                        try {
                            s = MainActivity.doGet("http://54.38.186.12/add_comment?main_id=" + String.valueOf(MainActivity.MyID)
                                    + "&event_id=" + String.valueOf(Event.Events.get(CurrID).Id) + "&token="
                                    + MainActivity.MyToken + "&text=" + URLEncoder.encode(comment, "utf-8"));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        return s;
                    }

                    @Override
                    protected void onPostExecute(final String result) {
                        MainActivity.MainContext.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                RefreshComments();
                            }
                        });
                    }
                }.execute();
            }
        });

        new AsyncTask<Void, String, String>() {
            @Override
            protected String doInBackground(Void... voids) {
                String s = "";
                try {
                    s = MainActivity.doGet("http://54.38.186.12/get_user?id=" + String.valueOf(Event.Events.get(CurrID).AuthorId));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return s;
            }

            @Override
            protected void onPostExecute(final String result) {
                MainActivity.MainContext.runOnUiThread(new Runnable() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void run() {
                        try {
                            final JSONObject jsonObject = new JSONObject(result);
                            new URLImage((ImageView) view.findViewById(R.id.imageView3)).execute(jsonObject.getString("photo"));
                            if (Event.Events.get(CurrID).AuthorId == MainActivity.MyID) {
                                if (System.currentTimeMillis() / 1000 > Event.Events.get(EventFragment.CurrID).TimeBegin
                                        && System.currentTimeMillis() / 1000 < Event.Events.get(EventFragment.CurrID).TimeEnd)
                                    ((TextView) view.findViewById(R.id.textView9)).setText(jsonObject.getString("Сканировать QR"));
                                if (System.currentTimeMillis() / 1000 < Event.Events.get(EventFragment.CurrID).TimeBegin)
                                    ((TextView) view.findViewById(R.id.textView9)).setText(jsonObject.getString("Удалить мероприятие"));
                                if (System.currentTimeMillis() / 1000 > Event.Events.get(EventFragment.CurrID).TimeEnd) {
                                    ((ViewManager) view.findViewById(R.id.imageView5).getParent()).removeView(view.findViewById(R.id.imageView5));
                                    ((ViewManager) view.findViewById(R.id.textView9).getParent()).removeView(view.findViewById(R.id.textView9));
                                }
                            } else {
                                view.findViewById(R.id.imageView5).setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Intent browserIntent = null;
                                        try {
                                            browserIntent = new Intent(Intent.ACTION_VIEW,
                                                    Uri.parse("http://vk.com/id" + jsonObject.getString("vk_id")));
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                        startActivity(browserIntent);
                                    }
                                });
                                ((TextView) view.findViewById(R.id.textView9)).setText("Связаться с\nорганизатором");
                            }

                            ((TextView) view.findViewById(R.id.textRatingCreator)).setText(jsonObject.getString("reputation"));
                            ((TextView) view.findViewById(R.id.textView7)).setText(jsonObject.getString("first_name") + " " + jsonObject.getString("last_name"));

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        }.execute();

        RefreshComments();

        final GestureDetectorCompat gDetector = new GestureDetectorCompat(getActivity(), new GestureDetector.OnGestureListener() {

            @Override
            public boolean onDown(MotionEvent e) {
                return true;
            }

            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
                                   float velocityY) {
                Log.i("motion", "onFling has been called!");
                final int SWIPE_MIN_DISTANCE = 120;
                final int SWIPE_MAX_OFF_PATH = 250;
                final int SWIPE_THRESHOLD_VELOCITY = 200;
                try {
                    if (Math.abs(e1.getY() - e2.getY()) > SWIPE_MAX_OFF_PATH)
                        return false;
                    if (e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE
                            && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                        Log.i("motion", "Right to Left");
                        switchTabs(false, view);
                    } else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE
                            && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {

                        Log.i("motion", "Left to Right");
                        switchTabs(true, view);

                    }
                } catch (Exception e) {
                    // nothing
                }
                return false;
            }

            @Override
            public void onLongPress(MotionEvent e) {

            }

            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2,
                                    float distanceX, float distanceY) {
                return false;
            }

            @Override
            public void onShowPress(MotionEvent e) {

            }

            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                return false;
            }
        });

        ((TabHost) view.findViewById(R.id.tabHost)).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return gDetector.onTouchEvent(event);
            }
        });
    }

    @SuppressLint("StaticFieldLeak")
    void RefreshComments() {
        new AsyncTask<Void, String, String>() {
            @Override
            protected String doInBackground(Void... voids) {
                String s = "";
                try {
                        s = MainActivity.doGet("http://54.38.186.12/get_comments?r=t&id=" + String.valueOf(Event.Events.get(CurrID).Id)
                                + "&from=0&to=100");
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return s;
            }

            @Override
            protected void onPostExecute(final String result) {
                MainActivity.MainContext.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            ((LinearLayout) getView().findViewById(R.id.Comments)).removeAllViews();
                            JSONArray jsonarray = null;
                            final JSONObject jsonObject = new JSONObject(result);
                            jsonarray = jsonObject.getJSONArray("comments");
                            for (int i = 0; i < jsonarray.length(); i++) {
                                JSONObject jsonobject = jsonarray.getJSONObject(i);
                                ConstraintLayout item = (ConstraintLayout) getLayoutInflater().inflate(R.layout.item_comment, null);
                                new URLImage((ImageView) item.findViewById(R.id.imgIcon)).execute(jsonobject.getString("photo"));
                                ((TextView) (item.findViewById(R.id.textName))).setText(jsonobject.getString("author_name"));
                                Date date = new java.util.Date(jsonobject.getLong("time") * 1000L);
                                @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new java.text.SimpleDateFormat("HH:mm dd.MM.yyyy");
                                ((TextView) (item.findViewById(R.id.textCost))).setText(sdf.format(date));
                                ((TextView) (item.findViewById(R.id.textTime))).setText(jsonobject.getString("text"));
                                ((LinearLayout) getView().findViewById(R.id.Comments)).addView(item);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        }.execute();
    }

    public void switchTabs(boolean direction, View view) {

        Log.w("switch Tabs", "idemo direction");
        if (direction) // true = move left
        {
            if (((TabHost) view.findViewById(R.id.tabHost)).getCurrentTab() != 0)
                ((TabHost) view.findViewById(R.id.tabHost)).setCurrentTab(((TabHost) view.findViewById(R.id.tabHost)).getCurrentTab() - 1);
        } else
        // move right
        {
            if (((TabHost) view.findViewById(R.id.tabHost)).getCurrentTab() != (((TabHost) view.findViewById(R.id.tabHost)).getTabWidget()
                    .getTabCount() - 1))
                ((TabHost) view.findViewById(R.id.tabHost)).setCurrentTab(((TabHost) view.findViewById(R.id.tabHost)).getCurrentTab() + 1);

        }


    }
}