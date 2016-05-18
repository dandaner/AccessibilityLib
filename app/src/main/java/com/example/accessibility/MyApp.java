package com.example.accessibility;

import android.app.Application;

import com.example.accessibility.base.AccessibilityService;

/**
 * author: demon.zhang
 * time: 16/4/29 下午6:27
 */
public class MyApp extends Application {

    public static AccessibilityService sService;

    @Override
    public void onCreate() {
        super.onCreate();
        sService = new AccessibilityService(this);
    }
}
