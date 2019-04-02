package com.example.jakub.arapp.broadcastReceiver.internet;

import com.example.jakub.arapp.model.device.InternetDeviceWrapper;

import java.util.List;

public interface InternetConnectionListener {

    void internetDeviceLoadedReceive(List<InternetDeviceWrapper> internetDeviceWrapperList);
    void internetDeviceLoadedErrorReceive(String errorMassage);
}
