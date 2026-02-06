package com.example.exergen.application;

import android.app.Application;

public class ExerGenApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        AppBootstrap.init(this);
    }
}
