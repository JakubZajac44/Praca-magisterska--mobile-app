package com.example.jakub.arapp.arEngine.shape;

import android.content.Context;

import com.example.jakub.arapp.model.device.BleDeviceWrapper;
import com.example.jakub.arapp.utility.Constants;

import lombok.Getter;

public class BleDeviceShape extends IotDeviceShape {

    @Getter
    private String address;

    public BleDeviceShape(BleDeviceWrapper device, Context context) {
        super(0,0, device.getName(),device.getSample(),device.getStatus(),context, Constants.BLE_DEVICE);
        this.address = device.getAddress();
    }


}
