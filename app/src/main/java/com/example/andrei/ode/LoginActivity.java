package com.example.andrei.ode;

import android.annotation.SuppressLint;
import android.arch.persistence.room.Room;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.vk.sdk.VKAccessToken;
import com.vk.sdk.VKCallback;
import com.vk.sdk.VKScope;
import com.vk.sdk.VKSdk;
import com.vk.sdk.api.VKError;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public class LoginActivity extends AppCompatActivity {
    static SharedPreferences sharedPref;
    static SharedPreferences.Editor editor;
    static Context context;
    static AppDatabase db;
    static public List<Bitmap> Types = new ArrayList<>();

    @SuppressLint({"StaticFieldLeak", "CommitPrefEdits"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        sharedPref = getSharedPreferences("myPref", Context.MODE_PRIVATE);
        editor = sharedPref.edit();
        setContentView(R.layout.activity_login);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Objects.requireNonNull(getSupportActionBar()).setTitle("Авторизация");
        }

        Types.add(BitmapFactory.decodeResource(context.getResources(),
                R.drawable.sport));
        Types.add(BitmapFactory.decodeResource(context.getResources(),
                R.drawable.teatre));
        Types.add(BitmapFactory.decodeResource(context.getResources(),
                R.drawable.cinema));
        Types.add(BitmapFactory.decodeResource(context.getResources(),
                R.drawable.sales));
        Types.add(BitmapFactory.decodeResource(context.getResources(),
                R.drawable.other));
        Types.add(BitmapFactory.decodeResource(context.getResources(),
                R.drawable.museum));
        Types.add(BitmapFactory.decodeResource(context.getResources(),
                R.drawable.lecture));
        Types.add(BitmapFactory.decodeResource(context.getResources(),
                R.drawable.fair));
        Types.add(BitmapFactory.decodeResource(context.getResources(),
                R.drawable.concert));

        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... voids) {
                try {
                    db = Room.databaseBuilder(getApplicationContext(),
                            AppDatabase.class, "database").build();

                    Event.Events = db.getEventDao().getAll();
                    Collections.sort(Event.Events);
                    for (int i = 0; i < Event.Events.size(); i++) {
                        Event.EventsID.put(Event.Events.get(i).Id, i);
                    }

                    List<User> usrs = db.getUserDao().getAll();
                    for (User i : usrs) {
                        User.Users.put(i.ID, i);
                    }

                    if (!sharedPref.getString("user_token_vk", "").equals("")) {
                        if (!sharedPref.getString("user_token", "").equals("")) {
                            if (!sharedPref.getString("user_id", "").equals("")) {
                                Long Id = Long.parseLong(sharedPref.getString("user_id", ""));
                                if (User.Users.containsKey(Id)) {
                                    MainActivity.isOffline = true;
                                    MainActivity.MyID = User.Users.get(Id).ID;
                                    MainActivity.MyFName = User.Users.get(Id).FName;
                                    MainActivity.MyLName = User.Users.get(Id).LName;
                                    MainActivity.MyIDVK = Long.parseLong(User.Users.get(Id).VkID);
                                    MainActivity.MyPhoto = User.Users.get(Id).Image;
                                    MainActivity.MyPRating = User.Users.get(Id).Rating;
                                    MainActivity.MyCRating = User.Users.get(Id).CRating;
                                    MainActivity.MyToken = sharedPref.getString("user_token", "");
                                    MainActivity.MyTokenVK = sharedPref.getString("user_token_vk", "");


                                    Intent myIntent = new Intent(LoginActivity.this, MainActivity.class);
                                    LoginActivity.this.startActivity(myIntent);
                                    LoginActivity.this.finish();
                                }
                            }
                        }
                    }
                } catch (Exception e) {
                    Log.e("AUTH", e.getMessage());
                }
                return null;
            }
        }.execute();
        VKSdk.login(this, VKScope.OFFLINE);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (!VKSdk.onActivityResult(requestCode, resultCode, data, new VKCallback<VKAccessToken>() {
            @SuppressLint("StaticFieldLeak")
            @Override
            public void onResult(VKAccessToken res) {
                final VKAccessToken fres = res;
                new AsyncTask<Void, String, String>() {
                    @Override
                    protected String doInBackground(Void... voids) {
                        String s = "";
                        try {
                            s = MainActivity.doGet(MainActivity.Domain + "/auth_vk?token=" + fres.accessToken);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        return s;
                    }

                    @Override
                    protected void onPostExecute(final String result) {
                        try {
                            final JSONObject jsonObject = new JSONObject(result);
                            if (jsonObject.has("error")) {
                                VKSdk.login(LoginActivity.this, VKScope.OFFLINE);
                            }
                            MainActivity.MyID = jsonObject.getLong("main_id");
                            MainActivity.MyFName = jsonObject.getString("first_name");
                            MainActivity.MyLName = jsonObject.getString("last_name");
                            MainActivity.MyIDVK = jsonObject.getLong("id");
                            MainActivity.MyPhoto = jsonObject.getString("photo_200");
                            MainActivity.MyPRating = jsonObject.getLong("reputation");
                            MainActivity.MyCRating = jsonObject.getLong("creator_reputation");
                            MainActivity.MyToken = jsonObject.getString("token");
                            MainActivity.MyTokenVK = fres.accessToken;

                            final User usr = new User();
                            usr.ID = MainActivity.MyID;
                            usr.Image = MainActivity.MyPhoto;
                            usr.FName = MainActivity.MyFName;
                            usr.LName = MainActivity.MyLName;
                            usr.VkID = String.valueOf(MainActivity.MyIDVK);
                            usr.CRating = MainActivity.MyCRating;
                            usr.Rating = MainActivity.MyPRating;

                            LoginActivity.editor.putString("user_id", jsonObject.getString("main_id"));
                            LoginActivity.editor.commit();
                            LoginActivity.editor.putString("user_token_vk", fres.accessToken);
                            LoginActivity.editor.commit();
                            LoginActivity.editor.putString("user_token", MainActivity.MyToken);
                            LoginActivity.editor.commit();

                            User.Users.put(usr.ID, usr);

                            new AsyncTask<Void, Void, Void>() {
                                @Override
                                protected Void doInBackground(Void... voids) {
                                    db.getUserDao().insert(usr);
                                    return null;
                                }
                            }.execute();

                            Intent myIntent = new Intent(LoginActivity.this, MainActivity.class);
                            LoginActivity.this.startActivity(myIntent);
                            LoginActivity.this.finish();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }.execute();
            }

            @Override
            public void onError(VKError error) {
                VKSdk.login(LoginActivity.this, VKScope.OFFLINE);
            }
        })) {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}
