package com.example.jakub.arapp.dataBase.repository.interent;

import android.util.Log;

import com.example.jakub.arapp.dataBase.data.internet.InternetDevice;
import com.example.jakub.arapp.dataBase.data.internet.InternetDeviceDao;
import com.example.jakub.arapp.model.device.InternetDeviceWrapper;
import com.example.jakub.arapp.utility.Constants;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.Maybe;

@Singleton
public class InternetDeviceRepositoryImpl implements InternetDeviceRepository {

public static final String TAG = InternetDeviceRepositoryImpl.class.getSimpleName();

    private final InternetDeviceDao internetDeviceDao;

    @Inject
    public InternetDeviceRepositoryImpl(InternetDeviceDao internetDeviceDao) {
        this.internetDeviceDao = internetDeviceDao;
    }

    @Override
    public Maybe<List<InternetDevice>> getAllInternetDevice() {
        return internetDeviceDao.getAllInternetDevice();
    }

    @Override
    public void insertInternetDevice(InternetDeviceWrapper device) {
        InternetDevice internetDevice = this.getDeviceFromWrapper(device);
        internetDeviceDao.insert(internetDevice);
    }

    @Override
    public void insertInternetDevices(List<InternetDeviceWrapper> devices) {
        List<InternetDevice> devicesToInsert = this.getDevicesListFromWrapperList(devices);

        internetDeviceDao.insert(devicesToInsert);
    }

    @Override
    public void deleteDevices(List<InternetDeviceWrapper> devices) {
        List<InternetDevice> devicesToInsert = this.getDevicesListFromWrapperList(devices);
        internetDeviceDao.removeDevices(devicesToInsert);
    }

    @Override
    public void deleteDevice(InternetDeviceWrapper device) {
        InternetDevice internetDevice = this.getDeviceFromWrapper(device);
        internetDeviceDao.remove(internetDevice);
    }

    @Override
    public void deleteAllDevice() {
        internetDeviceDao.remobeAll();
    }

    private InternetDevice getDeviceFromWrapper(InternetDeviceWrapper deviceWrapper) {
        InternetDevice internetDevice = new InternetDevice();
        internetDevice.setId(deviceWrapper.getId());
        internetDevice.setName(deviceWrapper.getName());
        internetDevice.setLat(deviceWrapper.getLat());
        internetDevice.setLon(deviceWrapper.getLon());
        internetDevice.setSample(deviceWrapper.getSample());
        internetDevice.setUpdateTime(deviceWrapper.getUpdatetime());
        return internetDevice;
    }

    private List<InternetDevice> getDevicesListFromWrapperList(List<InternetDeviceWrapper> wrappedDevices) {
        List<InternetDevice> devices = new ArrayList<>();
        for (InternetDeviceWrapper deviceWrapper : wrappedDevices) {
            InternetDevice device = this.getDeviceFromWrapper(deviceWrapper);
            devices.add(device);
        }
        return devices;
    }

    private InternetDeviceWrapper wrapInternetDevice(InternetDevice device) {
        InternetDeviceWrapper internetDeviceWrapper = new InternetDeviceWrapper();
        internetDeviceWrapper.setId(device.getId());
        internetDeviceWrapper.setName(device.getName());
        internetDeviceWrapper.setLat(device.getLat());
        internetDeviceWrapper.setLon(device.getLon());
        internetDeviceWrapper.setSample(device.getSample());
        internetDeviceWrapper.setStatus(Constants.UNKNOWN_STATUS);
        internetDeviceWrapper.setUpdatetime(device.getUpdateTime());
        return internetDeviceWrapper;
    }

    @Override
    public List<InternetDeviceWrapper> wrapListInternetDevice(List<InternetDevice> devices){
        List<InternetDeviceWrapper> wrappedDevices = new ArrayList<>();
        for (InternetDevice deviceWrapper : devices) {
            InternetDeviceWrapper device = this.wrapInternetDevice(deviceWrapper);
            wrappedDevices.add(device);
        }
        return wrappedDevices;
    }

}
