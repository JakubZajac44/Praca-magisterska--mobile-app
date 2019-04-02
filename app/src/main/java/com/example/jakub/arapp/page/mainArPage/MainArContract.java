package com.example.jakub.arapp.page.mainArPage;

import com.example.jakub.arapp.model.device.InternetDeviceWrapper;
import com.example.jakub.arapp.model.device.IoTDevice;

import java.util.List;

import dagger.Binds;
import dagger.Module;

public interface MainArContract {

    interface Presenter{
        void attachView(MainArContract.View view);
        void detachView();
        void getSavedDevice();
        void removeIotDevice(int itemPosition);
    }

    interface View{

        void setupModel(List<IoTDevice> ioTDeviceList);
        void notifyDataChanged();
        void showMessage(String message);
    }

    @Module()
    abstract class ArSettingsModule {

        @Binds
        public abstract MainArContract.Presenter provideArSettingsPresenter (MainArPresenter mainArPresenter);
    }
}
