package com.example.jakub.arapp.utility;

import android.location.Location;
import android.util.Log;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class Logger {

    @Inject
    public Logger(){

    }

    private static final String TAG = Logger.class.getSimpleName();

    public void log(String TAG, int number) {
        Log.i(TAG, Integer.toString(number));
    }

    public void log(String TAG, boolean status) {
        Log.i(TAG, Boolean.toString(status));
    }

    public void log(String TAG, float numberFloat) {
        Log.i(TAG, Float.toString(numberFloat));
    }

    public void log(String TAG, Double numberDouble) {
        Log.i(TAG, Double.toString(numberDouble));
    }

    public void log(String TAG, Location location) {
        Log.i(TAG, "Lat: "+location.getLatitude()+", long: "+location.getLongitude());
    }

    public void log(String TAG, String log) {
        Log.i(TAG, log);
    }

}
