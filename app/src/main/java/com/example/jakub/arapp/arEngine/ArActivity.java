package com.example.jakub.arapp.arEngine;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ConfigurationInfo;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.widget.Toast;

import com.example.jakub.arapp.MainActivity;
import com.example.jakub.arapp.MyApplication;
import com.example.jakub.arapp.R;
import com.example.jakub.arapp.arEngine.model.Scenario;
import com.example.jakub.arapp.arEngine.model.ScenarioListener;
import com.example.jakub.arapp.arEngine.openGLprovider.MyGLSurfaceView;
import com.example.jakub.arapp.arEngine.openGLprovider.MyRender;
import com.example.jakub.arapp.broadcastReceiver.bluetooth.BleBroadcastReceiver;
import com.example.jakub.arapp.broadcastReceiver.gps.GpsBroadcastReceiver;
import com.example.jakub.arapp.broadcastReceiver.internet.InternetBroadcastReceiver;
import com.example.jakub.arapp.motionSensor.MySensorManager;
import com.example.jakub.arapp.motionSensor.gyroFilter.GyroscopeFilterProviderImpl;
import com.example.jakub.arapp.motionSensor.sensorUtility.Orientation3d;
import com.example.jakub.arapp.page.arViewPage.ArContract;
import com.example.jakub.arapp.page.arViewPage.ArFragment;
import com.example.jakub.arapp.service.BluetoothService;
import com.example.jakub.arapp.utility.Constants;
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

public class ArActivity extends AppCompatActivity implements ScenarioListener {

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

    private MyGLSurfaceView myGlView;
    private Observer<Orientation3d> observer;
    private String currentFragmentTAG = "";
    private MyRender myRender;
    private Unbinder unbinder;
    private Timer fuseTimer = new Timer();
    private ArContract.View arFragment;
    private Scenario scenario;

    private boolean isScenarioReady;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ar);
        ((MyApplication) getApplication()).getAppComponent().inject(this);
        unbinder = ButterKnife.bind(this);
        mGLView = findViewById(R.id.glsurfaceView);
        if (isOpelGL20Available()) {
//            myGlView = new MyGLSurfaceView(mGLView,arManager.getMyRender());
            fuseTimer.scheduleAtFixedRate(new GyroscopeFilterProviderImpl().new calculateFusedOrientationTask(),
                    1000, TIME_CONSTANT);
        } else {
            Toast.makeText(context, "OpenGL20Unavailable", Toast.LENGTH_SHORT).show();
        }
        isScenarioReady=false;
        scenario = new Scenario(this);
        scenario.setUpListener(this);
        scenario.getSavedDevice();
        arFragment = new ArFragment();
        this.replaceFragment((Fragment) arFragment, ArFragment.TAG);
    }

    @Override
    protected void onStart() {
        super.onStart();
        this.registerBroadcastReceiver();
        arManager.setupListener();
//        if(isScenarioReady)  myGlView = new MyGLSurfaceView(mGLView,arManager.getMyRender());
        if (isOpelGL20Available()) {
            if (observer == null) this.createObserver();
            mySensorManager.setListener(observer);
        }
    }

    private void registerBroadcastReceiver() {
        this.registerReceiver(gpsReceiver, GpsBroadcastReceiver.makeGattUpdateIntentFilter());
        this.registerReceiver(bluetoothStateReceiver, BleBroadcastReceiver.makeGattUpdateIntentFilter());
        this.registerReceiver(internetReceiver, InternetBroadcastReceiver.makeGattUpdateIntentFilter());
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
        fuseTimer.cancel();
        fuseTimer.purge();
        arManager.removeListener();
        this.unregisterBroadcastReceiver();
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        unbinder.unbind();
        logger.log(TAG, "OnDestroy");
        this.destroyBluetoothService();
        super.onDestroy();
    }

    private void destroyBluetoothService() {
        Intent stopIntent = new Intent(ArActivity.this, BluetoothService.class);
        stopIntent.setAction(Constants.STOP_FOREGROUND_ACTION);
        startService(stopIntent);
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
            arFragment.orientationChangedText(newOrientation);
        }
        arManager.setPosition(azimuthDegrees,pitchDegrees);
      if(isScenarioReady)  myGlView.updateGlView();
    }

    @Override
    public void scenarioIsReady() {
        logger.log(TAG,"scenarioIsReady");
        isScenarioReady=true;
        myGlView = new MyGLSurfaceView(mGLView, arManager.createRender(scenario.getIotDevices()));
    }
}
