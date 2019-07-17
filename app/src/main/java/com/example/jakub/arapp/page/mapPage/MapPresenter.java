package com.example.jakub.arapp.page.mapPage;


import android.content.Context;
import android.location.Location;

import com.example.jakub.arapp.R;
import com.example.jakub.arapp.broadcastReceiver.gps.GpsBroadcastReceiver;
import com.example.jakub.arapp.broadcastReceiver.gps.GpsLocationListener;
import com.example.jakub.arapp.broadcastReceiver.internet.InternetBroadcastReceiver;
import com.example.jakub.arapp.broadcastReceiver.internet.InternetConnectionListener;
import com.example.jakub.arapp.dataBase.repository.ble.BleDeviceRepository;
import com.example.jakub.arapp.dataBase.repository.interent.InternetDeviceRepository;
import com.example.jakub.arapp.model.device.InternetDeviceWrapper;
import com.example.jakub.arapp.utility.Logger;
import com.example.jakub.arapp.utility.MathOperation;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.Completable;
import io.reactivex.CompletableObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class MapPresenter implements MapContract.Presenter, GpsLocationListener, InternetConnectionListener {

    public static final String TAG = MapPresenter.class.getSimpleName();
    @Inject
    GpsBroadcastReceiver gpsBroadcastReceiver;
    @Inject
    InternetBroadcastReceiver internetBroadcastReceiver;
    @Inject
    Logger logger;
    @Inject
    Context context;
    @Inject
    BleDeviceRepository bleRepository;

    @Inject
    InternetDeviceRepository internetRepository;


    private MapContract.View view;

    private List<InternetDeviceWrapper> allInternetDevice;
    private List<InternetDeviceWrapper> internetDeviceToSave;

    @Inject
    public MapPresenter() {
        allInternetDevice = new ArrayList<>();
        internetDeviceToSave = new ArrayList<>();
    }

    @Override
    public void attachView(MapContract.View view) {
        this.view = view;
        gpsBroadcastReceiver.setUpListener(this);
        internetBroadcastReceiver.setUpListener(this);
    }

    @Override
    public void detachView() {
        this.view = null;
        gpsBroadcastReceiver.removeListener();
        internetBroadcastReceiver.removeListener();
    }

    @Override
    public void saveDeviceInCircle(int currentRadius, Location currentCircleLocation) {
        internetDeviceToSave.clear();
        for(InternetDeviceWrapper device: allInternetDevice){
            if(shouldBeSaved(device,currentRadius,currentCircleLocation)){
                internetDeviceToSave.add(device);
            }
        }
       this.saveInternetDeviceToDb();
    }

    private void saveInternetDeviceToDb() {
        logger.log(TAG,"Saved to scenario, size: "+String.valueOf(internetDeviceToSave.size()));
        this.removeAllInternetDevice(internetDeviceToSave);
    }

    private void removeAllInternetDevice(List<InternetDeviceWrapper> internetDeviceToSave) {
        Completable.fromAction(()-> internetRepository.deleteAllDevice()).observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io()).subscribe(new CompletableObserver() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onComplete() {
                if(!internetDeviceToSave.isEmpty())saveNewInternetDevice(internetDeviceToSave);
            }

            @Override
            public void onError(Throwable e) {
                view.showMessage(context.getString(R.string.api_error));
            }
        });
    }

    private void saveNewInternetDevice(List<InternetDeviceWrapper> internetDeviceToSave) {
        Completable.fromAction(() -> internetRepository.insertInternetDevices(internetDeviceToSave)).observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io()).subscribe(new CompletableObserver() {
            @Override
            public void onSubscribe(Disposable d) {
            }

            @Override
            public void onComplete() {
                view.showMessage("Zapisano poprawnie, " + internetDeviceToSave.size() + " urządzeń");
            }

            @Override
            public void onError(Throwable e) {
                logger.log(TAG,e.getMessage());
                view.showMessage(context.getString(R.string.api_error));
            }
        });
    }


    private boolean shouldBeSaved(InternetDeviceWrapper device, int currentRadius, Location currentCircleLocation) {
        Location deviceLocation = MathOperation.getLocation(device.getLat(),device.getLon());
        double distance = MathOperation.measureDistanceBetweenM(deviceLocation,currentCircleLocation);
        logger.log(TAG,"Distance: "+String.valueOf(distance)+", radius: "+String.valueOf(currentRadius));
        if(distance<=currentRadius)return true;
        else return false;
    }

    @Override
    public void changeGpsLocation(Location location) {
        logger.log(TAG, location);
        view.changeUserLocation(location);
    }

    @Override
    public void internetDeviceLoadedReceive(List<InternetDeviceWrapper> internetDeviceWrapperList) {
        allInternetDevice.clear();
        allInternetDevice.addAll(internetDeviceWrapperList);
        logger.log(TAG,"Number of Internet Device loaded: "+String.valueOf(internetDeviceWrapperList.size()));
        view.addInternetDeviceMarker(internetDeviceWrapperList);
    }

    @Override
    public void internetDeviceLoadedErrorReceive(String errorMassage) {
        view.showMessage(context.getString(R.string.api_error));
        view.removeAllInternetDeviceMarker();
        allInternetDevice.clear();
    }
}
