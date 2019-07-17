package com.example.jakub.arapp.page.bluetoothScannerPage;

import android.app.Activity;

import com.example.jakub.arapp.model.device.BleDeviceWrapper;
import com.example.jakub.arapp.dataBase.data.ble.BleDevice;

import java.util.List;

import dagger.Binds;
import dagger.Module;

public interface BluetoothScannerContract {

    interface Presenter{
        void attachView(BluetoothScannerContract.View view);
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
        public abstract BluetoothScannerContract.Presenter provideBluetoothPresenter (BluetoothScannerPresenter bluetoothScannerPresenter);
    }
}
