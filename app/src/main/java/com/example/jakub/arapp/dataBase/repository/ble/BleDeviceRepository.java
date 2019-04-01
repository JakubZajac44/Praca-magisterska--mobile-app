package com.example.jakub.arapp.dataBase.repository.ble;

import com.example.jakub.arapp.model.device.BleDeviceWrapper;
import com.example.jakub.arapp.dataBase.data.ble.BleDevice;

import java.util.List;

import io.reactivex.Maybe;


public interface BleDeviceRepository {

    Maybe<List<BleDevice>> getAllBleDevice();

    void insertBleDevice(BleDeviceWrapper device);

    void insertBleDevices(List<BleDevice> device);

    void deleteDevices(List<BleDevice> devices);

    void deleteDevice(BleDevice device);

}
