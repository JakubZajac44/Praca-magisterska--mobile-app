package com.example.jakub.arapp.dataBase.repository.interent;

import com.example.jakub.arapp.dataBase.data.internet.InternetDevice;
import com.example.jakub.arapp.model.device.InternetDeviceWrapper;

import java.util.List;

import io.reactivex.Maybe;

public interface InternetDeviceRepository {

    Maybe<List<InternetDevice>> getAllInternetDevice();

    void insertInternetDevice(InternetDeviceWrapper device);

    void insertInternetDevices(List<InternetDeviceWrapper> devices);

    void deleteDevices(List<InternetDeviceWrapper> devices);

    void deleteDevice(InternetDeviceWrapper device);

    List<InternetDeviceWrapper> wrapListInternetDevice(List<InternetDevice> devices);
}
