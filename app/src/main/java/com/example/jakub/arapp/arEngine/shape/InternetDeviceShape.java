package com.example.jakub.arapp.arEngine.shape;

import android.content.Context;

import com.example.jakub.arapp.model.device.InternetDeviceWrapper;
import com.example.jakub.arapp.utility.Constants;
import com.google.android.gms.maps.model.LatLng;

import lombok.Getter;
import lombok.Setter;

public class InternetDeviceShape extends IotDeviceShape {

    @Getter
    private int id;
    @Getter
    private double distance;
    @Getter
    private LatLng latLng;
    @Getter
    @Setter
    private String updatetime;

    public InternetDeviceShape(InternetDeviceWrapper device,double horizontalAngle,Context context) {
        super(horizontalAngle, 0, device.getName(),device.getSample(),device.getStatus(),context, Constants.SERVER_DEVICE);
        this.latLng = new LatLng(device.getLat(),device.getLon());
        this.updatetime = device.getUpdatetime();
        this.id = device.getId();
        this.distance=-1.0;
    }

    public void updateDistance(double distance){
        this.distance = distance;
    }


}
