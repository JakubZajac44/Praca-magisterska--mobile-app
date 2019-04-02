package com.example.jakub.arapp.internet.backendConnection;

import android.content.Context;

import com.example.jakub.arapp.broadcastReceiver.internet.InternetConnectionListener;
import com.example.jakub.arapp.model.device.InternetDeviceWrapper;
import com.example.jakub.arapp.internet.api.ApiService;
import com.example.jakub.arapp.utility.Logger;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;

public class BackendConnectionProviderImpl implements BackendConnectionProvider {

    public static final String TAG = BackendConnectionProviderImpl.class.getSimpleName();

    @Inject
    Context context;

    @Inject
    Logger logger;

    @Inject
    Retrofit retrofit;

    private BackendConnectionListener listener;


    @Inject
    public BackendConnectionProviderImpl() {
    }

    @Override
    public void getInternetDevice() {
        ApiService apiService = retrofit.create(ApiService.class);
        apiService.getAllInternetDevice()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableSingleObserver<List<InternetDeviceWrapper>>() {
                    @Override
                    public void onSuccess(List<InternetDeviceWrapper> internetDeviceWrappers) {
                        logger.log(TAG, "Internet device loaded, size: " + internetDeviceWrappers.size());
                        if(listener!=null)listener.internetDeviceLoaded(internetDeviceWrappers);
                    }

                    @Override
                    public void onError(Throwable e) {
                        logger.log(TAG, e.getMessage());
                        if(listener!=null)listener.internetDeviceLoadedError(e.getMessage());
                    }
                });
    }

    @Override
    public void setUpListener(BackendConnectionListener listener) {
        this.listener = listener;
    }

    @Override
    public void removeListener() {
        this.listener=null;
    }


}
