package com.example.jakub.arapp;

import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.widget.Toast;

import com.example.jakub.arapp.application.ConfigApp;
import com.example.jakub.arapp.arEngine.ArActivity;
import com.example.jakub.arapp.broadcastReceiver.bluetooth.BleBroadcastReceiver;
import com.example.jakub.arapp.broadcastReceiver.gps.GpsBroadcastReceiver;
import com.example.jakub.arapp.broadcastReceiver.internet.InternetBroadcastReceiver;
import com.example.jakub.arapp.dataBase.repository.ble.BleDeviceRepository;
import com.example.jakub.arapp.page.bluetoothDeviceListPage.BluetoothContract;
import com.example.jakub.arapp.page.bluetoothDeviceListPage.BluetoothFragment;
import com.example.jakub.arapp.page.mainArPage.MainArContract;
import com.example.jakub.arapp.page.mainArPage.MainArFragment;
import com.example.jakub.arapp.page.mapPage.MapContract;
import com.example.jakub.arapp.page.mapPage.MapFragment;
import com.example.jakub.arapp.service.BluetoothService;
import com.example.jakub.arapp.utility.Constants;
import com.example.jakub.arapp.utility.Logger;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;


public class MainActivity extends AppCompatActivity {
    public static final String TAG = MainActivity.class.getSimpleName();
    private final int MULTIPLE_PERMISSIONS = 101;

    @Inject
    public BleBroadcastReceiver bluetoothStateReceiver;
    @Inject
    public InternetBroadcastReceiver internetReceiver;
    @Inject
    public GpsBroadcastReceiver gpsReceiver;
    @Inject
    ConfigApp configApp;
    @Inject
    BleDeviceRepository bleDeviceRepository;
    @Inject
    Logger logger;
    @Inject
    SharedPreferences.Editor sharedPreferencesEditor;
    @Inject
    SharedPreferences sharedPreferences;

    @BindView(R.id.bottom_navigation)
    BottomNavigationView bottomNavigationView;


    Unbinder unbinder;
    boolean doubleBackToExitPressedOnce = false;
    private String currentFragmentTAG = "";
    private BluetoothContract.View bluetoothDeviceListFragment;
    private MainArContract.View mainArFragment;
    private MapContract.View listDeviceFragment;
    private List<String> listPermissionsNeeded;
    private BluetoothService bluetoothService;
    private ServiceConnection serviceConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            String name = className.getClassName();
            if (name.endsWith(BluetoothService.class.getSimpleName())) {
                bluetoothService = ((BluetoothService.BluetoothServiceBinder) service).getService();
            }
        }

        public void onServiceDisconnected(ComponentName className) {
            if (className.getClassName().equals(BluetoothService.class.getSimpleName())) {
                bluetoothService = null;
            }
        }
    };

    public MainActivity() {
        bluetoothDeviceListFragment = new BluetoothFragment();
        mainArFragment = new MainArFragment();
        listDeviceFragment = new MapFragment();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ((MyApplication) getApplication()).getAppComponent().inject(this);
        unbinder = ButterKnife.bind(this);
        logger.log(TAG, "onCreate");
        listPermissionsNeeded = new ArrayList<>();
        this.setBottomNavigationListener();
        this.replaceFragment((Fragment) mainArFragment, MainArFragment.TAG);
        bottomNavigationView.setSelectedItemId(R.id.ar_navigation);
        showPermissionRationale(configApp.getPermissions());
        this.createBluetoothService();
    }

    private void setBottomNavigationListener(){
        bottomNavigationView.setOnNavigationItemSelectedListener(menuItem -> {

            if (bottomNavigationView.getSelectedItemId() != menuItem.getItemId()) {

                Fragment nextFragment = null;
                String tag = "";

                switch (menuItem.getItemId()) {
                    case R.id.list_navigation:
                        nextFragment = (Fragment) listDeviceFragment;
                        tag = MapFragment.TAG;
                        break;

                    case R.id.ar_navigation:
                        nextFragment = (Fragment) mainArFragment;
                        tag = MainArFragment.TAG;
                        break;

                    case R.id.ble_navigation:
                        nextFragment = (Fragment) bluetoothDeviceListFragment;
                        tag = BluetoothFragment.TAG;
                        break;
                }
                this.replaceFragment(nextFragment, tag);

                return true;
            } else return false;
        });
    }

    private void createBluetoothService() {
        final Intent intent = new Intent(this, BluetoothService.class);
        intent.setAction(Constants.START_FOREGROUND_ACTION);
        this.startService(intent);
        this.bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onStart() {
        this.registerBroadcastReceiver();
        super.onStart();
    }

    private void registerBroadcastReceiver() {
        this.registerReceiver(gpsReceiver,GpsBroadcastReceiver.makeGattUpdateIntentFilter());
        this.registerReceiver(bluetoothStateReceiver, BleBroadcastReceiver.makeGattUpdateIntentFilter());
        this.registerReceiver(internetReceiver, InternetBroadcastReceiver.makeGattUpdateIntentFilter());
    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            onDestroy();
        } else {
            this.doubleBackToExitPressedOnce = true;
            Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();
        }
        new Handler().postDelayed(() -> doubleBackToExitPressedOnce = false, 2000);
    }

    @Override
    protected void onStop() {
        this.unregisterBroadcastReceiver();
        super.onStop();
    }

    private void unregisterBroadcastReceiver(){
        unregisterReceiver(bluetoothStateReceiver);
        unregisterReceiver(gpsReceiver);
        unregisterReceiver(internetReceiver);
    }

    @Override
    protected void onDestroy() {
        logger.log(TAG,"onDestroy");
        unbinder.unbind();
        this.unbindService(serviceConnection);
        this.destroyBluetoothService();
        finish();
        super.onDestroy();
    }

    private void destroyBluetoothService() {
        Intent stopIntent = new Intent(MainActivity.this, BluetoothService.class);
        stopIntent.setAction(Constants.STOP_FOREGROUND_ACTION);
        startService(stopIntent);
    }

    public <T extends Fragment> void replaceFragment(T fragment, String TAG) {
        this.currentFragmentTAG = TAG;
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.mainActivityFrame, fragment, TAG);
        ft.addToBackStack(null);
        ft.commit();
    }

    public void startNewActivity() {
        Intent myIntent = new Intent(MainActivity.this, ArActivity.class);
        MainActivity.this.startActivity(myIntent);

    }

    public void repeatShowPermission() {
        showPermissionRationale(configApp.getPermissions());
    }

    private void showPermissionRationale(String[] permissions) {
        boolean shouldShow = false;
        listPermissionsNeeded.clear();
        for (String p : permissions) {
            logger.log(TAG, p);
            if (ContextCompat.checkSelfPermission(this, p) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.shouldShowRequestPermissionRationale(this, p)) {
                shouldShow = true;
            }
            listPermissionsNeeded.add(p);
        }
        if (shouldShow) {
            new AlertDialog.Builder(this)
                    .setTitle(R.string.title_location_permission)
                    .setMessage(R.string.text_location_permission)
                    .setPositiveButton(R.string.ok, (dialogInterface, i) -> checkNeededPermission())
                    .create()
                    .show();
        } else {
            checkNeededPermission();
        }
    }

    private void checkNeededPermission() {
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), MULTIPLE_PERMISSIONS);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        boolean allPermissionGranted = false;
        switch (requestCode) {
            case MULTIPLE_PERMISSIONS: {
                if (grantResults.length > 0) {
                    for (int grantPermission : grantResults) {
                        if (grantPermission == PackageManager.PERMISSION_DENIED) {
                            allPermissionGranted = false;
                            break;
                        } else {
                            allPermissionGranted = true;
                        }
                    }
                    logger.log(TAG, allPermissionGranted);
                    sharedPreferencesEditor.putBoolean(ConfigApp.PERMISSION_KEY, allPermissionGranted);
                    sharedPreferencesEditor.commit();
                }
                return;
            }
        }
    }


}
