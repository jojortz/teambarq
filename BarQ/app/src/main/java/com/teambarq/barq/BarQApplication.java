package com.teambarq.barq;

import android.app.Application;

import com.firebase.client.Firebase;

/**
 * Created by jojortz on 5/6/2016.
 */
public class BarQApplication extends Application {
        @Override
        public void onCreate() {
            super.onCreate();
            Firebase.setAndroidContext(this);
        }
}
