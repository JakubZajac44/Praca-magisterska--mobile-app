package com.example.jakub.arapp.page.mainArPage;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.example.jakub.arapp.MainActivity;
import com.example.jakub.arapp.MyApplication;
import com.example.jakub.arapp.R;
import com.example.jakub.arapp.application.ConfigApp;
import com.example.jakub.arapp.broadcastReceiver.internet.InternetBroadcastReceiver;
import com.example.jakub.arapp.dialogFragment.SimpleChoiceDialog;
import com.example.jakub.arapp.model.device.BleDeviceWrapper;
import com.example.jakub.arapp.model.device.InternetDeviceWrapper;
import com.example.jakub.arapp.model.device.IoTDevice;
import com.example.jakub.arapp.page.mainArPage.bleDeviceStatusList.ListViewBleDeviceStatusAdapter;
import com.example.jakub.arapp.utility.Constants;
import com.example.jakub.arapp.utility.Logger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class MainArFragment extends Fragment implements MainArContract.View {

    public static final String TAG = MainArFragment.class.getSimpleName();
    public static final int DIALOG_FRAGMENT = 1;

    @Inject
    InternetBroadcastReceiver internetBroadcastReceiver;

    @BindView(R.id.bleDeviceStatusListView)
    ListView bleDeviceStatusListView;

    @Inject
    SharedPreferences sharedPreferences;

    @Inject
    Context context;
    @Inject
    MainArContract.Presenter presenter;
    @Inject
    Logger logger;

    private Unbinder unbinder;
    private ListViewBleDeviceStatusAdapter adapter;
    private List<IoTDevice> ioTDeviceList;

    private int itemPosition;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((MyApplication) getActivity().getApplication()).getAppComponent().inject(this);
        ioTDeviceList = Collections.synchronizedList(new ArrayList<>());
        logger.log(TAG, "onCreate");
        adapter = new ListViewBleDeviceStatusAdapter(context, ioTDeviceList);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.ar_settings_fragment, container, false);
        unbinder = ButterKnife.bind(this, view);
        bleDeviceStatusListView.setAdapter(adapter);
        this.setListViewListener();
        presenter.attachView(this);
        logger.log(TAG, "onCreateView");
        return view;
    }

    private void setListViewListener() {
        bleDeviceStatusListView.setOnItemLongClickListener((parent, view, position, id) -> {
            itemPosition = position;
            showDialogFragment();
            return false;
        });
    }

    private void showDialogFragment() {
        FragmentManager manager = getFragmentManager();
        SimpleChoiceDialog simpleChoiceDialog = new SimpleChoiceDialog();
        simpleChoiceDialog.setTargetFragment(this, DIALOG_FRAGMENT);
        simpleChoiceDialog.show(manager, SimpleChoiceDialog.TAG);
    }

    @Override
    public void onResume() {
        logger.log(TAG, "onResume");
        this.removeDeviceFromList();
        presenter.getSavedDevice();
        super.onResume();
    }

    @OnClick(R.id.startArViewButton)
    public void startArView() {
        boolean isPermissionGranted = sharedPreferences.getBoolean(ConfigApp.PERMISSION_KEY, false);
        if (isPermissionGranted) {
            ((MainActivity) getActivity()).startNewActivity();
        } else {
            ((MainActivity) getActivity()).repeatShowPermission();
            this.showMessage(context.getResources().getString(R.string.permission_not_granted));
        }
    }

    @Override
    public void onDestroy() {
        logger.log(TAG, "onDestroy");
        unbinder.unbind();
        presenter.detachView();
        super.onDestroy();
    }

    @Override
    public void upDateListViewIoTDevice(List<IoTDevice> ioTDevices) {
        ioTDeviceList.addAll(ioTDevices);
        adapter.notifyDataSetChanged();
    }

    private void removeDeviceFromList() {
        logger.log(TAG, "Clear device form list");
        List<IoTDevice> ioTDevicesToRemove = new ArrayList<>();
        for (IoTDevice ioTDevice : ioTDeviceList) {
            if (ioTDevice instanceof BleDeviceWrapper) {
                ioTDevicesToRemove.add(ioTDevice);
                logger.log(TAG, "Ble Device removed");
            } else if (ioTDevice instanceof InternetDeviceWrapper) {
                ioTDevicesToRemove.add(ioTDevice);
                logger.log(TAG, "Internet Device removed");
            }
        }
        ioTDeviceList.removeAll(ioTDevicesToRemove);
    }

    @Override
    public void upDateListViewAllDevice(String address, int status) {
        for (IoTDevice ioTDevice : ioTDeviceList) {
            if (ioTDevice instanceof BleDeviceWrapper) {
                if (((BleDeviceWrapper) ioTDevice).getAddress().equals(address)) {
                    ioTDevice.setStatus(status);
                    break;
                }
            }
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    public void upDateListViewAllInternetDevice(List<InternetDeviceWrapper> internetDeviceWrapperList) {
        for (IoTDevice ioTDevice : ioTDeviceList) {
            if (ioTDevice instanceof InternetDeviceWrapper) {
                for (InternetDeviceWrapper internetDevice : internetDeviceWrapperList) {
                    if (((InternetDeviceWrapper) ioTDevice).getId() == internetDevice.getId()) {
                        ioTDevice.setStatus(Constants.CONNECTED_STATUS);
                        ((InternetDeviceWrapper) ioTDevice).setUpdatetime(internetDevice.getUpdatetime());
                        break;
                    }
                }
            }
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    public void setAllInternetDeviceOffline() {
        for (IoTDevice ioTDevice : ioTDeviceList) {
            if (ioTDevice instanceof InternetDeviceWrapper) {
                ioTDevice.setStatus(Constants.DISCONNECTED_STATUS);
            }
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case DIALOG_FRAGMENT:

                if (resultCode == Activity.RESULT_OK) {
                    IoTDevice device = ioTDeviceList.get(itemPosition);
                    presenter.removeIotDevice(device);
                    ioTDeviceList.remove(itemPosition);
                    adapter.notifyDataSetChanged();
                }

                break;
        }
    }

    @Override
    public void showMessage(String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }
}

