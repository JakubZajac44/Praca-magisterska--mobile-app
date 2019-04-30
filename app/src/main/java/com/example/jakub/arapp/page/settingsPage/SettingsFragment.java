package com.example.jakub.arapp.page.settingsPage;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import com.example.jakub.arapp.MainActivity;
import com.example.jakub.arapp.MyApplication;
import com.example.jakub.arapp.R;
import com.example.jakub.arapp.utility.Constants;
import com.example.jakub.arapp.utility.Logger;

import javax.inject.Inject;

import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SwitchPreference;

public class SettingsFragment extends PreferenceFragmentCompat implements SettingsContract.View,
        SharedPreferences.OnSharedPreferenceChangeListener {

    public static final String TAG = SettingsFragment.class.getSimpleName();

    @Inject
    public SettingsContract.Presenter presenter;

    @Inject
    public Logger logger;

    @Inject
    Context context;

    @Inject
    SharedPreferences.Editor editor;

    @Inject
    SharedPreferences pref;

    private SwitchPreference accountPref;
    private SwitchPreference fingerprintPref;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.v(TAG, "OnCreate");
        ((MyApplication) getActivity().getApplication()).getAppComponent().inject(this);
        getPreferenceManager().setSharedPreferencesName("settingsPreference");
        getPreferenceManager().setSharedPreferencesMode(Context.MODE_PRIVATE);

        if (pref.getBoolean(Constants.ACCOUNT_REGISTRATION_STATUS_KEY, false)) {
            if (pref.getBoolean(Constants.RUNNING_WITHOUT_LOGIN, false)) {
                accountPref.setChecked(false);
                fingerprintPref.setEnabled(false);
                accountPref.setTitle("Zaloguj");
                accountPref.setOnPreferenceClickListener(preference -> {
                            signIn();
                            return true;
                        }
                );
            } else {
                accountPref.setChecked(true);
                accountPref.setTitle("Wyloguj");
            }
        } else {
            accountPref.setChecked(false);
            accountPref.setTitle("Zaloguj");
        }
        if (pref.getBoolean(Constants.FINGERPRINT_USER_CONFIRMATION, false)) {
            fingerprintPref.setChecked(true);
            fingerprintPref.setTitle("Wyłącz");
        } else {
            fingerprintPref.setChecked(false);
            fingerprintPref.setTitle("Włącz");
        }

    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.preference, rootKey);
        accountPref = (SwitchPreference) findPreference(Constants.ACCOUNT_REGISTRATION_STATUS_KEY);
        fingerprintPref = (SwitchPreference) findPreference(Constants.FINGERPRINT_USER_CONFIRMATION);
    }

    @Override
    public void onResume() {
        super.onResume();
        getPreferenceScreen().getSharedPreferences()
                .registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        getPreferenceScreen().getSharedPreferences()
                .unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onDestroy() {
        presenter.detachView();
        super.onDestroy();
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        logger.log(TAG, "change");
        if (key.equals(Constants.ACCOUNT_REGISTRATION_STATUS_KEY)) {
            boolean accountStatus = pref.getBoolean(key, false);
            if (accountStatus) {
                this.signOut();
            } else this.signIn();
        } else if (key.equals(Constants.FINGERPRINT_USER_CONFIRMATION)) {
            boolean fingerprintStatus = pref.getBoolean(key, false);
            if (fingerprintStatus)editor.putBoolean(Constants.FINGERPRINT_USER_CONFIRMATION, false);
            else editor.putBoolean(Constants.FINGERPRINT_USER_CONFIRMATION, true);
            editor.commit();
        }
    }

    private void signIn() {
        ((MainActivity) getActivity()).startAuthActivity();
    }

    private void signOut() {
        editor.putBoolean(Constants.FINGERPRINT_USER_CONFIRMATION, false);
        editor.putBoolean(Constants.ACCOUNT_REGISTRATION_STATUS_KEY, false);
        editor.putString(Constants.ACCOUNT_NAME_KEY, "");
        editor.putString(Constants.ACCOUNT_PASSWORD_KEY, "");
        editor.putString(Constants.ACCOUNT_NAME_IV_KEY, "");
        editor.putString(Constants.ACCOUNT_PASSWORD_IV_KEY, "");
        editor.commit();
        ((MainActivity) getActivity()).startAuthActivity();
    }

}
