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
import com.example.jakub.arapp.model.device.IoTDevice;
import com.example.jakub.arapp.page.mainArPage.bleDeviceStatusList.ListViewBleDeviceStatusAdapter;
import com.example.jakub.arapp.utility.Logger;

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


    private int itemPosition;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((MyApplication) getActivity().getApplication()).getAppComponent().inject(this);
        logger.log(TAG, "onCreate");


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.ar_settings_fragment, container, false);
        unbinder = ButterKnife.bind(this, view);

        this.setListViewListener();
        presenter.attachView(this);
        logger.log(TAG, "onCreateView");
        return view;
    }

    @Override
    public void setupModel(List<IoTDevice> ioTDeviceList) {
        adapter = new ListViewBleDeviceStatusAdapter(context, ioTDeviceList);
        bleDeviceStatusListView.setAdapter(adapter);
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
        presenter.getSavedDevice();
        super.onResume();
    }

    @OnClick(R.id.startArViewButton)
    public void startArView() {
        boolean isPermissionGranted = sharedPreferences.getBoolean(ConfigApp.PERMISSION_KEY, false);
        if (isPermissionGranted) {
            ((MainActivity) getActivity()).startArActivity();
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
    public void notifyDataChanged() {
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case DIALOG_FRAGMENT:
                if (resultCode == Activity.RESULT_OK) {
                    presenter.removeIotDevice(itemPosition);
                }
                break;
        }
    }

    @Override
    public void showMessage(String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }
}

