package com.example.jakub.arapp.dialogFragment.fingerprintAuth;


import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.example.jakub.arapp.R;

import androidx.fragment.app.DialogFragment;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class FingerPrintPermissionDialog extends DialogFragment {

    public static final String TAG = FingerPrintPermissionDialog.class.getSimpleName();
    public static final String FINGERPRINT_AUTH_STATUS = "FINGERPRINT_AUTH_STATUS";
    public static final int DIALOG_FINGERPRINT_AUTH_STATUS_FRAGMENT = 2;

    private Unbinder unbinder;
    private boolean isDismissed;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fingerprint_registration_dialog_fragment, container, false);
        unbinder = ButterKnife.bind(this, v);
        isDismissed = false;
        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        this.setDialogProperties();
    }

    private void setDialogProperties() {
        ViewGroup.LayoutParams params = getDialog().getWindow().getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        getDialog().getWindow().setAttributes((WindowManager.LayoutParams) params);
    }

    @OnClick(R.id.fingerprintAuthYesButton)
    public void confirmationFingerprintAuthButton() {
        this.closeDialog();
        getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, getActivity().getIntent().putExtra(FINGERPRINT_AUTH_STATUS, true));
    }

    @OnClick(R.id.fingerprintAuthNoButton)
    public void denyFingerprintAuthButton() {
        this.closeDialog();
        getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_CANCELED, getActivity().getIntent().putExtra(FINGERPRINT_AUTH_STATUS, false));
    }

    @Override
    public void onPause() {
        this.closeDialog();
        super.onPause();
    }

    private void closeDialog() {
        if (!isDismissed) {
            unbinder.unbind();
            this.dismiss();
            this.isDismissed = true;
        }
    }

}
