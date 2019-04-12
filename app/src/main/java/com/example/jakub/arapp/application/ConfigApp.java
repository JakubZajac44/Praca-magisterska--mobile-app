package com.example.jakub.arapp.application;

import android.Manifest;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class ConfigApp {
    public static final String PERMISSION_KEY = "permission_key";
    public static final String CIRCLE_MAP_KEY = "ratio_map_key";


    public static int PROFILE_TEST = 2;
    public static int PROFILE_DEV = 1;
    public static int PROFILE_PROD = 0;

    public static String URL = "http://192.168.0.143:8081/";


    private int PROFILE ;

    private final String[] permissions = new String[]{
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.CAMERA
    };

    @Inject
    public ConfigApp() {
        PROFILE = PROFILE_TEST;
    }

    public int getProfile(){
        return PROFILE;
    }

    public String[] getPermissions() {
        return permissions;
    }

}
