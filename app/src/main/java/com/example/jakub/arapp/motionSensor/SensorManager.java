package com.example.jakub.arapp.motionSensor;

import com.example.jakub.arapp.motionSensor.sensorUtility.Orientation3d;

import dagger.Binds;
import dagger.Module;
import io.reactivex.Observer;


public interface SensorManager {
    void setListener(Observer<Orientation3d> observer);
    void removeListener();
    void actualizeDevicePosition(Orientation3d orientation3d);

    @Module
    abstract class MySensorManagerModule {
        @Binds
        public abstract SensorManager mySensorManagerProvider(SensorManagerImpl mySensorManager);
    }
}
