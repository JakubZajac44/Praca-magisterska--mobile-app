package com.example.jakub.arapp.motionSensor.gyroFilter;

import android.hardware.SensorEvent;

import com.example.jakub.arapp.motionSensor.MySensorManager;

import dagger.Binds;
import dagger.Module;

public interface GyroscopeFilterProvider {

    void setListener(MySensorManager listener);
    void gyroFunction(SensorEvent event, float[] accMagOrientation);

    @Module
    abstract class GyroscopeFilterModule {
        @Binds
        public abstract GyroscopeFilterProvider gyroscopeFilterProvider(GyroscopeFilterProviderImpl gyroscopeFilterProviderImpl);
    }
}
