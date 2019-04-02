package com.example.jakub.arapp.page.mainArPage;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.os.Handler;
import android.os.Message;

import com.example.jakub.arapp.R;
import com.example.jakub.arapp.bluetooth.connectionManager.ConnectedBleDeviceProvider;
import com.example.jakub.arapp.broadcastReceiver.bluetooth.BleBroadcastReceiver;
import com.example.jakub.arapp.broadcastReceiver.bluetooth.BleConnectionListener;
import com.example.jakub.arapp.broadcastReceiver.internet.InternetBroadcastReceiver;
import com.example.jakub.arapp.broadcastReceiver.internet.InternetConnectionListener;
import com.example.jakub.arapp.dataBase.data.ble.BleDevice;
import com.example.jakub.arapp.dataBase.data.internet.InternetDevice;
import com.example.jakub.arapp.dataBase.repository.ble.BleDeviceRepository;
import com.example.jakub.arapp.dataBase.repository.interent.InternetDeviceRepository;
import com.example.jakub.arapp.model.ScenarioAr;
import com.example.jakub.arapp.model.device.BleDeviceWrapper;
import com.example.jakub.arapp.model.device.InternetDeviceWrapper;
import com.example.jakub.arapp.model.device.IoTDevice;
import com.example.jakub.arapp.utility.Constants;
import com.example.jakub.arapp.utility.Logger;

import java.util.ArrayList;
import java.util.Collections;
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

    private List<IoTDevice> ioTDeviceList;

    @Inject
    public MainArPresenter() {
        ioTDeviceList = Collections.synchronizedList(new ArrayList<>());
    }


    public Handler timeHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            view.notifyDataChanged();
        }
    };



    @Override
    public void attachView(MainArContract.View view) {
        this.view = view;
        bleConnectionStatusReceiver.setUpListener(this);
        internetBroadcastReceiver.setUpListener(this);
        view.setupModel(ioTDeviceList);
    }

    @Override
    public void detachView() {
        bleConnectionStatusReceiver.removeListener();
        internetBroadcastReceiver.removeListener();
        this.view = null;
    }

    @Override
    public void getSavedDevice() {
        ioTDeviceList.clear();
        bleRepository.getAllBleDevice().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new MaybeObserver<List<BleDevice>>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onSuccess(List<BleDevice> bleDevices) {
                ioTDeviceList.addAll(bleRepository.wrapListInternetDevice(bleDevices));
                setAllBleDeviceStatus();
                view.notifyDataChanged();
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
                logger.log(TAG, "Internet device loaded:, " + String.valueOf(devices.size()));
                bleDeviceWrapperList.addAll(internetRepository.wrapListInternetDevice(devices));
                if (!bleDeviceWrapperList.isEmpty()) {
                    ioTDeviceList.addAll(bleDeviceWrapperList);
                    view.notifyDataChanged();
                }
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        });
    }

    private void setAllBleDeviceStatus(){
        for(IoTDevice device:ioTDeviceList){
            if(device instanceof BleDeviceWrapper){
                int status = connectedBleDeviceProviderImpl.getStatus(((BleDeviceWrapper)device).getAddress());
                device.setStatus(status);
            }

        }
    }

    @Override
    public void removeIotDevice(int position) {
        IoTDevice ioTDevice = ioTDeviceList.get(position);
        if (ioTDevice instanceof BleDeviceWrapper) {
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
                        }

                        @Override
                        public void onError(Throwable e) {

                        }

                        @Override
                        public void onComplete() {
                            connectedBleDeviceProviderImpl.removeDevice(deviceWrapper.getAddress());
                            ioTDeviceList.remove(position);
                            timeHandler.obtainMessage(0).sendToTarget();
                        }
                    });
        } else if (ioTDevice instanceof InternetDeviceWrapper) {
            InternetDeviceWrapper deviceWrapper = (InternetDeviceWrapper) ioTDevice;
            Observable.just(deviceWrapper)
                    .subscribeOn(AndroidSchedulers.mainThread())
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
                            ioTDeviceList.remove(position);
                            timeHandler.obtainMessage(0).sendToTarget();
                        }
                    });

        }
    }

    // Ble device listener
    @Override
    public void changeBleDeviceConnectionStatus(String address, int status) {
        logger.log(TAG, "Address: " + address + ", status: " + String.valueOf(status));
        for (IoTDevice ioTDevice : ioTDeviceList) {
            if (ioTDevice instanceof BleDeviceWrapper) {
                if (((BleDeviceWrapper) ioTDevice).getAddress().equals(address))
                    ioTDevice.setStatus(status);
            }
        }
        view.notifyDataChanged();

    }

    @Override
    public void changeBleDeviceData(String address, String data) {
        for (IoTDevice ioTDevice : ioTDeviceList) {
            if (ioTDevice instanceof BleDeviceWrapper) {
                if (((BleDeviceWrapper) ioTDevice).getAddress().equals(address))
                    ioTDevice.setSample(data);
            }
        }
        view.notifyDataChanged();
    }

    // Internet device listener
    @Override
    public void internetDeviceLoadedReceive(List<InternetDeviceWrapper> internetDeviceWrapperList) {
        for (IoTDevice ioTDevice : ioTDeviceList) {
            if (ioTDevice instanceof InternetDeviceWrapper) {
                for (InternetDeviceWrapper internetDevice : internetDeviceWrapperList) {
                    if (((InternetDeviceWrapper) ioTDevice).getId() == internetDevice.getId()) {
                        ioTDevice.setStatus(Constants.CONNECTED_STATUS);
                        ((InternetDeviceWrapper) ioTDevice).setUpdatetime(internetDevice.getUpdatetime());
                        break;
                    }
                }
            }
        }
        view.notifyDataChanged();
    }

    @Override
    public void internetDeviceLoadedErrorReceive(String errorMassage) {
        view.showMessage(context.getString(R.string.error_backend_connection));
        this.setAllInternetDeviceOffline();
    }

    private void setAllInternetDeviceOffline() {
        for (IoTDevice ioTDevice : ioTDeviceList) {
            if (ioTDevice instanceof InternetDeviceWrapper) {
                ioTDevice.setStatus(Constants.DISCONNECTED_STATUS);
            }
        }
        view.notifyDataChanged();
    }
}

