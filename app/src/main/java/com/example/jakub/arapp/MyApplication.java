package com.example.jakub.arapp;

import android.app.Application;

import com.example.jakub.arapp.application.AppComponent;
import com.example.jakub.arapp.application.AppModule;


import com.example.jakub.arapp.application.ConfigApp;
import com.example.jakub.arapp.application.DaggerAppComponent;
import com.example.jakub.arapp.auth.AuthActivity;
import com.example.jakub.arapp.dataBase.IoTDeviceDbModule;
import com.example.jakub.arapp.internet.api.InternetModule;


public class MyApplication extends Application {

    private AppComponent appComponent;

    public static final String TAG = AuthActivity.class.getSimpleName();


    @Override
    public void onCreate() {
        super.onCreate();
        appComponent = DaggerAppComponent.builder()
                .appModule(new AppModule(this))
                .ioTDeviceDbModule(new IoTDeviceDbModule(this))
                .internetModule(new InternetModule(ConfigApp.URL))
                .build();

    }



    public AppComponent getAppComponent(){
        return appComponent;
    }





}
