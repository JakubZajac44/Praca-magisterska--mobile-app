package com.example.jakub.arapp.model;

import com.example.jakub.arapp.model.device.InternetDeviceWrapper;
import com.example.jakub.arapp.model.device.IoTDevice;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class ScenarioAr {

    private List<IoTDevice> internetDeviceList;

    @Inject
    public ScenarioAr() {
        internetDeviceList = new ArrayList<>();
    }

    public void clearScenario(){
        internetDeviceList.clear();
    }

    public void addInternetDevicesToScenario(List<InternetDeviceWrapper> internetDeviceList){
        this.internetDeviceList.clear();
        this.internetDeviceList.addAll(internetDeviceList);
    }

    public List<IoTDevice> getInternetDevicesFromScenario(){
        return internetDeviceList;
    }
}
