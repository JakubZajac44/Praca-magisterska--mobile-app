package com.example.jakub.arapp.arEngine.shape;

import android.content.Context;

import lombok.Getter;
import lombok.Setter;

public abstract class IotDeviceShape extends Frame {

    protected double horizontalAngle;
    protected double pitchAngle;
    @Getter
    @Setter
    protected String name;
    @Getter
    @Setter
    protected String sample;
    @Getter
    @Setter
    protected int status;


    public IotDeviceShape(double horizontalAngle, double pitch, String name, String sample, int status,Context context) {
        super(context,horizontalAngle, pitch);
        this.horizontalAngle = horizontalAngle;
        this.pitchAngle = pitch;
        this.name = name;
        this.sample = sample;
        this.status = status;
    }
}
