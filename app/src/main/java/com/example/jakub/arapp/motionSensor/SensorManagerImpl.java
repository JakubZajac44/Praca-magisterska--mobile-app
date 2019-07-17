package com.example.jakub.arapp.motionSensor;

import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;

import com.example.jakub.arapp.motionSensor.gyroFilter.GyroscopeFilterProvider;
import com.example.jakub.arapp.motionSensor.sensorUtility.LowPassFilter;
import com.example.jakub.arapp.motionSensor.sensorUtility.Orientation3d;
import com.example.jakub.arapp.utility.Logger;

import javax.inject.Inject;


import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.PublishSubject;



public class SensorManagerImpl implements SensorManager, SensorEventListener {

    public static final String TAG = SensorManagerImpl.class.getSimpleName();

    private final int BUFFER_SIZE = 10000;

    @Inject
    public Logger logger;

    @Inject
    public Context context;

    @Inject
    public GyroscopeFilterProvider gyroscopeFilter;

    int axisX;
    int axisY;
    Observer<Orientation3d> observer;
    PublishSubject<Orientation3d> ps = PublishSubject.create();
    private android.hardware.SensorManager mSensorManager;
    private Sensor accelerometerSensor;
    private Sensor magneticFieldSensor;
    private Sensor gyroscopeSensor;
    private Orientation3d orientation3d;
    private float[] sensorValuesFiltered = new float[3];
    private float[] magneticFieldValues = new float[3];
    private float[] accelerometerValues = new float[3];
    private float[] orientationValuesRemap = new float[9];
    private float[] accMagOrientation = new float[3];
    private float[] rotationMatrix = new float[9];
    private boolean isGyroAvailable;


    @Inject
    public SensorManagerImpl() {
        axisX = android.hardware.SensorManager.AXIS_X;
        axisY = android.hardware.SensorManager.AXIS_Z;
    }

    @Override
    public void setListener(Observer<Orientation3d> observer) {
        this.observer = observer;
        this.prepareSensorManager();
        this.registerMotionListeners();

    }

    public void prepareSensorManager() {
        mSensorManager = (android.hardware.SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        this.checkIfGyroAvailable();
        this.iniSensor();
        ps.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(observer);
    }

    private void checkIfGyroAvailable() {
        PackageManager packageManager = context.getPackageManager();
        isGyroAvailable = packageManager.hasSystemFeature(PackageManager.FEATURE_SENSOR_GYROSCOPE);
    }

    private void iniSensor() {
        accelerometerSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        magneticFieldSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        if (isGyroAvailable) {
            gyroscopeSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
            gyroscopeFilter.setListener(this);
        }
    }

    private void registerMotionListeners() {
        mSensorManager.registerListener(this,
                accelerometerSensor,
                android.hardware.SensorManager.SENSOR_DELAY_NORMAL);

        mSensorManager.registerListener(this,
                magneticFieldSensor,
                android.hardware.SensorManager.SENSOR_DELAY_NORMAL);
        if (isGyroAvailable) {
            mSensorManager.registerListener(this,
                    gyroscopeSensor,
                    android.hardware.SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    @Override
    public void removeListener() {
        ps.onComplete();
        this.unregisterListeners();
    }

    private void unregisterListeners() {
        mSensorManager.unregisterListener(this, accelerometerSensor);
        mSensorManager.unregisterListener(this, magneticFieldSensor);
        if (isGyroAvailable) mSensorManager.unregisterListener(this, gyroscopeSensor);

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        switch (event.sensor.getType()) {
            case Sensor.TYPE_ACCELEROMETER:
                sensorValuesFiltered = LowPassFilter.filter(event.values, accelerometerValues);
                System.arraycopy(sensorValuesFiltered, 0, accelerometerValues, 0, 3);
                calculateAccMagOrientation();
                break;

            case Sensor.TYPE_GYROSCOPE:
                gyroscopeFilter.gyroFunction(event, accMagOrientation);
                break;

            case Sensor.TYPE_MAGNETIC_FIELD:
                sensorValuesFiltered = LowPassFilter.filter(event.values, magneticFieldValues);
                System.arraycopy(sensorValuesFiltered, 0, magneticFieldValues, 0, 3);
                break;
        }
    }

    private void calculateAccMagOrientation() {
        android.hardware.SensorManager.getRotationMatrix(rotationMatrix, null, accelerometerValues, magneticFieldValues);
        if (isGyroAvailable) {
            android.hardware.SensorManager.getOrientation(rotationMatrix, accMagOrientation);
        } else {
            android.hardware.SensorManager.remapCoordinateSystem(rotationMatrix, axisX, axisY, orientationValuesRemap);
            android.hardware.SensorManager.getOrientation(orientationValuesRemap, accMagOrientation);
            orientation3d = new Orientation3d(accMagOrientation[0], accMagOrientation[1], accMagOrientation[2]);
                actualizeDevicePosition(orientation3d);
        }
    }

    @Override
    public void actualizeDevicePosition(Orientation3d orientation3d) {
        logger.log(TAG,String.valueOf(orientation3d.getAzimuthDegrees())+" "+String.valueOf(orientation3d.getPitchDegrees())+" "+String.valueOf(orientation3d.getRollDegrees()));
        ps.onNext(orientation3d);
    }

}
