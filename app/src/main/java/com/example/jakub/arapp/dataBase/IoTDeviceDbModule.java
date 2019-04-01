package com.example.jakub.arapp.dataBase;

import android.app.Application;

import com.example.jakub.arapp.dataBase.data.ble.BleDeviceDao;
import com.example.jakub.arapp.dataBase.data.internet.InternetDeviceDao;
import com.example.jakub.arapp.dataBase.repository.ble.BleDeviceRepository;
import com.example.jakub.arapp.dataBase.repository.ble.BleDeviceRepositoryImpl;
import com.example.jakub.arapp.dataBase.repository.interent.InternetDeviceRepository;
import com.example.jakub.arapp.dataBase.repository.interent.InternetDeviceRepositoryImpl;

import javax.inject.Singleton;

import androidx.room.Room;
import dagger.Module;
import dagger.Provides;

@Module
public class IoTDeviceDbModule {


    private IoTDeviceDatabase demoDatabase;

    public IoTDeviceDbModule(Application mApplication) {
        demoDatabase = Room.databaseBuilder(mApplication, IoTDeviceDatabase.class, IoTDeviceDatabase.DATABASE_NAME).build();
    }



    @Singleton
    @Provides
    public IoTDeviceDatabase provideBleDeviceDataBase() {
        return demoDatabase;
    }

    @Singleton
    @Provides
    public BleDeviceDao provideBleDeviceDao(IoTDeviceDatabase ioTDeviceDatabase) {
        return ioTDeviceDatabase.getBleDeviceDao();
    }


    @Singleton
    @Provides
    BleDeviceRepository provideBleDeviceRepository(BleDeviceDao productDao) {
        return new BleDeviceRepositoryImpl(productDao);
    }

    @Singleton
    @Provides
    public InternetDeviceDao provideInternetDeviceRao(IoTDeviceDatabase ioTDeviceDatabase) {
        return ioTDeviceDatabase.getInternetDeviceDao();
    }


    @Singleton
    @Provides
    InternetDeviceRepository provideInternetDeviceRepository(InternetDeviceDao productDao) {
        return new InternetDeviceRepositoryImpl(productDao);
    }


}
