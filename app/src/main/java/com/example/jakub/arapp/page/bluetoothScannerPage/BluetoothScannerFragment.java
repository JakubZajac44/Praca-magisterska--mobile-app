package com.example.jakub.arapp.page.bluetoothScannerPage;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.jakub.arapp.MainActivity;
import com.example.jakub.arapp.MyApplication;
import com.example.jakub.arapp.R;
import com.example.jakub.arapp.application.ConfigApp;
import com.example.jakub.arapp.model.device.BleDeviceWrapper;
import com.example.jakub.arapp.dataBase.data.ble.BleDevice;
import com.example.jakub.arapp.dataBase.repository.ble.BleDeviceRepository;
import com.example.jakub.arapp.page.bluetoothScannerPage.bleDeviceList.BleDeviceListViewAdapter;
import com.example.jakub.arapp.page.bluetoothScannerPage.bleDeviceList.ListViewBleDeviceItem;
import com.example.jakub.arapp.utility.Logger;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import androidx.fragment.app.Fragment;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;


public class BluetoothScannerFragment extends Fragment implements BluetoothScannerContract.View {


    public static final String TAG = BluetoothScannerFragment.class.getSimpleName();

    @Inject
    SharedPreferences sharedPreferences;

    @Inject
    public BluetoothScannerContract.Presenter presenter;

    @Inject
    public Context context;

    @Inject
    public Logger logger;

    @Inject
    public BleDeviceRepository repository;

    @BindView(R.id.bleDeviceItemListView)
    public ListView bleDeviceListView;

    @BindView(R.id.scanningBleDeviceProgressBar)
    public ProgressBar progressBar;

    List<BleDeviceWrapper> bluetoothDeviceList;
    private Unbinder unbinder;
    private BleDeviceListViewAdapter listViewDataAdapter;
    private List<ListViewBleDeviceItem> bleDeviceItemList;
    private List<BleDevice> bleDevicesToSave;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((MyApplication) getActivity().getApplication()).getAppComponent().inject(this);
        bleDeviceItemList = new ArrayList<>();
        bleDevicesToSave = new ArrayList<>();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bluetooth_device_list_fragment, container, false);
        presenter.attachView(this);
        unbinder = ButterKnife.bind(this, view);

        progressBar.setVisibility(View.INVISIBLE);
        listViewDataAdapter = new BleDeviceListViewAdapter(context, bleDeviceItemList);
        bleDeviceListView.setAdapter(listViewDataAdapter);
        this.setListViewListener();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        bleDeviceItemList.clear();
        listViewDataAdapter.notifyDataSetChanged();
    }

    private void setListViewListener() {
        bleDeviceListView.setOnItemClickListener((adapterView, view, itemIndex, l) -> {
            Object itemObject = adapterView.getAdapter().getItem(itemIndex);

            ListViewBleDeviceItem itemDto = (ListViewBleDeviceItem) itemObject;

            CheckBox itemCheckbox = view.findViewById(R.id.bleDeviceItemCheckBox);
            if (itemDto.isChecked()) {
                itemCheckbox.setChecked(false);
                itemDto.setChecked(false);
            } else {
                itemCheckbox.setChecked(true);
                itemDto.setChecked(true);
            }
        });
    }

    @OnClick(R.id.scanBleDeviceButton)
    public void startScanClick() {
        boolean isPermissionGranted = sharedPreferences.getBoolean(ConfigApp.PERMISSION_KEY,false);
        if(isPermissionGranted){
            this.clearAllList();
            progressBar.setVisibility(View.VISIBLE);
            presenter.startScanBleDevice();
        }
        else{
            ((MainActivity)getActivity()).repeatShowPermission();
            this.showMessage(context.getResources().getString(R.string.permission_not_granted));
        }
    }

    @Override
    public void setUpAdapterData(List<BleDeviceWrapper> bluetoothDeviceList) {
        this.bluetoothDeviceList = bluetoothDeviceList;
        for (BleDeviceWrapper device : bluetoothDeviceList) {
            bleDeviceItemList.add(new ListViewBleDeviceItem(device));
        }
        progressBar.setVisibility(View.INVISIBLE);
        listViewDataAdapter.notifyDataSetChanged();
    }
    private void clearAllList() {
        bleDeviceItemList.clear();
        listViewDataAdapter.notifyDataSetChanged();
    }

    @OnClick(R.id.connectAndSaveBleDeviceButton)
    public void saveToDataBase() {

        for (ListViewBleDeviceItem item : bleDeviceItemList) {
            if (item.isChecked())
                bleDevicesToSave.add(new BleDevice(item.getItemAddressText(), item.getItemNameText()));
        }
        if (!bleDevicesToSave.isEmpty()) {
            presenter.saveBleDevices(bleDevicesToSave);
        }

    }

    @Override
    public void removeItemFromList() {
        bleDevicesToSave.clear();
        for (ListViewBleDeviceItem item : bleDeviceItemList) {
            item.setChecked(false);
        }
        listViewDataAdapter.notifyDataSetChanged();
    }

    @Override
    public void showMessage(String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroy() {
        unbinder.unbind();
        presenter.detachView();
        super.onDestroy();
    }




}
