package com.example.jakub.arapp.broadcastReceiver.bluetooth;

import com.example.jakub.arapp.model.IoTDeviceDetails;

public interface BleConnectionListener {

    void changeBleDeviceConnectionStatus(String address, int status);

    void changeBleDeviceData(String address, IoTDeviceDetails ioTDeviceDetails);
}
