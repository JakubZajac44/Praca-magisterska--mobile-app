package com.example.jakub.arapp.broadcastReceiver.bluetooth;

public interface BleConnectionListener {

    void changeBleDeviceConnectionStatus(String address, int status);

    void changeBleDeviceData(String address, String data);
}
