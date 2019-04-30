package com.example.jakub.arapp.page.mapPage;

import android.location.Location;

import com.example.jakub.arapp.model.device.InternetDeviceWrapper;

import java.util.List;

import dagger.Binds;
import dagger.Module;

public interface MapContract {
    interface Presenter{
        void attachView(MapContract.View view);
        void detachView();
        void saveDeviceInCircle(int currentRadius, Location currentCircleLocation);
    }

    interface View{

        void changeUserLocation(Location location);
        void showMessage(String message);

        void addInternetDeviceMarker(List<InternetDeviceWrapper> internetDeviceWrappers);

        void removeAllInternetDeviceMarker();
    }

    @Module()
    abstract class MapModule {

        @Binds
        public abstract MapContract.Presenter provideMapPresenter (MapPresenter mapPresenter);
    }
}
