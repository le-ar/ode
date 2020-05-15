package com.example.andrei.ode;

import android.annotation.SuppressLint;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.util.EventLog;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import static com.example.andrei.ode.LoginActivity.db;

@Entity
public class Event implements Comparable {
    public static List<Event> Events = new ArrayList<>();
    public static Map<Long, Integer> EventsID = new HashMap<>();

   @PrimaryKey public long Id;
    public String Name;
    public long AuthorId;
    public long TimeBegin;
    public long TimeEnd;
    public double Longitude;
    public double Latitude;
    public String ShortDescription;
    public String LongDescription;
    public long Cost;
    public int Type;
    public long Fu_count;
    public long Pa_count;
    public long Fu_rating;
    public long Pa_rating;
    public long Comments_count;
    public long MaxPeople;

    public static void Refresh(final boolean gotoMain) {

        new AsyncTask<Void, String, String>() {
            @Override
            protected String doInBackground(Void... voids) {
                String s = "";
                try {
                    s = MainActivity.doGet(MainActivity.Domain + "/showallevents");
                    LoginActivity.db.getEventDao().deleteAll();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return s;
            }

            @SuppressLint("StaticFieldLeak")
            @Override
            protected void onPostExecute(final String result) {
                JSONArray jsonarray = null;
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    jsonarray = jsonObject.getJSONArray("events");
                    int offset = TimeZone.getDefault().getRawOffset() + TimeZone.getDefault().getDSTSavings();
                    Events.clear();
                    EventsID.clear();

                    for (int i = 0; i < jsonarray.length(); i++) {
                        JSONObject jsonobject = jsonarray.getJSONObject(i);
                        final Event evt = new Event();
                        evt.Id = jsonobject.getLong("id");
                        evt.AuthorId = jsonobject.getLong("main_id");
                        evt.TimeBegin = jsonobject.getLong("time");
                        evt.TimeEnd = jsonobject.getLong("time_end");
                        evt.Cost = jsonobject.getLong("cost");
                        evt.MaxPeople = jsonobject.getLong("max_people");
                        evt.Fu_rating = jsonobject.getLong("fu_reputation");
                        evt.Pa_rating = jsonobject.getLong("pa_reputation");
                        evt.Fu_count = jsonobject.getLong("fu_count");
                        evt.Pa_count = jsonobject.getLong("pa_count");
                        evt.Comments_count = jsonobject.getLong("comments_count");
                        evt.Type = jsonobject.getInt("type");
                        evt.Name = jsonobject.getString("name");
                        evt.ShortDescription = jsonobject.getString("s_description");
                        evt.LongDescription = jsonobject.getString("l_description");
                        evt.Latitude = jsonobject.getDouble("latitude");
                        evt.Longitude = jsonobject.getDouble("longitude");
                        Events.add(evt);
                        new AsyncTask<Void, Void, Void>() {
                            @Override
                            protected Void doInBackground(Void... voids) {
                                db.getEventDao().insert(evt);
                                return null;
                            }
                        }.execute();
                    }
                    Collections.sort(Events);
                    for (int i = 0; i < Events.size(); i++) {
                        EventsID.put(Events.get(i).Id, i);
                    }
                    if (gotoMain)
                        MainActivity.This.onNavigationItemSelected(MainActivity.This.navigationView.getMenu().getItem(0));
                } catch (JSONException e) {
                    Log.e("JSON", e.getMessage());
                    e.printStackTrace();
                }
            }
        }.execute();
    }

    @Override
    public int compareTo(@NonNull Object o) {
        if (this.TimeBegin > ((Event) o).TimeBegin) {
            return 1;
        } else {
            return -1;
        }
    }
}
