package com.example.jakub.arapp.dataBase;


import com.example.jakub.arapp.dataBase.data.ble.BleDevice;
import com.example.jakub.arapp.dataBase.data.ble.BleDeviceDao;
import com.example.jakub.arapp.dataBase.data.internet.InternetDevice;
import com.example.jakub.arapp.dataBase.data.internet.InternetDeviceDao;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {BleDevice.class, InternetDevice.class}, version = 1, exportSchema = false)
public abstract class IoTDeviceDatabase extends RoomDatabase {
    public static final String DATABASE_NAME = "IoTDevice.db";

    public abstract BleDeviceDao getBleDeviceDao();
    public abstract InternetDeviceDao getInternetDeviceDao();
}
