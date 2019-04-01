package com.example.jakub.arapp.bluetooth.scanningManager;

import com.example.jakub.arapp.model.device.BleDeviceWrapper;

import java.util.List;

public interface BluetoothScanningListener {

    void BleDeviceListInvalidate(List<BleDeviceWrapper> bluetoothDeviceList);
}
