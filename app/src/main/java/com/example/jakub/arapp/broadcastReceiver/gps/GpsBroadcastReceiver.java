package com.example.jakub.arapp.broadcastReceiver.gps;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;

import com.example.jakub.arapp.gps.GpsProvider;
import com.example.jakub.arapp.utility.Logger;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class GpsBroadcastReceiver extends BroadcastReceiver {

    public static final String TAG = GpsBroadcastReceiver.class.getSimpleName();


    @Inject
    Logger logger;


    private GpsLocationListener listener;

    @Inject
    public GpsBroadcastReceiver() {
    }

    public void setUpListener(GpsLocationListener listener){
        this.listener = listener;
    }
    public void removeListener(){
        this.listener = null;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Location location = intent.getParcelableExtra(GpsProvider.KEY_LOCATION_CHANGED);
        switch (intent.getAction()){
            case GpsProvider.ACTION_LOCATION_CHANGED:
                logger.log(TAG,"Position changed: "+location.toString());
                this.changeConnectionStatus(location);
                break;
        }

    }

    public void changeConnectionStatus(Location location){
        if(listener!=null){
            listener.changeGpsLocation(location);

        }
    }

    public static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(GpsProvider.ACTION_LOCATION_CHANGED);
        return intentFilter;
    }
}
