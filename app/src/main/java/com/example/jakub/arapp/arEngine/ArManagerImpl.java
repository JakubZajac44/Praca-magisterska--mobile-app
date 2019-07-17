package com.example.jakub.arapp.arEngine;

import android.content.Context;
import android.location.Location;
import android.widget.Toast;

import com.example.jakub.arapp.arEngine.openGLprovider.MyRender;
import com.example.jakub.arapp.arEngine.shape.InternetDeviceShape;
import com.example.jakub.arapp.arEngine.shape.IotDeviceShape;
import com.example.jakub.arapp.broadcastReceiver.bluetooth.BleBroadcastReceiver;
import com.example.jakub.arapp.broadcastReceiver.bluetooth.BleConnectionListener;
import com.example.jakub.arapp.broadcastReceiver.gps.GpsBroadcastReceiver;
import com.example.jakub.arapp.broadcastReceiver.gps.GpsLocationListener;
import com.example.jakub.arapp.broadcastReceiver.internet.InternetBroadcastReceiver;
import com.example.jakub.arapp.broadcastReceiver.internet.InternetConnectionListener;
import com.example.jakub.arapp.model.IoTDeviceDetails;
import com.example.jakub.arapp.model.device.InternetDeviceWrapper;
import com.example.jakub.arapp.model.device.IoTDevice;
import com.example.jakub.arapp.utility.Logger;
import com.example.jakub.arapp.utility.MathOperation;

import java.util.List;

import javax.inject.Inject;

public class ArManagerImpl implements ArManager, GpsLocationListener, InternetConnectionListener, BleConnectionListener, IotTouchListener {
    public static final String TAG = ArManagerImpl.class.getSimpleName();

    @Inject
    GpsBroadcastReceiver gpsBroadcastReceiver;
    @Inject
    InternetBroadcastReceiver internetBroadcastReceiver;
    @Inject
    BleBroadcastReceiver bleBroadcastReceiver;
    @Inject
    Logger logger;
    @Inject
    Context context;

    private MyRender myRender;
    private IotTouchListener listener;

    @Inject
    public ArManagerImpl() {
    }

    @Override
    public void setupListener(IotTouchListener listener) {
        this.listener = listener;
        gpsBroadcastReceiver.setUpListener(this);
        internetBroadcastReceiver.setUpListener(this);
        bleBroadcastReceiver.setUpListener(this);
    }

    @Override
    public void removeListener() {
        listener = null;
        gpsBroadcastReceiver.removeListener();
        internetBroadcastReceiver.removeListener();
        bleBroadcastReceiver.removeListener();
        myRender.removeListener();
    }

    @Override
    public void setPosition(float azimuthDegrees, float pitchDegrees) {
        myRender.setX(azimuthDegrees);
        myRender.setY(360 - pitchDegrees);
    }

    @Override
    public MyRender createRender(List<IoTDevice> ioTDevices) {
        myRender = new MyRender(ioTDevices, context);
        myRender.setUpListener(this);
        return myRender;
    }

    // location listener
    @Override
    public void changeGpsLocation(Location location) {
        logger.log(TAG, "Position changed");
        myRender.updateUserLocation(location);
    }

    // internet device listener
    @Override
    public void internetDeviceLoadedReceive(List<InternetDeviceWrapper> internetDeviceWrapperList) {
        logger.log(TAG, "Loaded internet device, size: " + String.valueOf(internetDeviceWrapperList.size()));
        myRender.updateInternetDevice(internetDeviceWrapperList);
    }

    @Override
    public void internetDeviceLoadedErrorReceive(String errorMassage) {
        logger.log(TAG, errorMassage);
        myRender.setAllInternetDeviceOffline();
    }

    // ble device listener
    @Override
    public void changeBleDeviceConnectionStatus(String address, int status) {
        logger.log(TAG, "Change device status: " + String.valueOf(status) + ", address: " + address);
        myRender.updateBleDeviceStatus(address, status);
    }

    @Override
    public void changeBleDeviceData(String address, IoTDeviceDetails deviceDetails) {
        logger.log(TAG, "Change device data: " + String.valueOf(address) + ", data: " + deviceDetails.getData());
        myRender.updateBleDeviceData(address, deviceDetails);
    }

    @Override
    public void iotDeviceTouched(IotDeviceShape device) {
        if(listener!=null)listener.iotDeviceTouched(device);
    }
}
