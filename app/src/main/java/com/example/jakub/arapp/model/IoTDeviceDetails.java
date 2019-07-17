package com.example.jakub.arapp.model;

import lombok.Getter;
import lombok.Setter;

public class IoTDeviceDetails {

    @Setter
    @Getter
    private int type;
    @Setter
    @Getter
    private double azimuth;
    @Setter
    @Getter
    private double pitch;
    @Setter
    @Getter
    private String data;



}
