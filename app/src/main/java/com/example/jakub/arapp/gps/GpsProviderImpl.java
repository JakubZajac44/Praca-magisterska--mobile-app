package com.example.jakub.arapp.gps;

import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import com.example.jakub.arapp.application.ConfigApp;
import com.example.jakub.arapp.utility.Logger;

import javax.inject.Inject;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.PublishSubject;

public class GpsProviderImpl implements GpsProvider {

    private static final String TAG = GpsProviderImpl.class.getSimpleName();

    @Inject
    public Context context;
    @Inject
    public LocationManager locationManager;
    @Inject
    ConfigApp configApp;
    @Inject
    Logger logger;


    Observer<Location> observer;
    PublishSubject<Location> ps = PublishSubject.create();

    LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
           ps.onNext(location);
            logger.log(TAG, "updateLocation");
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {
        }

        @Override
        public void onProviderDisabled(String provider) {
        }
    };
    private String providerGPS;


    @Inject
    public GpsProviderImpl() {
        this.observer = null;
    }

    @SuppressLint("MissingPermission")
    public void setGpsListener(Observer<Location> observer) {
        if (observer != null) {
            this.observer = observer;
            ps.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(observer);
            providerGPS = this.getProviderName();
            locationManager.requestLocationUpdates(providerGPS, 0, 0, locationListener);
        }
    }

    @Override
    public void removeGpsListener() {
        if (observer != null) {
            locationManager.removeUpdates(locationListener);
            ps.onComplete();
            this.observer = null;
        }
    }

    private String getProviderName() {
        Criteria criteria = new Criteria();
        criteria.setPowerRequirement(Criteria.POWER_MEDIUM);
        criteria.setAccuracy(Criteria.ACCURACY_MEDIUM);
        criteria.setSpeedRequired(true);
        criteria.setSpeedAccuracy(Criteria.ACCURACY_MEDIUM);
        criteria.setBearingRequired(false);
        criteria.setAltitudeRequired(false);
        criteria.setCostAllowed(false);
        String providerName = locationManager.getBestProvider(criteria, true);
        logger.log(TAG, providerName);
        return providerName;
    }

}
