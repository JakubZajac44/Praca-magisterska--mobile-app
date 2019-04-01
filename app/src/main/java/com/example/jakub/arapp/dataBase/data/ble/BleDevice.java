package com.example.jakub.arapp.dataBase.data.ble;

import com.example.jakub.arapp.model.device.BleDeviceWrapper;

import javax.annotation.Nullable;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "ble_device")
public class BleDevice {

    @NonNull
    @PrimaryKey()
    private String address;

    @Nullable
    private String name;

    public BleDevice() {
    }

    @Ignore
    public BleDevice(@Nullable String address, @Nullable String name) {
        this.address = address;
        this.name = name;
    }

    public BleDevice(BleDeviceWrapper deviceWrapper) {
        this.address = deviceWrapper.getAddress();
        this.name = deviceWrapper.getName();
    }

    @Nullable
    public String getAddress() {
        return address;
    }

    public void setAddress(@Nullable String address) {
        this.address = address;
    }

    @Nullable
    public String getName() {
        return name;
    }

    public void setName(@Nullable String name) {
        this.name = name;
    }

}
