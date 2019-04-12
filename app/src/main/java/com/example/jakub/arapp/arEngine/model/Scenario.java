package com.example.jakub.arapp.arEngine.model;

import android.content.Context;

import com.example.jakub.arapp.MyApplication;
import com.example.jakub.arapp.dataBase.data.ble.BleDevice;
import com.example.jakub.arapp.dataBase.data.internet.InternetDevice;
import com.example.jakub.arapp.dataBase.repository.ble.BleDeviceRepository;
import com.example.jakub.arapp.dataBase.repository.interent.InternetDeviceRepository;
import com.example.jakub.arapp.model.device.InternetDeviceWrapper;
import com.example.jakub.arapp.model.device.IoTDevice;
import com.example.jakub.arapp.utility.Logger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.MaybeObserver;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;


public class Scenario {

    public static final String TAG = Scenario.class.getSimpleName();

    @Inject
    InternetDeviceRepository internetRepository;

    @Inject
    BleDeviceRepository blRepository;

    @Inject
    Logger logger;

    private ScenarioListener listener;

    private List<IoTDevice> ioTDeviceList;

    public Scenario(Context context) {
        ((MyApplication) context.getApplicationContext()).getAppComponent().inject(this);
        ioTDeviceList = Collections.synchronizedList(new ArrayList<>());
    }

    public void setUpListener(ScenarioListener listener) {
        this.listener = listener;
    }

    public void getSavedDevice() {
        this.getBleDevices();

    }

    private void getBleDevices() {
        blRepository.getAllBleDevice().subscribeOn(Schedulers.io()).observeOn(Schedulers.io()).subscribe(new MaybeObserver<List<BleDevice>>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onSuccess(List<BleDevice> bleDevices) {
                ioTDeviceList.addAll(blRepository.wrapListInternetDevice(bleDevices));
                this.onComplete();
            }

            @Override
            public void onError(Throwable e) {
                getInternetDevices();
            }

            @Override
            public void onComplete() {
                getInternetDevices();
            }
        });
    }

    private void getInternetDevices() {
        internetRepository.getAllInternetDevice().subscribeOn(Schedulers.io()).observeOn(Schedulers.io()).subscribe(new MaybeObserver<List<InternetDevice>>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onSuccess(List<InternetDevice> devices) {
                ioTDeviceList.addAll(internetRepository.wrapListInternetDevice(devices));
                this.onComplete();
            }

            @Override
            public void onError(Throwable e) {
                listener.scenarioIsReady();
            }

            @Override
            public void onComplete() {
                listener.scenarioIsReady();
            }
        });
    }

    public List<IoTDevice> getIotDevices() {
        return ioTDeviceList;
    }

}
