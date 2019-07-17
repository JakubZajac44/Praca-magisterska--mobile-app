package com.example.jakub.arapp.arEngine;

import com.example.jakub.arapp.arEngine.openGLprovider.MyRender;
import com.example.jakub.arapp.model.device.IoTDevice;

import java.util.List;

import dagger.Binds;
import dagger.Module;

public interface ArManager {

    void setupListener(IotTouchListener listener);

    void removeListener();

    void setPosition(float v, float v1);

    MyRender createRender(List<IoTDevice> iotDevices);

    @Module()
    abstract class ArManagerProvider {

        @Binds
        public abstract ArManager arManagerProvider(ArManagerImpl arManager);
    }
}
