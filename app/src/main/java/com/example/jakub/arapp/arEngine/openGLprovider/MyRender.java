package com.example.jakub.arapp.arEngine.openGLprovider;

import android.content.Context;
import android.location.Location;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.util.Log;

import com.example.jakub.arapp.arEngine.IotTouchListener;
import com.example.jakub.arapp.arEngine.shape.BleDeviceShape;
import com.example.jakub.arapp.arEngine.shape.InternetDeviceShape;
import com.example.jakub.arapp.model.IoTDeviceDetails;
import com.example.jakub.arapp.model.device.BleDeviceWrapper;
import com.example.jakub.arapp.model.device.InternetDeviceWrapper;
import com.example.jakub.arapp.model.device.IoTDevice;
import com.example.jakub.arapp.utility.Constants;
import com.example.jakub.arapp.utility.MathOperation;

import java.util.ArrayList;
import java.util.List;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class MyRender implements GLSurfaceView.Renderer {

    public static final String TAG = MyRender.class.getCanonicalName();

    private final float[] mMVPMatrix = new float[16];
    private final float[] mProjectionMatrix = new float[16];
    private final float[] mViewMatrix = new float[16];
    private final float eyeX = 0.0f;
    private final float eyeY = 0.0f;
    private final float eyeZ = 0.0f;
    private final float upX = 0.0f;
    private final float upY = 1.0f;
    private final float upZ = 0.0f;
    Context context;
    private float X = 0.0f;
    private float Y = 0.0f;
    private List<IoTDevice> ioTDevices;
    private List<BleDeviceShape> bleDeviceShapes;
    private List<InternetDeviceShape> internetDeviceShapes;
    private IotTouchListener listener;

    public MyRender(List<IoTDevice> ioTDevices, Context context) {
        bleDeviceShapes = new ArrayList<>();
        internetDeviceShapes = new ArrayList<>();
        this.ioTDevices = new ArrayList<>();
        this.ioTDevices.addAll(ioTDevices);
        this.context = context;
    }

    public static int loadShader(int type, String shaderCode) {
        int shader = GLES20.glCreateShader(type);
        GLES20.glShaderSource(shader, shaderCode);
        GLES20.glCompileShader(shader);
        return shader;
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        this.createShapes(ioTDevices);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        GLES20.glViewport(0, 0, width, height);
        float ratio = (float) width / height;
        Matrix.frustumM(mProjectionMatrix, 0, -ratio, ratio, -1, 1, 3, 7);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        final float R = Constants.SCENE_R;
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
        float x = R * (float) Math.cos(Math.toRadians(Y)) * (float) Math.cos(Math.toRadians(X));
        float z = R * (float) Math.sin(Math.toRadians(X)) * (float) Math.cos(Math.toRadians(Y));
        float y = R * (float) Math.sin(Math.toRadians(Y));
        Matrix.setLookAtM(mViewMatrix, 0, eyeX, eyeY, eyeZ, x, y, z, upX, upY, upZ);
        Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mViewMatrix, 0);
        this.drawShapes(gl);
    }

    // internet listener

    private void drawShapes(GL10 gl) {
        float[] scratch = new float[16];
        for (BleDeviceShape singleFrame : bleDeviceShapes) {
            if (singleFrame.getStatus() == Constants.CONNECTED_STATUS) {

            }
            Matrix.multiplyMM(scratch, 0, mMVPMatrix, 0, singleFrame.getmModelMatrix(), 0);
            singleFrame.draw(scratch, gl);
        }
        for (InternetDeviceShape singleFrame : internetDeviceShapes) {
            if (singleFrame.getStatus() == Constants.CONNECTED_STATUS) {

            }
            Matrix.multiplyMM(scratch, 0, mMVPMatrix, 0, singleFrame.getmModelMatrix(), 0);
            singleFrame.draw(scratch, gl);
        }
    }

    private void createShapes(List<IoTDevice> ioTDevices) {
        for (IoTDevice device : ioTDevices) {
            if (device instanceof BleDeviceWrapper) {
                BleDeviceShape deviceShape = new BleDeviceShape((BleDeviceWrapper) device, context);
//                deviceShape.setColor(Constants.INVISIBLE_COLOR);
                deviceShape.setStatusTexture(Constants.UNKNOWN_STATUS, Constants.BLE_DEVICE);
                bleDeviceShapes.add(deviceShape);
            } else if (device instanceof InternetDeviceWrapper) {
                double horizontalAngle = MathOperation.angleFromCoordinate(50.0980585, 19.9731165999, ((InternetDeviceWrapper) device).getLat(), ((InternetDeviceWrapper) device).getLon());
                InternetDeviceShape deviceShape = new InternetDeviceShape((InternetDeviceWrapper) device, horizontalAngle, context);
                internetDeviceShapes.add(deviceShape);
            }
        }
    }
    // ble listener

    public void updateInternetDevice(List<InternetDeviceWrapper> internetDeviceWrapperList) { // zrobic
        for (InternetDeviceWrapper deviceWrapper : internetDeviceWrapperList) {
            for (InternetDeviceShape deviceShape : internetDeviceShapes) {
                if (deviceShape.getId() == deviceWrapper.getId()) {
                    deviceShape.setStatus(Constants.CONNECTED_STATUS);
                    deviceShape.setStatusTexture(Constants.CONNECTED_STATUS, Constants.SERVER_DEVICE);
                    deviceShape.setUpdatetime(deviceWrapper.getUpdatetime());
                    deviceShape.setSample(deviceWrapper.getSample());
                }
            }
        }
    }

    public void setAllInternetDeviceOffline() {
        for (InternetDeviceShape device : internetDeviceShapes) {
//            device.setColor(Constants.RED_COLOR);
            device.setStatusTexture(Constants.DISCONNECTED_STATUS, Constants.SERVER_DEVICE);
            device.setStatus(Constants.DISCONNECTED_STATUS);
        }
    }

    public void updateBleDeviceStatus(String address, int status) {
        for (BleDeviceShape deviceShape : bleDeviceShapes) {
            if (deviceShape.getAddress().equals(address)) {
                switch (status) {
                    case Constants.CONNECTED_STATUS:
                        deviceShape.setStatusTexture(Constants.CONNECTED_STATUS, Constants.BLE_DEVICE);
                        deviceShape.setStatus(Constants.CONNECTED_STATUS);
                        break;
                    case Constants.DISCONNECTED_STATUS:
                        deviceShape.setStatusTexture(Constants.DISCONNECTED_STATUS, Constants.BLE_DEVICE);
                        deviceShape.setStatus(Constants.DISCONNECTED_STATUS);
                        break;
                    default:
                        deviceShape.setStatusTexture(Constants.UNKNOWN_STATUS, Constants.BLE_DEVICE);
                        deviceShape.setStatus(Constants.UNKNOWN_STATUS);
                        break;
                }
            }
        }
    }

    public void updateBleDeviceData(String address, IoTDeviceDetails deviceDetails) {
        //TODO tutaj zrobić z typem typ
        double azimuth = deviceDetails.getAzimuth();
        double pitch = deviceDetails.getPitch();
        for (BleDeviceShape deviceShape : bleDeviceShapes) {
            if (deviceShape.getAddress().equals(address)) {
                String sample = deviceDetails.getData();
                deviceShape.setSample(sample);
                deviceShape.changeCords(azimuth, pitch);
                deviceShape.setStatusTexture(Constants.CONNECTED_STATUS, Constants.BLE_DEVICE);
            }
        }
    }

    // location listener
    public void updateUserLocation(Location location) {
        for (InternetDeviceShape device : internetDeviceShapes) {
            Location deviceLocation = MathOperation.getLocation(device.getLatLng());
            double horizontalAngle = MathOperation.angleFromLocation(location, deviceLocation);
            double distance = MathOperation.measureDistanceBetweenKm(location, deviceLocation);
            device.updateDistance(distance);
            device.changeCords(horizontalAngle, 0);
        }
    }

    public void setX(float shift) {
        this.X = shift;
//        if(Math.abs(lastShiftX -shift)>1.0f){
//            this.check();
//        }
//        lastShiftX = shift;
    }

    public void setY(float shift) {
        this.Y = shift;
//        if(Math.abs(lastShiftY -shift)>2.0f){
//            this.check();
//        }
//        lastShiftY = shift;
    }

    public void setUpListener(IotTouchListener listener) {
        this.listener = listener;
    }

    public void removeListener() {
        this.listener = null;
    }


    public void showCords(float x, float y) {

        float nowy_azymt;
        float nowy_pulap = 0;
        float wynik_azimuth = 50.0f / 2200.0f;

        float wynik_pulap = 40.0f / 1080.0f;

        y = 1080f - y;
        float cos;
        if (y <= 540f) {
            cos = wynik_pulap * (540.0f - y);
            wynik_pulap = Y - cos;
        } else {
            cos = wynik_pulap * (y - 540.0f);
            wynik_pulap = Y + cos;
        }


        float wynik2;
        if (x <= 1100.f) {
            // lewo
            wynik2 = wynik_azimuth * (1100f - x);

            if (x <= 550.0f) {
                wynik2 *= 1.6;
            }
            nowy_azymt = X - wynik2;
            if (nowy_azymt < 0) nowy_azymt = 360.0f + nowy_azymt;

        } else {
            wynik2 = wynik_azimuth * (x - 1100);
//            if(x>1650.0f){
            wynik2 *= 1.6;
//            }

            nowy_azymt = X + wynik2;


        }
        nowy_azymt = Math.abs(nowy_azymt % 360);
        wynik_pulap = Math.abs(wynik_pulap % 360);
        Log.i(TAG, "tutaj kilkam " + String.valueOf(nowy_azymt) + " " + String.valueOf(wynik_pulap));


        for (BleDeviceShape deviceShape : bleDeviceShapes) {
            float aziumthPlus = deviceShape.getAzimuth() + 4.0f;
            float aziumthMinus = deviceShape.getAzimuth() - 4.0f;

            float pitchPlus = deviceShape.getPitch() + 4.0f;
            float pitchMinus = deviceShape.getPitch() - 4.0f;

            if (aziumthPlus > nowy_azymt && aziumthMinus < nowy_azymt
                    &&
                    pitchPlus > wynik_pulap && pitchMinus < wynik_pulap) {
                if (listener != null) listener.iotDeviceTouched(deviceShape);
            }
        }

        for (InternetDeviceShape deviceShape : internetDeviceShapes) {
            float aziumthPlus = deviceShape.getAzimuth() + 4.0f;
            float aziumthMinus = deviceShape.getAzimuth() - 4.0f;

            float pitchPlus = deviceShape.getPitch() + 4.0f;
            float pitchMinus = deviceShape.getPitch() - 4.0f;

            if (aziumthPlus > nowy_azymt && aziumthMinus < nowy_azymt
                    &&
                    pitchPlus > wynik_pulap && pitchMinus < wynik_pulap) {
                if (listener != null) listener.iotDeviceTouched(deviceShape);
//                Toast.makeText(context, String.valueOf(deviceShape.getSample() + " " + deviceShape.getName()
//                        + " " + MathOperation.numberToStringRound(deviceShape.getDistance(), 2)), Toast.LENGTH_SHORT).show();
//                ActivityManager manager = (ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);

            }
        }

    }


}
