package com.example.jakub.arapp.broadcastReceiver.bluetooth;

public interface BleConnectionListener {

    void changeDeviceConnectionStatus(String address, int status);

    void changeDeviceData(String address,String data);
}
