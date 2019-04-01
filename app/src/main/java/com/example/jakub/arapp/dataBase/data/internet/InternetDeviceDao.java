package com.example.jakub.arapp.dataBase.data.internet;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import io.reactivex.Maybe;

@Dao
public interface InternetDeviceDao {

    @Query("SELECT * FROM internet_device")
    Maybe<List<InternetDevice>> getAllInternetDevice();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(InternetDevice internetDevice);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(List<InternetDevice> internetDevice);

    @Delete
    void remove(InternetDevice internetDevice);

    @Delete
    void removeDevices(List<InternetDevice> internetDevice);
}
