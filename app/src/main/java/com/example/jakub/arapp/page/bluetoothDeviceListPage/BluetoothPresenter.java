package com.example.jakub.arapp.page.bluetoothDeviceListPage;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.jakub.arapp.bluetooth.scanningManager.BluetoothScannerManager;
import com.example.jakub.arapp.dataBase.data.ble.BleDevice;
import com.example.jakub.arapp.dataBase.repository.ble.BleDeviceRepository;
import com.example.jakub.arapp.utility.Logger;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.Completable;
import io.reactivex.CompletableObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class BluetoothPresenter implements BluetoothContract.Presenter {

    public static final String TAG = BluetoothPresenter.class.getSimpleName();

    @Inject
    SharedPreferences sharedPreferences;

    @Inject
    Logger logger;
    @Inject
    BluetoothScannerManager bluetoothScannerManager;
    @Inject
    BleDeviceRepository repository;
    @Inject
    Context context;
    private BluetoothContract.View view;

    @Inject
    public BluetoothPresenter() {
    }


    @Override
    public void attachView(BluetoothContract.View view) {
        this.view = view;
        this.prepareBluetoothManager();
    }

    private void prepareBluetoothManager() {
        bluetoothScannerManager.prepareManager(view.getActivity());
        bluetoothScannerManager.setBluetoothListener(bluetoothDeviceList -> view.setUpAdapterData(bluetoothDeviceList));
    }

    @Override
    public void detachView() {
        view = null;
    }

    @Override
    public void startScanBleDevice() {
      bluetoothScannerManager.startScanning();
    }

    @Override
    public void saveBleDevices(List<BleDevice> bleDevicesToSave) {

        Completable.fromAction(() -> repository.insertBleDevices(bleDevicesToSave)).observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io()).subscribe(new CompletableObserver() {
            @Override
            public void onSubscribe(Disposable d) {
            }

            @Override
            public void onComplete() {
                view.showMessage("Dodano poprawnie, " + bleDevicesToSave.size() + " rekordów");
                view.removeItemFromList();

            }

            @Override
            public void onError(Throwable e) {
                view.showMessage(e.getMessage());
            }
        });
    }

}
