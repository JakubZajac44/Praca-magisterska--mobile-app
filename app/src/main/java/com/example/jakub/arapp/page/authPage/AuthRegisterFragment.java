package com.example.jakub.arapp.page.authPage;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.example.jakub.arapp.MyApplication;
import com.example.jakub.arapp.R;
import com.example.jakub.arapp.auth.AuthActivity;
import com.example.jakub.arapp.dialogFragment.PinDialog;
import com.example.jakub.arapp.dialogFragment.fingerprintAuth.FingerPrintPermissionDialog;
import com.example.jakub.arapp.utility.Constants;
import com.example.jakub.arapp.utility.Logger;
import com.example.jakub.arapp.utility.fingerprintAuth.FingerprintHelper;

import javax.inject.Inject;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class AuthRegisterFragment extends Fragment implements AuthContract.RegisterView {

    public static final String TAG = AuthRegisterFragment.class.getSimpleName();

    @Inject
    public AuthContract.Presenter presenter;
    @Inject
    Context context;
    @Inject
    Logger logger;
    @Inject
    SharedPreferences.Editor editor;
    @Inject
    FingerprintHelper fingerprintHelper;
    @Inject
    SharedPreferences sharedPreferences;

    @BindView(R.id.userNameText)
    EditText userNameText;
    @BindView(R.id.userPasswordText)
    EditText userPasswordText;
    private Unbinder unbinder;
    private boolean isSignIn;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((MyApplication) getActivity().getApplication()).getAppComponent().inject(this);
        this.isSignIn = sharedPreferences.getBoolean(Constants.ACCOUNT_REGISTRATION_STATUS_KEY, false);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.auth_registration_fragment, container, false);
        presenter.attachView(this);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @OnClick(R.id.reloginButton)
    public void reLoginUser() {
        ((AuthActivity) getActivity()).showReLoginFragment();
    }

    @OnClick(R.id.notLoginButton)
    public void runWithoutLogin() {
        setRunningStatus(true);
        startMainActivity();
    }


    private void setRunningStatus(boolean status) {
        editor.putBoolean(Constants.RUNNING_WITHOUT_LOGIN, status);
        editor.commit();
    }

    @OnClick(R.id.registerButton)
    public void registerUser() {
        String name = userNameText.getText().toString();
        String password = userPasswordText.getText().toString();
        presenter.registerUser(name, password);
    }

    @Override
    public void userRegistered() {
        this.showPinDialogFragment();
    }

    private void showPinDialogFragment() {
        PinDialog pinDialog = new PinDialog();
        this.showDialogFragment(pinDialog,PinDialog.TAG,PinDialog.DIALOG_PIN_FRAGMENT);
    }

    private void startMainActivity() {
        ((AuthActivity) getActivity()).startMainActivity();
    }

    @Override
    public void onDestroy() {
        presenter.detachView();
        unbinder.unbind();
        super.onDestroy();
    }

    @Override
    public void showToast(String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case PinDialog.DIALOG_PIN_FRAGMENT:
                String pin = data.getStringExtra(PinDialog.PIN_KEY);
                presenter.savePin(pin);
                editor.putBoolean(Constants.ACCOUNT_REGISTRATION_STATUS_KEY, true);
                this.setRunningStatus(false);
                editor.commit();
                boolean isFingerprintAvailable = fingerprintHelper.checkFingerprintStatus();
                if (isFingerprintAvailable) this.showFingerPrintAuthPermissionDialog();
                else startMainActivity();
                break;

            case FingerPrintPermissionDialog.DIALOG_FINGERPRINT_AUTH_STATUS_FRAGMENT:
                boolean status = false;
                if (resultCode == Activity.RESULT_OK) {
                    status = true;
                }
                editor.putBoolean(Constants.FINGERPRINT_USER_CONFIRMATION, status);
                editor.commit();
                startMainActivity();
                break;
        }
    }

    public void showFingerPrintAuthPermissionDialog() {
        FingerPrintPermissionDialog fingerPrintPermissionDialog = new FingerPrintPermissionDialog();
        this.showDialogFragment(fingerPrintPermissionDialog,FingerPrintPermissionDialog.TAG,FingerPrintPermissionDialog.DIALOG_FINGERPRINT_AUTH_STATUS_FRAGMENT);
    }

    private <T extends DialogFragment> void showDialogFragment(T dialogFragment, String TAG, int code){
        FragmentManager manager = getFragmentManager();
        dialogFragment.setTargetFragment(this, code);
        dialogFragment.show(manager,TAG);
    }
}
