package com.example.jakub.arapp.dataBase.data.ble;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import io.reactivex.Maybe;

@Dao
public interface BleDeviceDao {

    @Query("SELECT * FROM ble_device")
    Maybe<List<BleDevice>> getAllBleDevice();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(BleDevice bleDevice);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(List<BleDevice> bleDevice);

    @Delete
    void remove(BleDevice bleDevice);

    @Delete
    void removeDevices(List<BleDevice> bleDevices);
}
