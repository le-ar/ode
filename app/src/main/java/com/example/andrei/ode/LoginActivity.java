package com.example.andrei.ode;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
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

public class LoginActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

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
                            s = MainActivity.doGet(MainActivity.Domain+"/auth_vk?token=" + fres.accessToken);
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
