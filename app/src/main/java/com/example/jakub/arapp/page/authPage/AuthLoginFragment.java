package com.example.jakub.arapp.page.authPage;

import android.content.Context;
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
import com.example.jakub.arapp.dialogFragment.fingerprintAuth.FingerPrintLoginDialog;
import com.example.jakub.arapp.utility.fingerprintAuth.FingerprintHelper;
import com.example.jakub.arapp.utility.Constants;
import com.example.jakub.arapp.utility.Logger;

import javax.inject.Inject;

import androidx.fragment.app.DialogFragment;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

public class AuthLoginFragment extends Fragment implements AuthContract.LoginView{

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
    SharedPreferences sharedPreferences;

    @Inject
    FingerprintHelper fingerprintHelper;

    @BindView(R.id.pinText)
    EditText userPinText;

    private Unbinder unbinder;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((MyApplication) getActivity().getApplication()).getAppComponent().inject(this);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.auth_login_fragment, container, false);
        presenter.attachView(this);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        this.showFingerPrintDialog();
    }

    @OnClick(R.id.reloginButton2)
    public void reLoginUser() {
        ((AuthActivity)getActivity()).showReLoginFragment();
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

    @OnClick(R.id.loginButton)
    public void loginUser() {
        String pin = userPinText.getText().toString();
        presenter.loginUserPin(pin);
    }

    @OnClick(R.id.fingerprintButton)
    public void showFingerPrintDialog() {

        boolean isFingerprintAvailable = fingerprintHelper.checkFingerprintStatus();
        boolean fingerprintAvailable = sharedPreferences.getBoolean(Constants.FINGERPRINT_USER_CONFIRMATION,false);
        if (fingerprintAvailable && isFingerprintAvailable) {
            FingerPrintLoginDialog dialogFragment = new FingerPrintLoginDialog();
            this.showDialogFragment(dialogFragment,FingerPrintLoginDialog.TAG,FingerPrintLoginDialog.FINGERPRINT_LOGIN_DIALOG_CODE);
            dialogFragment.setPresenter(presenter);

        } else {
            this.showToast("Opcja logowaniem przy pomocy palca niedostepna");
        }
    }

    @Override
    public void FingerprintDialogFragmentClosed(boolean authStatus) {
        if(authStatus)presenter.loginUserFingerPrint();
    }

    @Override
    public void userLogged() {
        this.setRunningStatus(false);
        this.startMainActivity();
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

    private <T extends DialogFragment> void showDialogFragment(T dialogFragment, String TAG, int code) {
        FragmentManager manager = getFragmentManager();
        dialogFragment.setTargetFragment(this, code);
        dialogFragment.show(manager, TAG);
    }

}
