package com.example.jakub.arapp.dataBase.repository.ble;

import com.example.jakub.arapp.dataBase.repository.ble.BleDeviceRepository;
import com.example.jakub.arapp.model.device.BleDeviceWrapper;
import com.example.jakub.arapp.dataBase.data.ble.BleDevice;
import com.example.jakub.arapp.dataBase.data.ble.BleDeviceDao;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.Maybe;

@Singleton
public class BleDeviceRepositoryImpl implements BleDeviceRepository {

    private final BleDeviceDao bleDeviceDao;

    @Inject
    public BleDeviceRepositoryImpl(BleDeviceDao bleDeviceDao) {
        this.bleDeviceDao = bleDeviceDao;
    }

    @Override
    public Maybe<List<BleDevice>> getAllBleDevice() {
        return bleDeviceDao.getAllBleDevice();
    }

    @Override
    public void insertBleDevice(BleDeviceWrapper device) {
        BleDevice bleDevice = new BleDevice();
        bleDevice.setAddress(device.getAddress());
        bleDevice.setName(device.getName());
        bleDeviceDao.insert(bleDevice);
    }

    @Override
    public void insertBleDevices(List<BleDevice> device) {
        bleDeviceDao.insert(device);
    }

    @Override
    public void deleteDevices(List<BleDevice> devices) {
        bleDeviceDao.removeDevices(devices);
    }

    @Override
    public void deleteDevice(BleDevice device) {
         bleDeviceDao.remove(device);
    }


}
