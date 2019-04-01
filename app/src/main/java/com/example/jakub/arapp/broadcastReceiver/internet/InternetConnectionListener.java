package com.example.jakub.arapp.broadcastReceiver.internet;

import com.example.jakub.arapp.model.device.InternetDeviceWrapper;

import java.util.List;

public interface InternetConnectionListener {

    void internetDeviceLoaded(List<InternetDeviceWrapper> internetDeviceWrapperList);
        void internetDeviceLoadedError(String errorMassage);
}
