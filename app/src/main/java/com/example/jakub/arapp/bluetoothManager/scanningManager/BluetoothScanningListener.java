package com.example.jakub.arapp.bluetoothManager.scanningManager;

import com.example.jakub.arapp.model.device.BleDeviceWrapper;

import java.util.List;

public interface BluetoothScanningListener {

    void bleDeviceListInvalidate(List<BleDeviceWrapper> bluetoothDeviceList);
}
