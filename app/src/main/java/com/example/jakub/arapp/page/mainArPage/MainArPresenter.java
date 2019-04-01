package com.example.jakub.arapp.page.mainArPage;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;

import com.example.jakub.arapp.bluetooth.connectionManager.ConnectedBleDeviceProvider;
import com.example.jakub.arapp.broadcastReceiver.bluetooth.BleBroadcastReceiver;
import com.example.jakub.arapp.broadcastReceiver.internet.InternetBroadcastReceiver;
import com.example.jakub.arapp.broadcastReceiver.internet.InternetConnectionListener;
import com.example.jakub.arapp.dataBase.data.internet.InternetDevice;
import com.example.jakub.arapp.dataBase.repository.interent.InternetDeviceRepository;
import com.example.jakub.arapp.model.device.BleDeviceWrapper;
import com.example.jakub.arapp.broadcastReceiver.bluetooth.BleConnectionListener;
import com.example.jakub.arapp.dataBase.data.ble.BleDevice;
import com.example.jakub.arapp.dataBase.repository.ble.BleDeviceRepository;
import com.example.jakub.arapp.model.device.InternetDeviceWrapper;
import com.example.jakub.arapp.model.device.IoTDevice;
import com.example.jakub.arapp.model.ScenarioAr;
import com.example.jakub.arapp.utility.Logger;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.MaybeObserver;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;

import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;


public class MainArPresenter implements MainArContract.Presenter, BleConnectionListener, InternetConnectionListener {

    public static final String TAG = MainArPresenter.class.getSimpleName();

    MainArContract.View view;

    @Inject
    BleDeviceRepository bleRepository;

    @Inject
    InternetDeviceRepository internetRepository;

    @Inject
    Logger logger;

    @Inject
    BluetoothAdapter bluetoothAdapter;

    @Inject
    Context context;

    @Inject
    BleBroadcastReceiver bleConnectionStatusReceiver;

    @Inject
    InternetBroadcastReceiver internetBroadcastReceiver;

    @Inject
    ConnectedBleDeviceProvider connectedBleDeviceProviderImpl;


    @Inject
    ScenarioAr scenario;


    @Inject
    public MainArPresenter() {

    }


    @Override
    public void attachView(MainArContract.View view) {
        this.view = view;
        bleConnectionStatusReceiver.setUpListener(this);
        internetBroadcastReceiver.setUpListener(this);
    }

    @Override
    public void detachView() {
        bleConnectionStatusReceiver.removeListener();
        internetBroadcastReceiver.removeListener();
        this.view = null;
    }

    @Override
    public void getSavedDevice() {
        bleRepository.getAllBleDevice().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new MaybeObserver<List<BleDevice>>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onSuccess(List<BleDevice> bleDevices) {
                List<IoTDevice> bleDeviceWrapperList = new ArrayList<>();
                for(BleDevice bleDevice:bleDevices){
                    int status = connectedBleDeviceProviderImpl.getStatus(bleDevice.getAddress());
                    BleDeviceWrapper device = new BleDeviceWrapper(bleDevice,status);
                    bleDeviceWrapperList.add(device);
                }
                if(!bleDeviceWrapperList.isEmpty())view.upDateListViewIoTDevice(bleDeviceWrapperList);
            }

            @Override
            public void onError(Throwable e) {
                logger.log(TAG, e.getMessage());
            }

            @Override
            public void onComplete() {
                logger.log(TAG, "complete");
            }
        });
        internetRepository.getAllInternetDevice().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new MaybeObserver<List<InternetDevice>>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onSuccess(List<InternetDevice> devices) {
                List<IoTDevice> bleDeviceWrapperList = new ArrayList<>();

                for(int i =0;i<devices.size();i++){
                    logger.log(TAG,devices.get(i).toString());
                }

                logger.log(TAG, "Internet device loaded:, "+String.valueOf(devices.size()));
                bleDeviceWrapperList.addAll(internetRepository.wrapListInternetDevice(devices));
                if(!bleDeviceWrapperList.isEmpty())view.upDateListViewIoTDevice(bleDeviceWrapperList);
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        });
    }


    @Override
    public void removeIotDevice(IoTDevice ioTDevice) {

        if(ioTDevice instanceof BleDeviceWrapper){
            BleDeviceWrapper deviceWrapper = (BleDeviceWrapper) ioTDevice;
            Observable.just(deviceWrapper)
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(Schedulers.newThread())
                    .subscribe(new Observer<BleDeviceWrapper>() {
                        @Override
                        public void onSubscribe(Disposable d) {

                        }

                        @Override
                        public void onNext(BleDeviceWrapper bleDeviceWrapper) {
                            bleRepository.deleteDevice(new BleDevice(bleDeviceWrapper));
                            connectedBleDeviceProviderImpl.removeDevice(bleDeviceWrapper.getAddress());
                        }

                        @Override
                        public void onError(Throwable e) {

                        }

                        @Override
                        public void onComplete() {

                        }
                    });
        }else if(ioTDevice instanceof InternetDeviceWrapper){
            InternetDeviceWrapper deviceWrapper = (InternetDeviceWrapper) ioTDevice;
            Observable.just(deviceWrapper)
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(Schedulers.newThread())
                    .subscribe(new Observer<InternetDeviceWrapper>() {
                        @Override
                        public void onSubscribe(Disposable d) {

                        }

                        @Override
                        public void onNext(InternetDeviceWrapper internetDeviceWrapper) {
                            internetRepository.deleteDevice(internetDeviceWrapper);
                        }

                        @Override
                        public void onError(Throwable e) {

                        }

                        @Override
                        public void onComplete() {

                        }
                    });

        }
    }

    @Override
    public void changeDeviceConnectionStatus(String address, int status) {
        logger.log(TAG,"Address: "+address+", status: "+String.valueOf(status));
        view.upDateListViewAllDevice(address,status);
    }

    @Override
    public void changeDeviceData(String address, String data) {
        // TODO tutaj zmienic w widoku odczyt sensorów
    }

    @Override
    public void internetDeviceLoaded(List<InternetDeviceWrapper> internetDeviceWrapperList) {
        view.upDateListViewAllInternetDevice( internetDeviceWrapperList);
    }

    @Override
    public void internetDeviceLoadedError(String errorMassage) {
        view.showMessage(errorMassage);
        view.setAllInternetDeviceOffline();
    }
}

