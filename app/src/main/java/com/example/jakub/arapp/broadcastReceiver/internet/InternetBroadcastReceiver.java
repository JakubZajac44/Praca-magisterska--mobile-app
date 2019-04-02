package com.example.jakub.arapp.broadcastReceiver.internet;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import com.example.jakub.arapp.model.device.InternetDeviceWrapper;
import com.example.jakub.arapp.internet.backendConnection.BackendConnectionProvider;
import com.example.jakub.arapp.utility.Logger;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;
@Singleton
public class InternetBroadcastReceiver extends BroadcastReceiver {

    public static final String TAG = InternetBroadcastReceiver.class.getSimpleName();


    @Inject
    Logger logger;


    private InternetConnectionListener listener;

    @Inject
    public InternetBroadcastReceiver() {
    }

    public void setUpListener(InternetConnectionListener listener){
        this.listener = listener;
    }
    public void removeListener(){
        this.listener = null;
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        switch (intent.getAction()){
            case BackendConnectionProvider.ACTION_INTERNET_DEVICE_LOADED:
                List<InternetDeviceWrapper> internetDeviceWrapperList=  intent.getParcelableArrayListExtra(BackendConnectionProvider.KEY_INTERNET_DEVICE_LOADED);
                logger.log(TAG,"Internet device loaded: "+String.valueOf(internetDeviceWrapperList.size()));
                if(listener!=null)listener.internetDeviceLoadedReceive(internetDeviceWrapperList);
                break;
            case BackendConnectionProvider.ACTION_INTERNET_DEVICE_ERROR:
                String message = intent.getStringExtra(BackendConnectionProvider.KEY_INTERNET_DEVICE_ERROR);
                if(listener!=null)listener.internetDeviceLoadedErrorReceive(message);
                break;
        }

    }


    public static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BackendConnectionProvider.ACTION_INTERNET_DEVICE_LOADED);
        intentFilter.addAction(BackendConnectionProvider.ACTION_INTERNET_DEVICE_ERROR);
        return intentFilter;
    }
}
