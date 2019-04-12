package com.example.jakub.arapp.application;

import com.example.jakub.arapp.MainActivity;
import com.example.jakub.arapp.arEngine.ArActivity;
import com.example.jakub.arapp.arEngine.ArManager;
import com.example.jakub.arapp.arEngine.model.Scenario;
import com.example.jakub.arapp.auth.AuthActivity;
import com.example.jakub.arapp.bluetooth.BluetoothManagerModule;
import com.example.jakub.arapp.bluetooth.connectionManager.ConnectedBleDevice;
import com.example.jakub.arapp.bluetooth.connectionManager.ConnectedBleDeviceProvider;
import com.example.jakub.arapp.bluetooth.scanningManager.BluetoothScannerManager;
import com.example.jakub.arapp.dataBase.IoTDeviceDbModule;
import com.example.jakub.arapp.gps.GpsProvider;
import com.example.jakub.arapp.internet.api.InternetModule;
import com.example.jakub.arapp.internet.backendConnection.BackendConnectionProvider;
import com.example.jakub.arapp.motionSensor.MySensorManager;
import com.example.jakub.arapp.motionSensor.gyroFilter.GyroscopeFilterProvider;
import com.example.jakub.arapp.notification.NotificationProvider;
import com.example.jakub.arapp.page.arViewPage.ArContract;
import com.example.jakub.arapp.page.arViewPage.ArFragment;
import com.example.jakub.arapp.page.authPage.AuthContract;
import com.example.jakub.arapp.page.authPage.AuthFragment;
import com.example.jakub.arapp.page.bluetoothDeviceListPage.BluetoothContract;
import com.example.jakub.arapp.page.bluetoothDeviceListPage.BluetoothFragment;
import com.example.jakub.arapp.page.mainArPage.MainArContract;
import com.example.jakub.arapp.page.mainArPage.MainArFragment;
import com.example.jakub.arapp.page.mapPage.MapContract;
import com.example.jakub.arapp.page.mapPage.MapFragment;
import com.example.jakub.arapp.service.BluetoothService;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {AppModule.class, MySensorManager.MySensorManagerModule.class, ArContract.ArModule.class
        , GyroscopeFilterProvider.GyroscopeFilterModule.class, BluetoothScannerManager.BluetoothScannerManagerModule.class
        , BluetoothManagerModule.class, BluetoothContract.BluetoothModule.class, IoTDeviceDbModule.class
        , MainArContract.ArSettingsModule.class, MapContract.ArSettingsModule.class, GpsProvider.GpsProviderModule.class
        , NotificationProvider.NotificationProviderModule.class, BackendConnectionProvider.BackendConnectionModule.class
        , InternetModule.class, ConnectedBleDeviceProvider.ConnectedBleDeviceProviderModule.class
        , ArManager.ArManagerProvider.class, AuthContract.AuthModule.class})
public interface AppComponent {

    void inject(ArActivity arActivity);

    void inject(ArFragment fragment);

    void inject(MainActivity mainActivity);

    void inject(BluetoothFragment fragment);

    void inject(MainArFragment fragment);

    void inject(MapFragment fragment);

    void inject(AuthActivity authActivity);

    void inject(BluetoothService service);

    void inject(ConnectedBleDevice callback);

    void inject(Scenario callback);

    void inject(AuthFragment fragment);

}
