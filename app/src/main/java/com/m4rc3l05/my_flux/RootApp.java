package com.m4rc3l05.my_flux;

import android.app.Application;

import com.google.firebase.database.FirebaseDatabase;

public class RootApp extends Application {
    public RootApp() { }

    @Override
    public void onCreate() {
        super.onCreate();
        System.out.println("Only once");
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }
}
