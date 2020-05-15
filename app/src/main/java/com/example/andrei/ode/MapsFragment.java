package com.example.andrei.ode;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static android.content.Context.LOCATION_SERVICE;

public class MapsFragment extends Fragment implements OnMapReadyCallback {

    private GoogleMap mMap;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_map, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getFragmentManager() == null)
            return;
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map1);
        mapFragment.getMapAsync(this);
    }

    Map<Marker, Long> Markers = new HashMap<>();

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;


        LocationManager locationManager = (LocationManager) MainActivity.This.getSystemService(LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        String provider = locationManager.getBestProvider(criteria, true);
        if (ActivityCompat.checkSelfPermission(MainActivity.This, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(MainActivity.This, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        Location location = locationManager.getLastKnownLocation(provider);
        if (location != null) {
            double latitude = location.getLatitude();
            double longitude = location.getLongitude();

            LatLng coordinate = new LatLng(latitude, longitude);
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(coordinate, 15f));
        }

        mMap.clear();

        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                if (Markers.containsKey(marker)) {
                    MainActivity.isMainFragment = false;
                    EventFragment.CurrID = Markers.get(marker);

                    Fragment fragment = null;

                    Class fragmentClass = EventFragment.class;
                    Class fragmentTollBar = EventFragmentT.class;
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
            }
        });
        Markers.clear();
        mMap.setMyLocationEnabled(true);
        for (int i = 0; i < Event.Events.size(); i++) {
            try {

                mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {

                    @Override
                    public View getInfoWindow(Marker arg0) {
                        return null;
                    }

                    @Override
                    public View getInfoContents(Marker marker) {

                        LinearLayout info = new LinearLayout(MainActivity.This);
                        info.setOrientation(LinearLayout.VERTICAL);

                        TextView title = new TextView(MainActivity.This);
                        title.setTextColor(Color.BLACK);
                        title.setGravity(Gravity.CENTER);
                        title.setTypeface(null, Typeface.BOLD);
                        title.setText(marker.getTitle());

                        TextView snippet = new TextView(MainActivity.This);
                        snippet.setTextColor(Color.GRAY);
                        snippet.setText(marker.getSnippet());

                        info.addView(title);
                        info.addView(snippet);

                        return info;
                    }
                });
                Geocoder geocoder = new Geocoder(MainActivity.This, Locale.getDefault());
                Event evt = Event.Events.get(i);
                @SuppressLint("SimpleDateFormat") Marker mk = mMap.addMarker(new MarkerOptions()
                        .position(new LatLng(Event.Events.get(i).Latitude, Event.Events.get(i).Longitude))
                        .title(Event.Events.get(i).Name)
                        .snippet(geocoder.getFromLocation(evt.Latitude, evt.Longitude, 1).get(0).getAddressLine(0)
                                + "\nТип: " + CreateEventFragment.data[evt.Type]
                                + "\nВремя начала: " + (new java.text.SimpleDateFormat("dd.MM.yyyy | HH:mm")).format(new java.util.Date(evt.TimeBegin * 1000L))
                                + "\nСтоимсть: " + (evt.Cost > 0 ? String.valueOf(evt.Cost) : "Бесплатно")
                                + "\nРейтинг: " + (System.currentTimeMillis() / 1000 <= evt.TimeBegin
                                ? evt.Fu_count + "/" + evt.Fu_rating : evt.Pa_count + "/" + evt.Pa_rating)
                        ));
                Markers.put(mk, evt.Id);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

}