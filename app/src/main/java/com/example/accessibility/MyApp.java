package com.example.accessibility;

import android.app.Application;

import com.example.accessibility.acc.AccManagerService;

/**
 * author: demon.zhang
 * time: 16/4/29 下午6:27
 */
public class MyApp extends Application {

    public static AccManagerService sService;

    @Override
    public void onCreate() {
        super.onCreate();
        sService = new AccManagerService(this);
    }
}
