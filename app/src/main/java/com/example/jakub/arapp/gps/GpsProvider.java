package com.example.jakub.arapp.gps;

import android.location.Location;

import dagger.Binds;
import dagger.Module;
import io.reactivex.Observer;

public interface GpsProvider {

    String ACTION_LOCATION_CHANGED =
            "com.example.jakub.arapp.service.ACTION_LOCATION_CHANGED";

    String KEY_LOCATION_CHANGED =
            "com.example.jakub.arapp.service.KEY_LOCATION_CHANGED";

    void setGpsListener(Observer<Location> observer);
    void removeGpsListener();


    @Module()
     abstract class GpsProviderModule {

          @Binds
          public abstract GpsProvider provideGpsManager (GpsProviderImpl gpsManager);
     }


}
