package com.example.jakub.arapp.motionSensor.gyroFilter;

import android.content.Context;
import android.hardware.SensorEvent;

import com.example.jakub.arapp.motionSensor.SensorManager;
import com.example.jakub.arapp.motionSensor.sensorUtility.Orientation3d;
import com.example.jakub.arapp.utility.Logger;

import java.util.TimerTask;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class GyroscopeFilterProviderImpl implements GyroscopeFilterProvider {

   @Inject
   public Orientation3d lastOrientation3d;

    @Inject
    public Logger logger;

    @Inject
    public Context context;

    private static final String TAG = GyroscopeFilterProviderImpl.class.getSimpleName();
    public static final float EPSILON = 0.000000001f;
    public static final float FILTER_COEFFICIENT = 0.98f;
    private static final float NS2S = 1.0f / 1000000000.0f;
    private SensorManager listener;
    int axisX;
    int axisY;
    private boolean initState = true;
    private float timestamp;
    private Orientation3d orientation3d;
    private float[] gyroMatrix = new float[9];
    private float[] gyroOrientation = new float[3];
    private float[] gyro = new float[3];
    private float[] orientationValuesRemap = new float[9];
    private float[] fusedOrientation = new float[3];
    private float[] accMagOrientation = new float[3];


    @Inject
    public GyroscopeFilterProviderImpl() {
        this.initTable();
    }

    public void setListener(SensorManager listener) {

        this.listener = listener;
    }

    private void initTable() {
        gyroOrientation[0] = 0.0f;
        gyroOrientation[1] = 0.0f;
        gyroOrientation[2] = 0.0f;

        gyroMatrix[0] = 1.0f;
        gyroMatrix[1] = 0.0f;
        gyroMatrix[2] = 0.0f;
        gyroMatrix[3] = 0.0f;
        gyroMatrix[4] = 1.0f;
        gyroMatrix[5] = 0.0f;
        gyroMatrix[6] = 0.0f;
        gyroMatrix[7] = 0.0f;
        gyroMatrix[8] = 1.0f;
    }

    public void gyroFunction(SensorEvent event, float[] accMagOrientation) {
        // don't start until first accelerometerSensor/magnetometer orientation has been acquired
        if (accMagOrientation == null)
            return;

        // initialisation of the gyroscopeSensor based rotation matrix
        if (initState) {
            float[] initMatrix = new float[9];
            initMatrix = getRotationMatrixFromOrientation(accMagOrientation);
            float[] test = new float[3];
            android.hardware.SensorManager.getOrientation(initMatrix, test);
            gyroMatrix = matrixMultiplication(gyroMatrix, initMatrix);
            initState = false;
        }

        // copy the new gyro values into the gyro array
        // convert the raw gyro data into a rotation vector
        System.arraycopy(accMagOrientation, 0, this.accMagOrientation, 0, 3);
        float[] deltaVector = new float[4];
        if (timestamp != 0) {
            final float dT = (event.timestamp - timestamp) * NS2S;
            System.arraycopy(event.values, 0, gyro, 0, 3);
            getRotationVectorFromGyro(gyro, deltaVector, dT / 2.0f);
        }

        // measurement done, save current time for next interval
        timestamp = event.timestamp;

        // convert rotation vector into rotation matrix
        float[] deltaMatrix = new float[9];

//        axisX = SensorManager.AXIS_X;
//        axisY = SensorManager.AXIS_Z;

        axisX = android.hardware.SensorManager.AXIS_X;
        axisY = android.hardware.SensorManager.AXIS_Z;

        android.hardware.SensorManager.getRotationMatrixFromVector(deltaMatrix, deltaVector);
        gyroMatrix = matrixMultiplication(gyroMatrix, deltaMatrix);

//        int rotation = ((WindowManager) context.getApplicationContext().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getRotation();
//        if(rotation == 0) // Default display rotation is portrait
//            SensorManager.remapCoordinateSystem(gyroMatrix, SensorManager.AXIS_MINUS_X, SensorManager.AXIS_Y, orientationValuesRemap);
//        else   // Default display rotation is landscape
//            SensorManager.remapCoordinateSystem(gyroMatrix, SensorManager.AXIS_Y, SensorManager.AXIS_MINUS_X, orientationValuesRemap);

        android.hardware.SensorManager.remapCoordinateSystem(gyroMatrix, axisX, axisY, orientationValuesRemap);
        android.hardware.SensorManager.getOrientation(orientationValuesRemap, gyroOrientation);
        orientation3d = new Orientation3d(gyroOrientation[0], gyroOrientation[1], gyroOrientation[2]);

        if (this.shouldActualize(orientation3d)) {
            listener.actualizeDevicePosition(orientation3d);
        }
        lastOrientation3d.updateOrientation(orientation3d);
    }

    private void getRotationVectorFromGyro(float[] gyroValues,
                                           float[] deltaRotationVector,
                                           float timeFactor) {
        float[] normValues = new float[3];

        // Calculate the angular speed of the sample
        float omegaMagnitude =
                (float) Math.sqrt(gyroValues[0] * gyroValues[0] +
                        gyroValues[1] * gyroValues[1] +
                        gyroValues[2] * gyroValues[2]);

        // Normalize the rotation vector if it's big enough to get the axis
        if (omegaMagnitude > EPSILON) {
            normValues[0] = gyroValues[0] / omegaMagnitude;
            normValues[1] = gyroValues[1] / omegaMagnitude;
            normValues[2] = gyroValues[2] / omegaMagnitude;
        }

        // Integrate around this axis with the angular speed by the timestep
        // in order to get a delta rotation from this sample over the timestep
        // We will convert this axis-angle representation of the delta rotation
        // into a quaternion before turning it into the rotation matrix.
        float thetaOverTwo = omegaMagnitude * timeFactor;
        float sinThetaOverTwo = (float) Math.sin(thetaOverTwo);
        float cosThetaOverTwo = (float) Math.cos(thetaOverTwo);
        deltaRotationVector[0] = sinThetaOverTwo * normValues[0];
        deltaRotationVector[1] = sinThetaOverTwo * normValues[1];
        deltaRotationVector[2] = sinThetaOverTwo * normValues[2];
        deltaRotationVector[3] = cosThetaOverTwo;
    }

    private float[] getRotationMatrixFromOrientation(float[] o) {
        float[] xM = new float[9];
        float[] yM = new float[9];
        float[] zM = new float[9];

        float sinX = (float) Math.sin(o[1]);
        float cosX = (float) Math.cos(o[1]);
        float sinY = (float) Math.sin(o[2]);
        float cosY = (float) Math.cos(o[2]);
        float sinZ = (float) Math.sin(o[0]);
        float cosZ = (float) Math.cos(o[0]);

        // rotation about x-axis (pitch)
        xM[0] = 1.0f;
        xM[1] = 0.0f;
        xM[2] = 0.0f;
        xM[3] = 0.0f;
        xM[4] = cosX;
        xM[5] = sinX;
        xM[6] = 0.0f;
        xM[7] = -sinX;
        xM[8] = cosX;

        // rotation about y-axis (roll)
        yM[0] = cosY;
        yM[1] = 0.0f;
        yM[2] = sinY;
        yM[3] = 0.0f;
        yM[4] = 1.0f;
        yM[5] = 0.0f;
        yM[6] = -sinY;
        yM[7] = 0.0f;
        yM[8] = cosY;

        // rotation about z-axis (azimuth)
        zM[0] = cosZ;
        zM[1] = sinZ;
        zM[2] = 0.0f;
        zM[3] = -sinZ;
        zM[4] = cosZ;
        zM[5] = 0.0f;
        zM[6] = 0.0f;
        zM[7] = 0.0f;
        zM[8] = 1.0f;

        // rotation order is y, x, z (roll, pitch, azimuth)
        float[] resultMatrix = matrixMultiplication(xM, yM);
        resultMatrix = matrixMultiplication(zM, resultMatrix);
        return resultMatrix;
    }

    private float[] matrixMultiplication(float[] A, float[] B)  {
        float[] result = new float[9];

        result[0] = A[0] * B[0] + A[1] * B[3] + A[2] * B[6];
        result[1] = A[0] * B[1] + A[1] * B[4] + A[2] * B[7];
        result[2] = A[0] * B[2] + A[1] * B[5] + A[2] * B[8];

        result[3] = A[3] * B[0] + A[4] * B[3] + A[5] * B[6];
        result[4] = A[3] * B[1] + A[4] * B[4] + A[5] * B[7];
        result[5] = A[3] * B[2] + A[4] * B[5] + A[5] * B[8];

        result[6] = A[6] * B[0] + A[7] * B[3] + A[8] * B[6];
        result[7] = A[6] * B[1] + A[7] * B[4] + A[8] * B[7];
        result[8] = A[6] * B[2] + A[7] * B[5] + A[8] * B[8];

        return result;
    }


    public class calculateFusedOrientationTask extends TimerTask {
        public void run() {
            float oneMinusCoeff = 1.0f - FILTER_COEFFICIENT;
            fusedOrientation[0] =
                    FILTER_COEFFICIENT * gyroOrientation[0]
                            + oneMinusCoeff * accMagOrientation[0];
            fusedOrientation[1] =
                    FILTER_COEFFICIENT * gyroOrientation[1]
                            + oneMinusCoeff * accMagOrientation[1];
            fusedOrientation[2] =
                    FILTER_COEFFICIENT * gyroOrientation[2]
                            + oneMinusCoeff * accMagOrientation[2];
            gyroMatrix = getRotationMatrixFromOrientation(fusedOrientation);
            System.arraycopy(fusedOrientation, 0, gyroOrientation, 0, 3);
        }
    }

    private boolean shouldActualize(Orientation3d orientation3d) {
//        if(logger==null) Log.i(TAG,"null");
//        else Log.i(TAG,"not null");
        if (lastOrientation3d.isVectorReady()) {
            if (Math.abs(lastOrientation3d.getAzimuthDegrees() - orientation3d.getAzimuthDegrees()) > 0.01f
                    || Math.abs(lastOrientation3d.getPitchDegrees() - orientation3d.getPitchDegrees()) > 0.01f
                    || Math.abs(lastOrientation3d.getRollDegrees() - orientation3d.getRollDegrees()) > 0.01f) {
                return true;
            }
        }
        return false;
    }
}
