package com.example.jakub.arapp.page.bluetoothDeviceListPage;

import android.app.Activity;

import com.example.jakub.arapp.model.device.BleDeviceWrapper;
import com.example.jakub.arapp.dataBase.data.ble.BleDevice;

import java.util.List;

import dagger.Binds;
import dagger.Module;

public interface BluetoothContract {

    interface Presenter{
        void attachView(BluetoothContract.View view);
        void detachView();

        void startScanBleDevice();

        void saveBleDevices(List<BleDevice> bleDevicesToSave);
    }

    interface View{
        Activity getActivity();
        void setUpAdapterData(List<BleDeviceWrapper> bluetoothDeviceList);

        void removeItemFromList();

        void showMessage(String message);
    }

    @Module()
    abstract class BluetoothModule {

        @Binds
        public abstract BluetoothContract.Presenter provideBluetoothPresenter (BluetoothPresenter bluetoothPresenter);
    }
}
