package com.example.jakub.arapp.broadcastReceiver.bluetooth;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import com.example.jakub.arapp.bluetoothManager.connectionManager.ConnectedBleDeviceProvider;
import com.example.jakub.arapp.model.IoTDeviceDetails;
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
                IoTDeviceDetails iotDevice = this.extractDetails(data);
                this.changeBleDeviceData(address,iotDevice);
                logger.log(TAG,"Device: "+address+", value: "+data);

                break;
        }
    }

    private IoTDeviceDetails extractDetails(String data) {
        logger.log(TAG,data);
        IoTDeviceDetails deviceDetails = new IoTDeviceDetails();
        deviceDetails.setAzimuth(Float.parseFloat(data.substring(0,3)));
        deviceDetails.setPitch(Float.parseFloat(data.substring(3,6)));
        deviceDetails.setType(Integer.parseInt(data.substring(7,8)));

        String dataNotExtracted = data.substring(9);
        String dataExtracted="";
        int length = dataNotExtracted.length();
        int numberAfterDot = Integer.parseInt(data.substring(8,9));
        if(numberAfterDot>0){
            dataExtracted = dataNotExtracted.substring(0,length-numberAfterDot)+"."+dataNotExtracted.substring(length-numberAfterDot,length);
        }else {
            dataExtracted = dataNotExtracted;
        }

        switch (deviceDetails.getType()) {
            case Constants.BLE_DEVICE_TERMOMETER:
                dataExtracted += " C";
                break;
            case Constants.BLE_DEVICE_BAROMETER:
                dataExtracted += " hPa";
                break;
            case Constants.BLE_DEVICE_AIR:
                dataExtracted += " %";
                break;
        }


        deviceDetails.setData(dataExtracted);
        //TODO tutaj odczytac typ koordynaty
        return deviceDetails;

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
            listener.changeBleDeviceConnectionStatus(address,status);
        }
    }

    public void changeBleDeviceData(String address, IoTDeviceDetails ioTDeviceDetails){
        if(listener!=null){
            listener.changeBleDeviceData(address,ioTDeviceDetails);
        }
    }
}
