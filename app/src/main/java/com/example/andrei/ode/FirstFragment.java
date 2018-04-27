package com.example.andrei.ode;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.TimeZone;

public class FirstFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener{


    public static SwipeRefreshLayout mSwipeRefreshLayout;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_first, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mSwipeRefreshLayout = (SwipeRefreshLayout) getView().findViewById(R.id.swipe_container);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        for (int i = 0; i < Event.Events.size(); i++) {
            final Event evt = Event.Events.get(i);
            ConstraintLayout item = (ConstraintLayout) getLayoutInflater().inflate(R.layout.item_event, null);
            ((TextView) (item.findViewById(R.id.textName))).setText(evt.Name);

            if (System.currentTimeMillis() / 1000 > evt.TimeBegin) {
                ((TextView) (item.findViewById(R.id.textPeople))).setText(String.valueOf(evt.Fu_count));
                ((TextView) (item.findViewById(R.id.textRating))).setText(String.valueOf(evt.Fu_rating));
                ((TextView) (item.findViewById(R.id.textComments))).setText(String.valueOf(evt.Comments_count));
            } else {
                ((TextView) (item.findViewById(R.id.textPeople))).setText(String.valueOf(evt.Pa_count));
                ((TextView) (item.findViewById(R.id.textRating))).setText(String.valueOf(evt.Pa_rating));
                ((TextView) (item.findViewById(R.id.textComments))).setText(String.valueOf(evt.Comments_count));
            }
            Date date = new java.util.Date(evt.TimeBegin * 1000L);
            SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd.MM.yyyy\nHH:mm");
            ((TextView) (item.findViewById(R.id.textTime))).setText(sdf.format(date));
            if (evt.Cost > 0)
                ((TextView) (item.findViewById(R.id.textCost))).setText(String.valueOf(evt.Cost));
            else
                ((TextView) (item.findViewById(R.id.textCost))).setText("Бесплатно");
            final int finalI = i;
            ((ImageView)item.findViewById(R.id.imgLocation)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                        MapActivity.isShow = true;
                        MapActivity.Lat = evt.Latitude;
                        MapActivity.Lng = evt.Longitude;
                        startActivity(new Intent(MainActivity.MainContext, MapActivity.class));
                }
            });
            item.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MainActivity.isMainFragment = false;
                    EventFragment.CurrID = evt.Id;

                    Fragment fragment = null;
                    Class fragmentClass = null;
                    Class fragmentTollBar = null;

                    fragmentClass = EventFragment.class;
                    fragmentTollBar = EventFragmentT.class;
                    FragmentManager fragmentManager = MainActivity.This.getSupportFragmentManager();

                    try {
                        fragment = (Fragment) fragmentTollBar.newInstance();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    fragmentManager.beginTransaction().replace(MainActivity.toolbar.getId(), fragment).commit();

                    try {
                        fragment = (Fragment) fragmentClass.newInstance();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    fragmentManager.beginTransaction().replace(R.id.container, fragment).commit();


                }
            });
            ((LinearLayout) getView().findViewById(R.id.Main)).addView(item);
        }

    }
    @Override
    public void onRefresh() {
        Event.Refreash(true);
    }
}