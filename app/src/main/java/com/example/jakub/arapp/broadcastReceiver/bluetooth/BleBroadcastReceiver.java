package com.example.jakub.arapp.broadcastReceiver.bluetooth;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import com.example.jakub.arapp.bluetooth.connectionManager.ConnectedBleDeviceProvider;
import com.example.jakub.arapp.utility.Constants;
import com.example.jakub.arapp.utility.Logger;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class BleBroadcastReceiver extends BroadcastReceiver {

    public static final String TAG = BleBroadcastReceiver.class.getSimpleName();

    private BleConnectionListener listener;

    @Inject
    Logger logger;

    @Inject
    public BleBroadcastReceiver() {
    }


    public void setUpListener(BleConnectionListener listener){
        this.listener = listener;
    }
    public void removeListener(){
        this.listener = null;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String address = intent.getStringExtra(ConnectedBleDeviceProvider.ADDRESS_BLE_DEVICE_KEY);
        switch (intent.getAction()){
            case ConnectedBleDeviceProvider.ACTION_CONNECTED_DEVICE:
                logger.log(TAG,"Device: "+address+", status: "+String.valueOf(Constants.CONNECTED_STATUS));
                this.changeConnectionStatus(address,Constants.CONNECTED_STATUS);
                break;
            case ConnectedBleDeviceProvider.ACTION_DISCONNECTED_DEVICE:
                logger.log(TAG,"Device: "+address+", status: "+String.valueOf(Constants.DISCONNECTED_STATUS));
                this.changeConnectionStatus(address,Constants.DISCONNECTED_STATUS);
                break;
            case ConnectedBleDeviceProvider.ACTION_RECONNECTING_DEVICE:
                logger.log(TAG,"Device: "+address+", status: "+String.valueOf(Constants.UNKNOWN_STATUS));
                this.changeConnectionStatus(address,Constants.UNKNOWN_STATUS);
                break;
            case ConnectedBleDeviceProvider.ACTION_NEW_DATA_AVAILABLE:
                String data = intent.getStringExtra(ConnectedBleDeviceProvider.NEW_DATA_KEY);
                logger.log(TAG,"Device: "+address+", value: "+data);
                break;
        }
    }

    public static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ConnectedBleDeviceProvider.ACTION_CONNECTED_DEVICE);
        intentFilter.addAction(ConnectedBleDeviceProvider.ACTION_DISCONNECTED_DEVICE);
        intentFilter.addAction(ConnectedBleDeviceProvider.ACTION_RECONNECTING_DEVICE);
        intentFilter.addAction(ConnectedBleDeviceProvider.ACTION_NEW_DATA_AVAILABLE);
        return intentFilter;
    }

    public void changeConnectionStatus(String address, int status){
        if(listener!=null){
            listener.changeDeviceConnectionStatus(address,status);
        }
    }
}
