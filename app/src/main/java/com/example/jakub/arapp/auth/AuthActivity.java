package com.example.jakub.arapp.auth;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import com.example.jakub.arapp.MainActivity;
import com.example.jakub.arapp.MyApplication;
import com.example.jakub.arapp.R;
import com.example.jakub.arapp.application.ConfigApp;
import com.example.jakub.arapp.page.authPage.AuthContract;
import com.example.jakub.arapp.page.authPage.AuthLoginFragment;
import com.example.jakub.arapp.page.authPage.AuthReLoginFragment;
import com.example.jakub.arapp.page.authPage.AuthRegisterFragment;
import com.example.jakub.arapp.utility.Constants;
import com.example.jakub.arapp.utility.Logger;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

public class AuthActivity extends AppCompatActivity {

    public static final String TAG = AuthActivity.class.getSimpleName();
    private final int MULTIPLE_PERMISSIONS = 101;

    @Inject
    Logger logger;
    @Inject
    ConfigApp configApp;
    @Inject
    SharedPreferences.Editor sharedPreferencesEditor;
    @Inject
    SharedPreferences sharedPreferences;
    boolean doubleBackToExitPressedOnce = false;
    private List<String> listPermissionsNeeded;
    private AuthContract.RegisterView registrationFragment;
    private AuthContract.ReLoginView reLoginFragment;
    private AuthContract.LoginView loginFragment;
    private boolean allPermissionGranted;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);
        ((MyApplication) getApplication()).getAppComponent().inject(this);
        listPermissionsNeeded = new ArrayList<>();
        registrationFragment = new AuthRegisterFragment();
        reLoginFragment = new AuthReLoginFragment();
        loginFragment = new AuthLoginFragment();
        this.chooseBeginFragment();
        showPermissionRationale(configApp.getPermissions());
    }

    private void chooseBeginFragment() {
        boolean isRegistered = sharedPreferences.getBoolean(Constants.ACCOUNT_REGISTRATION_STATUS_KEY, false);
        Fragment fragment;
        String fragmentTag;
        if(isRegistered){
            fragment = (Fragment) loginFragment;
            fragmentTag = AuthLoginFragment.TAG;
        }else {
            fragment = (Fragment) reLoginFragment;
            fragmentTag = AuthReLoginFragment.TAG;
        }
        this.replaceFragment(fragment,fragmentTag);
    }

    public void startMainActivity() {
        if (allPermissionGranted) {
            Intent myIntent = new Intent(AuthActivity.this, MainActivity.class);
            startActivity(myIntent);
            finish();
        }
    }

    public void showReLoginFragment() {
        this.replaceFragment((Fragment) reLoginFragment, AuthReLoginFragment.TAG);
    }

    public void showRegistrationFragment() {
        this.replaceFragment((Fragment) registrationFragment, AuthRegisterFragment.TAG);
    }

    public <T extends Fragment> void replaceFragment(T fragment, String TAG) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.contentAppAuth, fragment, TAG);
        ft.addToBackStack(null);
        ft.commit();
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
        allPermissionGranted = false;
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
    protected void onDestroy() {
        super.onDestroy();
        finish();
    }


}
