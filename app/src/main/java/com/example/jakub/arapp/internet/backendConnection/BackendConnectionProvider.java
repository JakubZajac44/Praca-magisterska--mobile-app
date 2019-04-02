package com.example.jakub.arapp.internet.backendConnection;

import com.example.jakub.arapp.broadcastReceiver.internet.InternetConnectionListener;

import dagger.Binds;
import dagger.Module;

public interface BackendConnectionProvider {


    String ACTION_INTERNET_DEVICE_LOADED =
            "com.example.jakub.arapp.service.ACTION_INTERNET_DEVICE_LOADED";

    String ACTION_INTERNET_DEVICE_ERROR =
            "com.example.jakub.arapp.service.ACTION_INTERNET_DEVICE_ERROR";

    String KEY_INTERNET_DEVICE_LOADED =
            "com.example.jakub.arapp.service.KEY_INTERNET_DEVICE_LOADED";

    String KEY_INTERNET_DEVICE_ERROR =
            "com.example.jakub.arapp.service.KEY_INTERNET_DEVICE_ERROR";

    void getInternetDevice();
    void setUpListener(BackendConnectionListener listener);
    void removeListener();


    @Module()
    abstract class BackendConnectionModule {

        @Binds
        public abstract BackendConnectionProvider connectionProvider (BackendConnectionProviderImpl connectionProvider);
    }
}
