package com.example.jakub.arapp.model.device;

import android.bluetooth.BluetoothDevice;

import com.example.jakub.arapp.dataBase.data.ble.BleDevice;
import com.example.jakub.arapp.utility.Constants;

import lombok.Getter;
import lombok.Setter;

public class BleDeviceWrapper extends IoTDevice{

    @Getter
    @Setter
    private String address;

    @Getter
    @Setter
    private int type;


    private BluetoothDevice bluetoothDevice;

    public BleDeviceWrapper(BluetoothDevice device){
        this.bluetoothDevice = device;
        address = bluetoothDevice.getAddress();
        this.checkName();
        this.status = Constants.UNKNOWN_STATUS;
    }

    public BleDeviceWrapper(BleDevice device){
        this.status =Constants.CONNECTED_STATUS;
        this.name = device.getName();
        this.address = device.getAddress();
        this.sample = "BRAK DANYCH";
    }

    public BleDeviceWrapper(BleDevice device, int status){
        this.status =status;
        this.name = device.getName();
        this.address = device.getAddress();
        this.sample = "BRAK DANYCH";
    }

    private void checkName(){
        String name = "";
        if(bluetoothDevice.getName()==null){
            name = "UNKNOWN NAME";
        }else name = this.bluetoothDevice.getName();
        this.name = name;
    }

}
