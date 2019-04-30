package com.example.jakub.arapp.internet.ApiConnection;

import dagger.Binds;
import dagger.Module;

public interface ApiConnectionProvider {


    String ACTION_INTERNET_DEVICE_LOADED =
            "com.example.jakub.arapp.service.ACTION_INTERNET_DEVICE_LOADED";

    String ACTION_INTERNET_DEVICE_ERROR =
            "com.example.jakub.arapp.service.ACTION_INTERNET_DEVICE_ERROR";

    String KEY_INTERNET_DEVICE_LOADED =
            "com.example.jakub.arapp.service.KEY_INTERNET_DEVICE_LOADED";

    String KEY_INTERNET_DEVICE_ERROR =
            "com.example.jakub.arapp.service.KEY_INTERNET_DEVICE_ERROR";

    void getInternetDevice();

    void setUpListener(ApiConnectionListener listener);

    void removeListener();


    @Module()
    abstract class BackendConnectionModule {

        @Binds
        public abstract ApiConnectionProvider connectionProvider(ApiConnectionProviderImpl connectionProvider);
    }
}
