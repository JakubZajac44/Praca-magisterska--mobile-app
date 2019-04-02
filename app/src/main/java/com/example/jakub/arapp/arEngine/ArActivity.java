package com.example.jakub.arapp.arEngine;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ConfigurationInfo;
import android.graphics.PixelFormat;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.widget.Toast;

import com.example.jakub.arapp.MyApplication;
import com.example.jakub.arapp.R;
import com.example.jakub.arapp.arEngine.openGLprovider.MyRender;
import com.example.jakub.arapp.broadcastReceiver.bluetooth.BleBroadcastReceiver;
import com.example.jakub.arapp.broadcastReceiver.gps.GpsBroadcastReceiver;
import com.example.jakub.arapp.broadcastReceiver.internet.InternetBroadcastReceiver;
import com.example.jakub.arapp.motionSensor.MySensorManager;
import com.example.jakub.arapp.motionSensor.gyroFilter.GyroscopeFilterProviderImpl;
import com.example.jakub.arapp.motionSensor.sensorUtility.Orientation3d;
import com.example.jakub.arapp.page.arViewPage.ArContract;
import com.example.jakub.arapp.page.arViewPage.ArFragment;
import com.example.jakub.arapp.utility.Logger;

import java.util.Timer;

import javax.inject.Inject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

public class ArActivity extends AppCompatActivity {

    public static final String TAG = ArActivity.class.getSimpleName();
    public static final int TIME_CONSTANT = 30;
    @Inject
    public Context context;
    @Inject
    public Logger logger;
    @Inject
    public MySensorManager mySensorManager;
    @Inject
    public GyroscopeFilterProviderImpl gyroscopeFilterProviderImpl;
    @Inject
    public ArManager arManager;
    @Inject
    public BleBroadcastReceiver bluetoothStateReceiver;
    @Inject
    public InternetBroadcastReceiver internetReceiver;
    @Inject
    public GpsBroadcastReceiver gpsReceiver;



    @BindView(R.id.glsurfaceView)
    GLSurfaceView mGLView;
    Observer<Orientation3d> observer;
    private String currentFragmentTAG = "";
    private MyRender myRender;
    private Unbinder unbinder;
    private Timer fuseTimer = new Timer();
    private ArContract.View arFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ar);
        ((MyApplication) getApplication()).getAppComponent().inject(this);
        unbinder = ButterKnife.bind(this);
        mGLView = findViewById(R.id.glsurfaceView);
        if (isOpelGL20Available()) {
            myRender = new MyRender(this);
            mGLView.setEGLContextClientVersion(2);
            mGLView.setPreserveEGLContextOnPause(true);
            mGLView.setZOrderOnTop(true);
            mGLView.setEGLConfigChooser(8, 8, 8, 8, 16, 0);
            mGLView.getHolder().setFormat(PixelFormat.RGBA_8888);
            mGLView.setRenderer(myRender);
            mGLView.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);

            fuseTimer.scheduleAtFixedRate(new GyroscopeFilterProviderImpl().new calculateFusedOrientationTask(),
                    1000, TIME_CONSTANT);
        } else {
            Toast.makeText(context, "OpenGL20Unavailable", Toast.LENGTH_SHORT).show();
        }
        arFragment = new ArFragment();
        this.replaceFragment((Fragment) arFragment, ArFragment.TAG);
    }

    @Override
    protected void onStart() {
        super.onStart();
        this.registerBroadcastReceiver();
        arManager.setupListener();
        arManager.addScenario();
        if (!isOpelGL20Available()) {
            if (observer == null) this.createObserver();
            mySensorManager.setListener(observer);
        }
    }

    private void registerBroadcastReceiver() {
        this.registerReceiver(gpsReceiver, GpsBroadcastReceiver.makeGattUpdateIntentFilter());
        this.registerReceiver(bluetoothStateReceiver, BleBroadcastReceiver.makeGattUpdateIntentFilter());
        this.registerReceiver(internetReceiver, InternetBroadcastReceiver.makeGattUpdateIntentFilter());
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    private boolean isOpelGL20Available() {
        ActivityManager am = (ActivityManager)
                getSystemService(Context.ACTIVITY_SERVICE);
        ConfigurationInfo info = am.getDeviceConfigurationInfo();
        return info.reqGlEsVersion >= 0x20000;
    }

    private void createObserver() {
        observer = new Observer<Orientation3d>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(Orientation3d orientation3d) {
                positionDeviceChanged(orientation3d);
            }

            @Override
            public void onError(Throwable e) {
                logger.log(TAG, " Error received" + e.toString());
            }

            @Override
            public void onComplete() {
                logger.log(TAG, "All data emitted.");
            }
        };
    }

    public <T extends Fragment> void replaceFragment(T fragment, String TAG) {
        this.currentFragmentTAG = TAG;
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.contentApp, fragment, TAG);
        ft.addToBackStack(null);
        ft.commit();
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    protected void onStop() {
        logger.log(TAG,"onStop");
        observer.onComplete();
        observer = null;
        mySensorManager.removeListener();
        unbinder.unbind();
        fuseTimer.cancel();
        fuseTimer.purge();
        arManager.removeListener();
        this.unregisterBroadcastReceiver();
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        logger.log(TAG, "OnDestroy");
        super.onDestroy();
    }

    private void unregisterBroadcastReceiver(){
        unregisterReceiver(bluetoothStateReceiver);
        unregisterReceiver(gpsReceiver);
        unregisterReceiver(internetReceiver);
    }

    public void positionDeviceChanged(Orientation3d newOrientation) {
        float azimuthDegrees = newOrientation.getAzimuthDegrees();
        float pitchDegrees = newOrientation.getPitchDegrees();
        if (arFragment != null && arFragment.isVisible()) {
            arFragment.orientationChanged(newOrientation);
        }
        myRender.setX(0.0175f * (360.0f - azimuthDegrees));
        myRender.setY(0.0175f * (360.0f - pitchDegrees));
        mGLView.requestRender();

    }

}
