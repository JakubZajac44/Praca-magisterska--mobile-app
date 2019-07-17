package com.example.jakub.arapp.bluetoothManager;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;

import com.polidea.rxandroidble2.RxBleClient;
import com.polidea.rxandroidble2.RxBleDevice;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class BluetoothManagerModule {

    @Provides
    @Singleton
    public BluetoothManager bluetoothManagerProvider(Context context){
        return (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
    }

    @Provides
    @Singleton
    public BluetoothAdapter bluetoothAdapterProvider(BluetoothManager bluetoothManager){
        return bluetoothManager.getAdapter();
    }

    @Provides
    @Singleton
    public RxBleClient rxBleClientProvider(Context context){
        return  RxBleClient.create(context);
    }

}
