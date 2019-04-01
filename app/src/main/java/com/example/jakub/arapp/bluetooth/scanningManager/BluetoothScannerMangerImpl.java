package com.example.jakub.arapp.bluetooth.scanningManager;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.widget.Toast;

import com.example.jakub.arapp.R;
import com.example.jakub.arapp.model.device.BleDeviceWrapper;
import com.example.jakub.arapp.utility.Logger;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class BluetoothScannerMangerImpl implements BluetoothScannerManager {

    public static final String TAG = BluetoothScannerMangerImpl.class.getSimpleName();
    private final int REQUEST_ENABLE_BT = 2;
    private final long SCAN_PERIOD = 5000;
    @Inject
    Context context;

    @Inject
    Logger logger;

    @Inject
    BluetoothManager bluetoothManager;

    private boolean isBleAvailable;
    private Activity activity;
    private BluetoothScanningListener listener;
    private BluetoothAdapter bluetoothAdapter;
    private Handler handler;
    private boolean mScanning;
    private List<BleDeviceWrapper> bluetoothDeviceList;
    private BluetoothAdapter.LeScanCallback leScanCallback =
            new BluetoothAdapter.LeScanCallback() {
                @Override
                public void onLeScan(final BluetoothDevice device, int rssi,
                                     final byte[] scanRecord) {
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (!isBluetoothDeviceAdded(device.getAddress())) {
                                logger.log(TAG,"New device added, address : " + device.getAddress());
                                bluetoothDeviceList.add(new BleDeviceWrapper(device));
                            }
                        }
                    });
                }

                private boolean isBluetoothDeviceAdded(String address) {
                    for (BleDeviceWrapper device : bluetoothDeviceList) {
                        if (device.getAddress().equals(address)) return true;
                    }
                    return false;
                }
            };
    @Inject
    public BluetoothScannerMangerImpl() {
        this.isBleAvailable = false;
        this.mScanning = false;
        this.handler = new Handler();
        this.bluetoothDeviceList = new ArrayList<>();
    }

    public void prepareManager(Activity activity) {
        this.checkBleAvailability();
        this.activity = activity;
        if (isBleAvailable) {
            bluetoothAdapter = bluetoothManager.getAdapter();
            this.checkBleEnable();
        }
    }

    private void checkBleAvailability() {
        if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            this.isBleAvailable = true;
        } else {
            Toast.makeText(context, context.getResources().getString(R.string.ble_not_supported), Toast.LENGTH_SHORT).show();
        }
    }

    private void checkBleEnable() {
        if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            activity.startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }
    }

    @Override
    public void startScanning() {
        this.scanLeDevice();
    }
    @Override
    public void stopScanning(){this.scanLeDevice();}

    private void scanLeDevice() {
        if (!mScanning) {
            // Stops scanning after a pre-defined scan period.
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mScanning = false;
                    bluetoothAdapter.stopLeScan(leScanCallback);
                    listener.BleDeviceListInvalidate(bluetoothDeviceList);
                }
            }, SCAN_PERIOD);
            this.bluetoothDeviceList.clear();
            mScanning = true;
            bluetoothAdapter.startLeScan(leScanCallback);
        } else {
            mScanning = false;
            bluetoothAdapter.stopLeScan(leScanCallback);
        }

    }

    public void setBluetoothListener(BluetoothScanningListener listener) {
        this.listener = listener;
    }


}
