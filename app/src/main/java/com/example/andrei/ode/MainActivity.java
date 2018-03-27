package com.example.andrei.ode;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.graphics.drawable.VectorDrawableCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    static Toolbar toolbar;

    static long MyID = 0;
    static long MyPRating = 0;
    static long MyCRating = 0;
    static String MyToken = "";
    static String MyTokenVK = "";
    static String MyPhoto = "";
    static String MyFName = "";
    static String MyLName = "";

    static boolean isMainFragment = true;
    EditText edtSearch;
    static Activity MainContext;
    static MainActivity This;
    static NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        This = this;
        MainContext = this;
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        onNavigationItemSelected(navigationView.getMenu().getItem(0));
        Event.Refreash();
    }

    @Override
    public void onBackPressed() {
            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if (!isMainFragment) {
            onNavigationItemSelected(navigationView.getMenu().getItem(0));
        } else {
            super.onBackPressed();
        }
    }

    /*@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }*/

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        setTitle("");

        isMainFragment = false;

        Fragment fragment = null;
        Class fragmentClass = null;
        Class fragmentTollBar = null;
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_show_list) {
            isMainFragment = true;
            fragmentClass = FirstFragment.class;
            fragmentTollBar = FirstFragmentT.class;
        } else if (id == R.id.nav_show_map) {
            fragmentClass = MapsFragment.class;
            fragmentTollBar = FirstFragmentT.class;
        } else if (id == R.id.nav_create_event) {
            fragmentClass = CreateEventFragment.class;
            fragmentTollBar = FirstFragmentT.class;
        } else if (id == R.id.nav_chat_location) {
            fragmentClass = ChatFragment.class;
            fragmentTollBar = FirstFragmentT.class;
        } else if (id == R.id.nav_show_qr) {
            fragmentClass = ShowQRFragment.class;
            fragmentTollBar = FirstFragmentT.class;
        }

        try {
            fragment = (Fragment) fragmentClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.container, fragment).commit();
        item.setChecked(true);


        try {
            fragment = (Fragment) fragmentTollBar.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        fragmentManager.beginTransaction().replace(toolbar.getId(), fragment).commit();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    public static String doGet(String url)
            throws Exception {

        URL obj = new URL(url);
        HttpURLConnection connection = (HttpURLConnection) obj.openConnection();

        //add reuqest header
        connection.setRequestMethod("GET");
        connection.setRequestProperty("User-Agent", "Mozilla/5.0" );
        connection.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
        connection.setRequestProperty("Content-Type", "application/json");

        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = bufferedReader.readLine()) != null) {
            response.append(inputLine);
        }
        bufferedReader.close();

//      print result
        Log.d("111","Response string: " + response.toString());


        return response.toString();
    }
}
