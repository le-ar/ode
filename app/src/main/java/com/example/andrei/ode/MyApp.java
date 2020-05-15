package com.example.andrei.ode;

import android.app.Application;

import com.vk.sdk.VKSdk;

public class MyApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        VKSdk.initialize(this);

    }
}
