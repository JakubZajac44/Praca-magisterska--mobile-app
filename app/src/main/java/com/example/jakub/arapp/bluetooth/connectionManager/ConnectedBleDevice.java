package com.example.jakub.arapp.bluetooth.connectionManager;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.jakub.arapp.broadcastReceiver.bluetooth.BleConnectionListener;
import com.example.jakub.arapp.utility.Constants;
import com.example.jakub.arapp.utility.Ints;
import com.example.jakub.arapp.utility.Logger;
import com.polidea.rxandroidble2.RxBleDevice;

import java.util.UUID;

import io.reactivex.disposables.Disposable;
import lombok.Getter;

public class ConnectedBleDevice {

    public static final String TAG = ConnectedBleDevice.class.getSimpleName();

    private RxBleDevice rxBleDevice;
    @Getter
    private int state;
    private Disposable connection;
    private Disposable connectionState;
    private Logger logger;
    private Context context;

    @Getter
    private String address;

    public ConnectedBleDevice() {
        logger = new Logger();
    }


    public ConnectedBleDevice(RxBleDevice rxBleDevice, Context context) {
        this.rxBleDevice = rxBleDevice;
        this.address = rxBleDevice.getMacAddress();
        logger = new Logger();
        this.monitorStatusConnection();
        this.context = context;
        this.state = Constants.UNKNOWN_STATUS;
    }

    private void monitorStatusConnection() {
        String address = rxBleDevice.getMacAddress();
        connectionState = rxBleDevice.observeConnectionStateChanges()
                .subscribe(
                        connectionState -> {
                            Log.i(TAG, "Status changed: " + connectionState.name() + ", device name: " + rxBleDevice.getName());
                            switch (connectionState.name()) {
                                case Constants.CONNECTED:
                                    this.state = Constants.CONNECTED_STATUS;
                                    this.sendBroadcast(ConnectedBleDeviceProvider.ACTION_CONNECTED_DEVICE, address);
                                    break;
                                case Constants.DISCONNECTED:
                                    this.state = Constants.DISCONNECTED_STATUS;
                                    this.sendBroadcast(ConnectedBleDeviceProvider.ACTION_DISCONNECTED_DEVICE, address);
                                    break;
                                default:
                                    this.state = Constants.DISCONNECTED_STATUS;
                                    this.sendBroadcast(ConnectedBleDeviceProvider.ACTION_DISCONNECTED_DEVICE, address);
                                    break;
                            }
                        },
                        throwable -> logger.log(TAG, throwable.getMessage())
                );
    }

    private void sendBroadcast(String action, String address) {
        Intent intent = new Intent(action);
        intent.putExtra(ConnectedBleDeviceProvider.ADDRESS_BLE_DEVICE_KEY, address);
        context.sendBroadcast(intent);
    }

    private void sendBroadcast(String action, String address, String data) {
        Intent intent = new Intent(action);
        intent.putExtra(ConnectedBleDeviceProvider.ADDRESS_BLE_DEVICE_KEY, address);
        intent.putExtra(ConnectedBleDeviceProvider.NEW_DATA_KEY, data);
        context.sendBroadcast(intent);
    }

    public void establishConnection() {
        connection = rxBleDevice.establishConnection(false)
                .flatMap(rxBleConnection -> rxBleConnection.setupNotification(UUID.fromString("00001111-0000-1000-8000-00805f9b34fb")))
                .doOnNext(notificationObservable -> {
                })
                .flatMap(notificationObservable -> notificationObservable)
                .subscribe(
                        bytes -> sendBroadcast(ConnectedBleDeviceProvider.ACTION_NEW_DATA_AVAILABLE, rxBleDevice.getMacAddress(),
                                String.valueOf(Ints.convertDataToInt(bytes))),
                        throwable ->
                                logger.log(TAG, throwable.getCause() + " " + throwable.getMessage())

                );


    }

    public void removeConnection() {
        connection.dispose();
        connectionState.dispose();
    }


}
