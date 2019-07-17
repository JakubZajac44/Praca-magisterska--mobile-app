package com.example.jakub.arapp.internetManager.apiConnection;

import com.example.jakub.arapp.model.device.InternetDeviceWrapper;

import java.util.List;

public interface ApiConnectionListener {

    void internetDeviceLoaded(List<InternetDeviceWrapper> internetDeviceWrapperList);
    void internetDeviceLoadedError(String errorMassage);
}
