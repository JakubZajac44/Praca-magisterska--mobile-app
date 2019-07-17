package com.example.jakub.arapp.application;

import com.example.jakub.arapp.MainActivity;
import com.example.jakub.arapp.arEngine.ArActivity;
import com.example.jakub.arapp.arEngine.ArManager;
import com.example.jakub.arapp.arEngine.model.Scenario;
import com.example.jakub.arapp.authManager.AuthActivity;
import com.example.jakub.arapp.bluetoothManager.BluetoothManagerModule;
import com.example.jakub.arapp.bluetoothManager.connectionManager.ConnectedBleDevice;
import com.example.jakub.arapp.bluetoothManager.connectionManager.ConnectedBleDeviceProvider;
import com.example.jakub.arapp.bluetoothManager.scanningManager.BluetoothScannerManager;
import com.example.jakub.arapp.dataBase.IoTDeviceDbModule;
import com.example.jakub.arapp.gps.GpsProvider;
import com.example.jakub.arapp.internetManager.apiConnection.ApiConnectionProvider;
import com.example.jakub.arapp.internetManager.api.InternetModule;
import com.example.jakub.arapp.motionSensor.SensorManager;
import com.example.jakub.arapp.motionSensor.gyroFilter.GyroscopeFilterProvider;
import com.example.jakub.arapp.notification.NotificationProvider;
import com.example.jakub.arapp.page.arViewPage.ArContract;
import com.example.jakub.arapp.page.arViewPage.ArFragment;
import com.example.jakub.arapp.page.authPage.AuthContract;
import com.example.jakub.arapp.page.authPage.AuthLoginFragment;
import com.example.jakub.arapp.page.authPage.AuthReloginFragment;
import com.example.jakub.arapp.page.authPage.AuthRegisterFragment;
import com.example.jakub.arapp.dialogFragment.fingerprintAuth.FingerPrintLoginDialog;
import com.example.jakub.arapp.page.bluetoothScannerPage.BluetoothScannerContract;
import com.example.jakub.arapp.page.bluetoothScannerPage.BluetoothScannerFragment;
import com.example.jakub.arapp.page.mainArPage.MainArContract;
import com.example.jakub.arapp.page.mainArPage.MainArFragment;
import com.example.jakub.arapp.page.mapPage.MapContract;
import com.example.jakub.arapp.page.mapPage.MapFragment;
import com.example.jakub.arapp.page.settingsPage.SettingsContract;
import com.example.jakub.arapp.page.settingsPage.SettingsFragment;
import com.example.jakub.arapp.service.BluetoothService;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {AppModule.class, SensorManager.MySensorManagerModule.class, ArContract.ArModule.class
        , GyroscopeFilterProvider.GyroscopeFilterModule.class, BluetoothScannerManager.BluetoothScannerManagerModule.class
        , BluetoothManagerModule.class, BluetoothScannerContract.BluetoothModule.class, IoTDeviceDbModule.class
        , MainArContract.ArSettingsModule.class, MapContract.MapModule.class, GpsProvider.GpsProviderModule.class
        , NotificationProvider.NotificationProviderModule.class, ApiConnectionProvider.ApiConnectionModule.class
        , InternetModule.class, ConnectedBleDeviceProvider.ConnectedBleDeviceProviderModule.class
        , ArManager.ArManagerProvider.class, AuthContract.AuthModule.class, SettingsContract.SettingsModule.class})
public interface AppComponent {

    void inject(ArActivity arActivity);

    void inject(ArFragment fragment);

    void inject(MainActivity mainActivity);

    void inject(BluetoothScannerFragment fragment);

    void inject(MainArFragment fragment);

    void inject(MapFragment fragment);

    void inject(AuthActivity authActivity);

    void inject(BluetoothService service);

    void inject(ConnectedBleDevice callback);

    void inject(Scenario callback);

    void inject(AuthRegisterFragment fragment);

    void inject(AuthLoginFragment fragment);

    void inject(AuthReloginFragment fragment);

    void inject(SettingsFragment fragment);

    void inject(FingerPrintLoginDialog dialog);
}
