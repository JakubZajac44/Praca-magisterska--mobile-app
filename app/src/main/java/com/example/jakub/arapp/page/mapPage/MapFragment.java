package com.example.jakub.arapp.page.mapPage;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jakub.arapp.MyApplication;
import com.example.jakub.arapp.R;
import com.example.jakub.arapp.application.ConfigApp;
import com.example.jakub.arapp.model.device.InternetDeviceWrapper;
import com.example.jakub.arapp.utility.Constants;
import com.example.jakub.arapp.utility.Logger;
import com.example.jakub.arapp.utility.MathOperation;
import com.example.jakub.arapp.utility.shape.MapCircle;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class MapFragment extends Fragment implements MapContract.View, OnMapReadyCallback, GoogleMap.OnMapLongClickListener {

    public static final String TAG = MapFragment.class.getSimpleName();

    @Inject
    MapContract.Presenter presenter;

    @Inject
    Logger logger;

    @Inject
    Context context;

    @Inject
    SharedPreferences.Editor editor;

    @Inject
    SharedPreferences sharedPreferences;

    @BindView(R.id.seekBarRadius)
    SeekBar seekBarRadius;

    @BindView(R.id.seekBarStatusTextView)
    TextView seekBarStatusTextView;

    private Unbinder unbinder;
    private GoogleMap mMap;
    private SupportMapFragment supportMapFragment;

    private Circle circle;
    private Location currentUserLocation;
    private LatLng currentCircleLocation;
    private int currentRadius;
    private Marker currentLocationMarker;
    private List<Marker> internetDeviceMarkerList;
    private boolean trackingUserLocation;
    private boolean firstUpdate;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((MyApplication) getActivity().getApplication()).getAppComponent().inject(this);
        this.trackingUserLocation = true;
        this.firstUpdate = true;
        internetDeviceMarkerList = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.map_panel, container, false);
        unbinder = ButterKnife.bind(this, view);
        presenter.attachView(this);
        supportMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (supportMapFragment == null) {
            FragmentManager fm = getFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            supportMapFragment = SupportMapFragment.newInstance();
            ft.replace(R.id.map, supportMapFragment).commit();
        }
        supportMapFragment.getMapAsync(this);
        this.setSeekBarStatusText(false);
        this.setSeekBarListener();
        this.setSeekBarEnable(false);
        return view;
    }

    private void setSeekBarListener() {
        seekBarRadius.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progressValue, boolean b) {
                currentRadius = progressValue * 1000;
                renderCircle();
                setSeekBarStatusText(true);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnMapLongClickListener(this);
        this.getCircleFromPref();
    }

    private void getCircleFromPref() {
        Gson gson = new Gson();
        String json = sharedPreferences.getString(ConfigApp.CIRCLE_MAP_KEY, "");
        if (json.equals("")) {
            logger.log(TAG, "no map circle to loaded");
            this.currentRadius = Constants.INIT_RATIO * 1000;
            this.setSeekBarEnable(false);
            this.setSeekBarStatusText(false);
        } else {
            MapCircle savedMapCircle = gson.fromJson(json, MapCircle.class);
            this.currentRadius = savedMapCircle.getRatio();
            this.currentCircleLocation = savedMapCircle.getLocation();
            this.seekBarRadius.setProgress(this.currentRadius / 1000);
            this.setSeekBarStatusText(true);
            this.setSeekBarEnable(true);
            this.renderCircle();
            logger.log(TAG, "map circle loaded");
        }
    }

    @Override
    public void onMapLongClick(LatLng latLng) {
        this.currentCircleLocation = latLng;
        this.setSeekBarStatusText(true);
        this.setSeekBarEnable(true);
        this.renderCircle();
    }

    private void renderCircle() {
        if (circle != null) circle.remove();
        this.drawCircle();
    }

    private void drawCircle() {

        CircleOptions circleOptions = new CircleOptions();
        circleOptions.center(currentCircleLocation);

        circleOptions.radius(this.currentRadius);

        circleOptions.strokeColor(Color.BLACK);
        circleOptions.fillColor(Color.BLUE);

        circleOptions.fillColor(0x300000FF);

        circleOptions.strokeWidth(2);

        circle = mMap.addCircle(circleOptions);
    }

    @Override
    public void changeUserLocation(Location location) {
        this.currentUserLocation = location;
        this.renderMap();
    }

    @Override
    public void showMessage(String message) {
        Toast.makeText(context,message, Toast.LENGTH_SHORT).show();
    }

    private void renderMap() {
        if (trackingUserLocation) this.setUpMapCamera();
        if (currentLocationMarker != null) {
            currentLocationMarker.remove();
        }
        this.addUserMarker();
    }

    @OnClick(R.id.saveInternetDeviceButton)
    public void saveInternetDevice() {
        presenter.saveDeviceInCircle(this.currentRadius, MathOperation.getLocation(currentCircleLocation));
    }

    @OnClick(R.id.trackUserLocationButton)
    public void trackUserLocationButton() {
        if (trackingUserLocation) trackingUserLocation = false;
        else trackingUserLocation = true;
    }

    @OnClick(R.id.saveRatioButton)
    public void saveCircleDetailsButton() {
        Gson gson = new Gson();
        MapCircle mapCircle = new MapCircle(circle.getCenter(), (int) circle.getRadius());
        String json = gson.toJson(mapCircle);
        editor.putString(ConfigApp.CIRCLE_MAP_KEY, json);
        editor.commit();
    }

    private void setSeekBarEnable(boolean status) {
        this.seekBarRadius.setEnabled(status);
    }

    private void setUpMapCamera() {
        LatLng latLng = new LatLng(this.currentUserLocation.getLatitude(),
                this.currentUserLocation.getLongitude());
        if (firstUpdate) {
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 11.0f));
            firstUpdate = false;
        } else mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
    }

    private void addUserMarker() {
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(new LatLng(this.currentUserLocation.getLatitude(), this.currentUserLocation.getLongitude()));
        currentLocationMarker = mMap.addMarker(markerOptions);
    }

    private void setSeekBarStatusText(boolean seekBarStatus) {
        String text;
        if (seekBarStatus) {
            text = context.getResources().getString(R.string.seekBarStatusActive);
            text += String.valueOf(currentRadius / 1000);
            text += " km.";
        } else text = context.getResources().getString(R.string.seekBarStatusNonactive);
        seekBarStatusTextView.setText(text);
    }

    @Override
    public void addInternetDeviceMarker(List<InternetDeviceWrapper> internetDeviceWrappers){
        this.removeAllInternetDeviceMarker();
        for(InternetDeviceWrapper device:internetDeviceWrappers){
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(new LatLng(device.getLat(), device.getLon()))
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE))
                    .title(device.getName()+ String.valueOf(device.getId()))
                    .snippet("Odczyt: "+device.getSample());
            Marker marker = mMap.addMarker(markerOptions);
            internetDeviceMarkerList.add(marker);
        }
    }

    @Override
    public void removeAllInternetDeviceMarker() {
        for(Marker marker : internetDeviceMarkerList) marker.remove();
        this.internetDeviceMarkerList.clear();
    }

    @Override
    public void onDestroy() {
        unbinder.unbind();
        presenter.detachView();
        super.onDestroy();
    }
}
