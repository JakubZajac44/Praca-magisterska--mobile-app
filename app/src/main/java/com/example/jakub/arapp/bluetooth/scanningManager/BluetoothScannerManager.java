package com.example.jakub.arapp.bluetooth.scanningManager;

import android.app.Activity;

import dagger.Binds;
import dagger.Module;


/* Class provide Bluetooth manager, which scanning ble device. Listener return list of Ble Device

    To use provider, you have to create BluetoothScannerManager by @Inject

    @Inject
    BluetoothScannerManager bluetoothScannerManager;

    than you should provide activity and set up listener.

     bluetoothScannerManager.prepareManager(view.getActivity());
     bluetoothScannerManager.setBluetoothListener(new BluetoothScanningListener() {
            @Override
            public void BleDeviceListInvalidate(List<BluetoothDevice> bluetoothDeviceList) {

            }
        });


    To start scanning you have to use method

     bluetoothScannerManager.startScanning();

 */


public interface BluetoothScannerManager {

    /*
        Method to provide activity
     */
    void prepareManager(Activity activity);

    /*
        Method to start scanning
     */
    void startScanning();

    /*
    Method to stop scanning
     */
    void stopScanning();

    /*
            Method to set up listener
    */
    void setBluetoothListener(BluetoothScanningListener listener);


    @Module
    abstract class BluetoothScannerManagerModule {
        @Binds
        public abstract BluetoothScannerManager mySensorManagerProvider(BluetoothScannerMangerImpl mySensorManager);


    }

}
