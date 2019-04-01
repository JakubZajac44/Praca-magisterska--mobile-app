package com.example.jakub.arapp.bluetooth.connectionManager;

import com.polidea.rxandroidble2.RxBleDevice;

import dagger.Binds;
import dagger.Module;

public interface ConnectedBleDeviceProvider {

    String ACTION_CONNECTED_DEVICE =
            "com.example.jakub.arapp.service.ACTION_CONNECTED_DEVICE";
    String ACTION_DISCONNECTED_DEVICE =
            "com.example.jakub.arapp.service.ACTION_DISCONNECTED_DEVICE";
    String ACTION_RECONNECTING_DEVICE =
            "com.example.jakub.arapp.service.ACTION_RECONNECTING_DEVICE";
    String ACTION_NEW_DATA_AVAILABLE =
            "com.example.jakub.arapp.service.ACTION_NEW_DATA_AVAILABLE";
    String ADDRESS_BLE_DEVICE_KEY =
            "Address key";
    String NEW_DATA_KEY =
            "New data key";


    void addNewDevice(ConnectedBleDevice device);

    boolean checkIfNotExist(RxBleDevice device);

    void establishConnection(ConnectedBleDevice connectedBleDevice);

    void reestablishConnection(String address);

    void removeAllConnection();

    int getStatus(String address);

    void removeDevice(String address);

    @Module()
    abstract class ConnectedBleDeviceProviderModule {

        @Binds
        public abstract ConnectedBleDeviceProvider connectionProvider(ConnectedBleDeviceProviderImpl connectedBleDeviceProvider);
    }
}
