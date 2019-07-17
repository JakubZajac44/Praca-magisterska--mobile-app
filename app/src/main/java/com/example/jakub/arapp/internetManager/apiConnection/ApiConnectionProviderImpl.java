package com.example.jakub.arapp.internetManager.apiConnection;

import android.content.Context;

import com.example.jakub.arapp.application.ConfigApp;
import com.example.jakub.arapp.model.device.InternetDeviceWrapper;
import com.example.jakub.arapp.internetManager.api.ApiConnection;
import com.example.jakub.arapp.utility.Logger;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;

public class ApiConnectionProviderImpl implements ApiConnectionProvider {

    public static final String TAG = ApiConnectionProviderImpl.class.getSimpleName();
    private final String TOKEN_PATTERN = "Token ";

    @Inject
    Context context;

    @Inject
    Logger logger;

    @Inject
    Retrofit retrofit;

    private ApiConnectionListener listener;


    @Inject
    public ApiConnectionProviderImpl() {
    }

    @Override
    public void getInternetDevice() {
        ApiConnection apiConnection = retrofit.create(ApiConnection.class);
        apiConnection.getAllInternetDevice(TOKEN_PATTERN+ ConfigApp.TOKEN)
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
    public void setUpListener(ApiConnectionListener listener) {
        this.listener = listener;
    }

    @Override
    public void removeListener() {
        this.listener=null;
    }


}
