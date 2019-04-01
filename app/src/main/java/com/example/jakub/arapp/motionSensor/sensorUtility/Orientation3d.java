package com.example.jakub.arapp.motionSensor.sensorUtility;

import javax.inject.Inject;


public class Orientation3d {

    private final float WRONG_ATTRIBUTE = -1000;

    private float azimuth;
    private float pitch;
    private float roll;

    private float azimuthDegrees;
    private float pitchDegrees;
    private float rollDegrees;

    @Inject
    public Orientation3d(){
        this.azimuth = this.azimuthDegrees = WRONG_ATTRIBUTE;
        this.pitch = this.pitchDegrees =  WRONG_ATTRIBUTE;
        this.roll = this.rollDegrees = WRONG_ATTRIBUTE;

    }

    public Orientation3d(float azimuth, float pitch, float roll) {
        this.azimuth = azimuth;
        this.pitch = pitch;
        this.roll = roll;
        this.calculateAngles();
    }

    private void calculateAngles() {
        azimuthDegrees = (float) (Math.toDegrees(azimuth) + 360) % 360;
        pitchDegrees = (float) (Math.toDegrees(pitch) + 360) % 360;
        rollDegrees = (float) (Math.toDegrees(roll) + 360) % 360;
    }

    public float getAzimuthDegrees() {
        return azimuthDegrees;
    }

    public float getPitchDegrees() {
        return pitchDegrees;
    }

    public float getRollDegrees() {
        return rollDegrees;
    }

    public void updateOrientation(Orientation3d orientation3d) {
        this.azimuthDegrees = orientation3d.getAzimuthDegrees();
        this.pitchDegrees = orientation3d.getPitchDegrees();
        this.rollDegrees = orientation3d.getRollDegrees();
    }

    public boolean isVectorReady(){

        if(this.azimuthDegrees!=WRONG_ATTRIBUTE &&
                this.pitchDegrees!=WRONG_ATTRIBUTE &&
                this.rollDegrees!=WRONG_ATTRIBUTE)return true;
        else return false;
    }
}
