package com.example.andrei.ode;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.arch.persistence.room.Room;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.graphics.drawable.VectorDrawableCompat;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
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
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.SupportMapFragment;
import com.vk.sdk.VKScope;
import com.vk.sdk.VKSdk;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collections;

import static com.example.andrei.ode.LoginActivity.db;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    static Toolbar toolbar;

    static String Domain = "http://139.99.98.213";

    static long MyID = 0;
    static long MyIDVK = 0;
    static long MyPRating = 0;
    static long MyCRating = 0;
    static String MyToken = "";
    static String MyTokenVK = "";
    static String MyPhoto = "";
    static String MyFName = "";
    static String MyLName = "";

    public static boolean isOffline = false;

    static boolean isMainFragment = true;
    EditText edtSearch;
    static Activity MainContext;
    static MainActivity This;
    static NavigationView navigationView;

    @SuppressLint({"SetTextI18n", "StaticFieldLeak"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (isOffline) {
            new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... voids) {
                    try {
                        String s = MainActivity.doGet(MainActivity.Domain + "/auth_vk?token=" + LoginActivity.sharedPref.getString("user_token_vk", ""));

                        final JSONObject jsonObject = new JSONObject(s);
                        if (jsonObject.has("error")) {
                            LoginActivity.editor.putString("user_token", "");
                            LoginActivity.editor.commit();
                            Intent myIntent = new Intent(MainActivity.this, LoginActivity.class);
                            MainActivity.this.startActivity(myIntent);
                            MainActivity.this.finish();
                            return null;
                        }
                        MainActivity.MyID = jsonObject.getLong("main_id");
                        MainActivity.MyFName = jsonObject.getString("first_name");
                        MainActivity.MyLName = jsonObject.getString("last_name");
                        MainActivity.MyIDVK = jsonObject.getLong("id");
                        MainActivity.MyPhoto = jsonObject.getString("photo_200");
                        MainActivity.MyPRating = jsonObject.getLong("reputation");
                        MainActivity.MyCRating = jsonObject.getLong("creator_reputation");
                        MainActivity.MyToken = jsonObject.getString("token");
                        MainActivity.MyTokenVK = LoginActivity.sharedPref.getString("user_token_vk", "");

                        LoginActivity.editor.putString("user_token", jsonObject.getString("token"));
                        LoginActivity.editor.commit();

                        final User usr = new User();
                        usr.ID = MainActivity.MyID;
                        usr.Image = MainActivity.MyPhoto;
                        usr.FName = MainActivity.MyFName;
                        usr.LName = MainActivity.MyLName;
                        usr.VkID = String.valueOf(MainActivity.MyIDVK);
                        usr.CRating = MainActivity.MyCRating;
                        usr.Rating = MainActivity.MyPRating;

                        new AsyncTask<Void, Void, Void>() {
                            @Override
                            protected Void doInBackground(Void... voids) {
                                db.getUserDao().insert(usr);
                                return null;
                            }
                        }.execute();
                        User.Users.put(usr.ID, usr);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return null;
                }
            }.execute();
        }
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
        Event.Refresh(true);

        new URLImage((ImageView) navigationView.getHeaderView(0).findViewById(R.id.imageView)).execute(MyPhoto);
        ((TextView) navigationView.getHeaderView(0).findViewById(R.id.FLName)).setText(MyFName + " " + MyLName);
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
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                            != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                        1231);

                if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED ||
                        ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                                != PackageManager.PERMISSION_GRANTED)
                    return false;
            }
            fragmentClass = MapsFragment.class;
            fragmentTollBar = FirstFragmentT.class;
        } else if (id == R.id.nav_create_event) {
            fragmentClass = CreateEventFragment.class;
            fragmentTollBar = CreateEventFragmentT.class;
            CreateEventFragmentT.Label = "Создание мероприятия";
        } else if (id == R.id.nav_chat_location) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                            != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                        1231);

                if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED ||
                        ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                                != PackageManager.PERMISSION_GRANTED)
                    return false;
            }
            fragmentClass = ChatFragment.class;
            fragmentTollBar = ChatFragmentT.class;
        } else if (id == R.id.nav_show_qr) {
            fragmentClass = ShowQRFragment.class;
            fragmentTollBar = CreateEventFragmentT.class;
            CreateEventFragmentT.Label = "Регистрация места";
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

    public static void ToastError(String error) {
        try {
            JSONObject jsonObject = new JSONObject(error);
            switch (jsonObject.getInt("error")) {
                case 11:
                    error = "Сессия неверна.\nПопробуйте перезайти в приложение.";
                    break;
                case 12:
                    error = "Вы ввели неверные данные (" + jsonObject.getString("q") + ").";
                    break;
                case 13:
                    error = "Сессия неверна.\nПопробуйте перезайти в приложение.";
                    break;
                case 14:
                    error = "Отсутствует мерроприятие с данным идентификатором.";
                    break;
                case 15:
                    error = "Вы ввели неверное время (Возможно, вы ввели старую дату).";
                    break;
                case 16:
                    error = "Вы ввели неверные данные (" + jsonObject.getString("q") + ").";
                    break;
                case 21:
                    error = "Вы не обладаете необходимыми правами для выполнения данного действия.";
                    break;
                case 22:
                    error = "Вы попытались выполнить действие в неподходящее время.";
                    break;
                case 31:
                    error = jsonObject.getString("q") + ".";
                    break;
                default:
                    return;
            }
            Toast.makeText(MainContext, error, Toast.LENGTH_SHORT).show();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static String doGet(String url)
            throws Exception {

        URL obj = new URL(url);
        HttpURLConnection connection = (HttpURLConnection) obj.openConnection();

        //add reuqest header
        connection.setRequestMethod("GET");
        connection.setRequestProperty("User-Agent", "Mozilla/5.0");
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
        Log.d("111", "Response string: " + response.toString());


        return response.toString();
    }
}
