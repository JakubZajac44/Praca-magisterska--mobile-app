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
import com.example.jakub.arapp.auth.AuthActivity;
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
import com.example.jakub.arapp.page.settingsPage.SettingsContract;
import com.example.jakub.arapp.page.settingsPage.SettingsFragment;
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
    private boolean doubleBackToExitPressedOnce = false;
    private boolean isRunningWithoutLogin = false;
    private String currentFragmentTAG = "";
    private BluetoothContract.View bluetoothDeviceListFragment;
    private MainArContract.View mainArFragment;
    private MapContract.View mapFragment;
    private SettingsContract.View settingsFragment;
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
        mapFragment = new MapFragment();
        settingsFragment = new SettingsFragment();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ((MyApplication) getApplication()).getAppComponent().inject(this);
        isRunningWithoutLogin = sharedPreferences.getBoolean(Constants.RUNNING_WITHOUT_LOGIN, false);
        unbinder = ButterKnife.bind(this);
        listPermissionsNeeded = new ArrayList<>();
        this.setBottomNavigationListener();
        this.replaceFragment((Fragment) mainArFragment, MainArFragment.TAG);
        bottomNavigationView.setSelectedItemId(R.id.ar_navigation);
        this.createBluetoothService();
    }

    private void setBottomNavigationListener() {
        bottomNavigationView.setOnNavigationItemSelectedListener(menuItem -> {

            if (bottomNavigationView.getSelectedItemId() != menuItem.getItemId()) {

                Fragment nextFragment = null;
                String tag = "";
                boolean fragmentShouldBeChanged = true;

                switch (menuItem.getItemId()) {
                    case R.id.map_navigation:
                        if (isRunningWithoutLogin) {
                            fragmentShouldBeChanged = false;
                            Toast.makeText(this, getResources().getString(R.string.running_without_login), Toast.LENGTH_SHORT).show();
                        } else {
                            nextFragment = (Fragment) mapFragment;
                            tag = MapFragment.TAG;
                        }
                        break;

                    case R.id.ar_navigation:
                        nextFragment = (Fragment) mainArFragment;
                        tag = MainArFragment.TAG;
                        break;

                    case R.id.ble_navigation:
                        nextFragment = (Fragment) bluetoothDeviceListFragment;
                        tag = BluetoothFragment.TAG;
                        break;
                    case R.id.settings_navigation:
                            nextFragment = (Fragment) settingsFragment;
                            tag = SettingsFragment.TAG;
                }
                if (fragmentShouldBeChanged) this.replaceFragment(nextFragment, tag);

                return fragmentShouldBeChanged;
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
        logger.log(TAG, "onStart");
        this.registerBroadcastReceiver();
        super.onStart();
    }

    @Override
    protected void onResume() {
        logger.log(TAG, "onResume");
        super.onResume();
    }

    private void registerBroadcastReceiver() {
        this.registerReceiver(gpsReceiver, GpsBroadcastReceiver.makeGattUpdateIntentFilter());
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
        logger.log(TAG, "onStop");
        this.unregisterBroadcastReceiver();
        super.onStop();
    }

    private void unregisterBroadcastReceiver() {
        unregisterReceiver(bluetoothStateReceiver);
        unregisterReceiver(gpsReceiver);
        unregisterReceiver(internetReceiver);
    }

    @Override
    protected void onDestroy() {
        logger.log(TAG, "onDestroy");
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

    public void startAuthActivity() {
        onDestroy();
        Intent myIntent = new Intent(MainActivity.this, AuthActivity.class);
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
