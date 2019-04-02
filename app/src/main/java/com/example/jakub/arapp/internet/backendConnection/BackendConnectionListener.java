package com.example.jakub.arapp.internet.backendConnection;

import com.example.jakub.arapp.model.device.InternetDeviceWrapper;

import java.util.List;

public interface BackendConnectionListener {

    void internetDeviceLoaded(List<InternetDeviceWrapper> internetDeviceWrapperList);
    void internetDeviceLoadedError(String errorMassage);
}
