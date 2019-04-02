package com.example.jakub.arapp.bluetooth.connectionManager;

import com.example.jakub.arapp.utility.Constants;
import com.example.jakub.arapp.utility.Logger;
import com.polidea.rxandroidble2.RxBleDevice;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;


@Singleton
public class ConnectedBleDeviceProviderImpl implements ConnectedBleDeviceProvider{

    public static final String TAG = ConnectedBleDeviceProviderImpl.class.getSimpleName();

    private List<ConnectedBleDevice> connectedBleDevices;

    @Inject
    Logger logger;

    @Inject
    public ConnectedBleDeviceProviderImpl() {
        connectedBleDevices = Collections.synchronizedList(new ArrayList<>());
    }

    public void addNewDevice(ConnectedBleDevice device) {
        connectedBleDevices.add(device);
        logger.log(TAG, connectedBleDevices.size());
    }

    public void removeDevice(String address) {
        for (ConnectedBleDevice device : connectedBleDevices)
            if (device.getAddress().equals(address)) {
                device.removeConnection();
                connectedBleDevices.remove(device);
                break;
            }
    }

    public boolean checkIfNotExist(RxBleDevice device) {
        for (ConnectedBleDevice connectedBleDevice : connectedBleDevices) {
            if (connectedBleDevice.getAddress().equals(device.getMacAddress()))
                return false;
        }
        return true;
    }

    public void establishConnection(ConnectedBleDevice connectedBleDevice) {
        connectedBleDevice.establishConnection();
    }

    public void reestablishConnection(String address) {
        for (ConnectedBleDevice connectedBleDevice : connectedBleDevices) {
            if (connectedBleDevice.getAddress().equals(address)) connectedBleDevice.establishConnection();
        }
    }

    public void removeAllConnection() {
        for(ConnectedBleDevice connectedBleDevice : connectedBleDevices) connectedBleDevice.removeConnection();
    }

    public int getStatus(String address){
        for(ConnectedBleDevice connectedBleDevice : connectedBleDevices){
            if(connectedBleDevice.getAddress().equals(address)){
                return connectedBleDevice.getState();
            }
        }
        return Constants.UNKNOWN_STATUS;
    }
}
