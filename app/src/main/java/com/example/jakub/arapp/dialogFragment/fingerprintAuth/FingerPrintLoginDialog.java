package com.example.jakub.arapp.dialogFragment.fingerprintAuth;

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;

import com.example.jakub.arapp.MyApplication;
import com.example.jakub.arapp.R;
import com.example.jakub.arapp.page.authPage.AuthContract;
import com.example.jakub.arapp.utility.fingerprintAuth.FingerprintHelper;
import com.example.jakub.arapp.utility.fingerprintAuth.FingerprintObserver;

import javax.inject.Inject;

import androidx.fragment.app.DialogFragment;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class FingerPrintLoginDialog extends DialogFragment {

    public static final String TAG = FingerPrintLoginDialog.class.getSimpleName();
    public static final int FINGERPRINT_LOGIN_DIALOG_CODE = 5;
    @Inject
    public FingerprintHelper fingerprintHelper;
    @BindView(R.id.fingerprintImageView)
    ImageView fingerprintImageView;
    private Unbinder unbinder;
    private AuthContract.Presenter presenter;
    private boolean isDismissed;
    private boolean isAuth;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((MyApplication) getActivity().getApplication()).getAppComponent().inject(this);
        setStyle(DialogFragment.STYLE_NO_FRAME,0);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fingerprint_login_dialog_fragment, container, false);
        unbinder = ButterKnife.bind(this, v);
        isDismissed = false;
        return v;
    }

    @Override
    public void onResume() {
        super.onResume();

        this.setDialogProperties();

        fingerprintHelper.checkFingerprintStatus();
        fingerprintHelper.startFingerPrintAuth();

        FingerprintObserver observer = new FingerprintObserver(fingerprintHelper, this);
    }

    private void setDialogProperties() {
        WindowManager.LayoutParams params = getDialog().getWindow().getAttributes();
        params.dimAmount = 0.8f;
        getDialog().getWindow().setAttributes(params);
        getDialog().getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
    }

    public void setPresenter(AuthContract.Presenter presenter) {
        this.presenter = presenter;
    }

    public void changeAuthStatus(boolean status) {
        isAuth = status;
        if (isAuth) {
            fingerprintImageView.setImageResource(R.drawable.correct_finger_print);
            final Handler handler = new Handler();
            handler.postDelayed(() -> close(), 1000);
        } else {
            fingerprintImageView.setImageResource(R.drawable.finger_pring_red);
        }
    }

    @OnClick(R.id.closeDialogFragmentButton)
    public void closeDialogFragment() {
        this.close();
    }

    @Override
    public void onPause() {
        this.close();
        super.onPause();
    }

    private void close() {
        if (!isDismissed) {
            fingerprintHelper.stopFingerPrintAuth();
            fingerprintHelper = null;
            this.dismiss();
            this.isDismissed = true;
        }
    }

    @Override
    public void onDismiss(final DialogInterface dialog) {
        super.onDismiss(dialog);
        AuthContract.LoginView contractView = (AuthContract.LoginView) getTargetFragment();
        contractView.FingerprintDialogFragmentClosed(isAuth);
    }

}

