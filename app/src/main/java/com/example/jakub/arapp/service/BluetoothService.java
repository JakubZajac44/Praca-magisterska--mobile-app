package com.example.jakub.arapp.service;

import android.annotation.SuppressLint;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

import com.example.jakub.arapp.MyApplication;
import com.example.jakub.arapp.bluetoothManager.connectionManager.ConnectedBleDevice;
import com.example.jakub.arapp.bluetoothManager.connectionManager.ConnectedBleDeviceProvider;
import com.example.jakub.arapp.dataBase.data.ble.BleDevice;
import com.example.jakub.arapp.dataBase.repository.ble.BleDeviceRepository;
import com.example.jakub.arapp.gps.GpsProvider;
import com.example.jakub.arapp.internetManager.apiConnection.ApiConnectionListener;
import com.example.jakub.arapp.internetManager.apiConnection.ApiConnectionProvider;
import com.example.jakub.arapp.model.device.InternetDeviceWrapper;
import com.example.jakub.arapp.notification.NotificationProvider;
import com.example.jakub.arapp.notification.NotificationProviderImpl;
import com.example.jakub.arapp.utility.Constants;
import com.example.jakub.arapp.utility.Logger;
import com.polidea.rxandroidble2.RxBleClient;
import com.polidea.rxandroidble2.RxBleConnection;
import com.polidea.rxandroidble2.RxBleDevice;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import javax.inject.Inject;

import io.reactivex.MaybeObserver;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;


public class BluetoothService extends Service
        implements ApiConnectionListener {

    private final BluetoothServiceBinder binder = new BluetoothServiceBinder();
    private final String TAG = BluetoothService.class.getSimpleName();
    private final int INTERVAL_TIME = 10000;
    private final int DELAY_TIME = 1000;


    @Inject
    public Logger logger;
    @Inject
    public Context context;
    @Inject
    public NotificationManager notificationManager;
    @Inject
    RxBleClient client;
    @Inject
    BleDeviceRepository repository;
    @Inject
    GpsProvider gpsProvider;
    @Inject
    ConnectedBleDeviceProvider connectedBleDeviceProviderImpl;
    @Inject
    NotificationProvider notificationProvider;
    @Inject
    SharedPreferences sharedPreferences;

    @Inject
    ApiConnectionProvider apiConnectionProvider;
    @SuppressLint("HandlerLeak")
    public Handler timeHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            logger.log(TAG, "Scanning");
            repository.getAllBleDevice().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new MaybeObserver<List<BleDevice>>() {
                        @Override
                        public void onSubscribe(Disposable d) {

                        }

                        @Override
                        public void onSuccess(List<BleDevice> bleDevices) {
                            tryConnectToBleDevice(bleDevices);
                        }

                        @Override
                        public void onError(Throwable e) {

                        }

                        @Override
                        public void onComplete() {

                        }
                    });
           if(!isRunningWithoutLogin) apiConnectionProvider.getInternetDevice();



        }

        private void tryConnectToBleDevice(List<BleDevice> bleDevices) {
            for (BleDevice bleDevice : bleDevices) {
                RxBleDevice device = client.getBleDevice(bleDevice.getAddress());
                RxBleConnection.RxBleConnectionState state = device.getConnectionState();
                logger.log(TAG, "Device address: " + device.getMacAddress() + ", status: " + state.toString());
                if (!state.equals(RxBleConnection.RxBleConnectionState.CONNECTED)) {
                    if (connectedBleDeviceProviderImpl.checkIfNotExist(device)) {
                        logger.log(TAG, "Connected new device: " + bleDevice.getAddress());
                        ConnectedBleDevice connectedBleDevice = new ConnectedBleDevice(device, context);
                        connectedBleDeviceProviderImpl.addNewDevice(connectedBleDevice);
                        connectedBleDeviceProviderImpl.establishConnection(connectedBleDevice);
                    } else {
                        logger.log(TAG, "Reconnecting " + bleDevice.getAddress());
                        connectedBleDeviceProviderImpl.reestablishConnection(bleDevice.getAddress());
                    }
                }
            }
        }
    };
    Observer<Location> gpsObserver;
    private Timer timer;
    private TimeUpdateTask task;
    private boolean isRunningWithoutLogin;

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            if (intent.getAction().equals(Constants.START_FOREGROUND_ACTION)) {
                logger.log(TAG, "Received Start Foreground Intent ");
                this.start();
            } else if (intent.getAction().equals(Constants.STOP_FOREGROUND_ACTION)) {
                logger.log(TAG, "Received Stop Foreground Intent");
                onDestroy();
            }
        }
        return START_STICKY;
    }

    public void start() {
        startForeground(NotificationProviderImpl.NOTIFICATION_ID, notificationProvider.getNotificationProvider());
        isRunningWithoutLogin = sharedPreferences.getBoolean(Constants.RUNNING_WITHOUT_LOGIN,false);
        timer = new Timer();
        task = new TimeUpdateTask();
        timer.schedule(task, DELAY_TIME, INTERVAL_TIME);
        this.createGpsObserver();
        gpsProvider.setGpsListener(gpsObserver);
        apiConnectionProvider.setUpListener(this);
    }

    private void createGpsObserver() {
        logger.log(TAG, "GPS observer created");
        gpsObserver = new Observer<Location>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(Location location) {
                Intent intent = new Intent(GpsProvider.ACTION_LOCATION_CHANGED);
                intent.putExtra(GpsProvider.KEY_LOCATION_CHANGED, location);
                sendCustomBroadcast(intent);
            }

            @Override
            public void onError(Throwable e) {
                logger.log(TAG, " Error received" + e.toString());
            }

            @Override
            public void onComplete() {
                logger.log(TAG, "All data emitted.");
            }
        };
    }

    private void destroyGpsObserver() {
        if (gpsObserver != null) {
            logger.log(TAG, "GpsObserver destroy");
            gpsObserver.onComplete();
            gpsObserver = null;
            gpsProvider.removeGpsListener();
        }
    }

    private void destroyTimer() {
        if (timer != null) {
            logger.log(TAG, "Timer destroy");
            timer.purge();
            timer.cancel();
            timer = null;
        }
    }

    @Override
    public void onCreate() {
        Log.i(TAG, "onCreate");
        ((MyApplication) getApplication()).getAppComponent().inject(this);
    }

    @Override
    public void onDestroy() {
        logger.log(TAG, "Service destroy");
        this.destroyTimer();
        connectedBleDeviceProviderImpl.removeAllConnection();
        this.destroyGpsObserver();
        apiConnectionProvider.removeListener();
        stopForeground(true);
        stopSelf();
        super.onDestroy();
    }

    private void sendCustomBroadcast(Intent intent) {
        context.sendBroadcast(intent);
    }

    @Override
    public void internetDeviceLoaded(List<InternetDeviceWrapper> internetDeviceWrapperList) {
        Intent intent = new Intent(ApiConnectionProvider.ACTION_INTERNET_DEVICE_LOADED);
        intent.putParcelableArrayListExtra(ApiConnectionProvider.KEY_INTERNET_DEVICE_LOADED, (ArrayList<InternetDeviceWrapper>) internetDeviceWrapperList);
        sendCustomBroadcast(intent);
    }

    @Override
    public void internetDeviceLoadedError(String errorMassage) {
        Intent intent = new Intent(ApiConnectionProvider.ACTION_INTERNET_DEVICE_ERROR);
        intent.putExtra(ApiConnectionProvider.KEY_INTERNET_DEVICE_ERROR, errorMassage);
        sendCustomBroadcast(intent);
        logger.log(TAG, errorMassage);
    }

    public class BluetoothServiceBinder extends Binder {
        public BluetoothService getService() {
            return BluetoothService.this;
        }
    }

    class TimeUpdateTask extends TimerTask {

        @Override
        public void run() {
            timeHandler.obtainMessage(0).sendToTarget();
        }
    }
}
