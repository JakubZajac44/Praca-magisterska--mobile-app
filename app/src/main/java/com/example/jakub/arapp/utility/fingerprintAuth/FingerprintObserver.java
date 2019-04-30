package com.example.jakub.arapp.utility.fingerprintAuth;

import com.example.jakub.arapp.dialogFragment.fingerprintAuth.FingerPrintLoginDialog;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class FingerprintObserver implements PropertyChangeListener {

    public static final String TAG = FingerprintObserver.class.getSimpleName();
    private FingerPrintLoginDialog fingerPrintLoginDialog;

    public FingerprintObserver(FingerprintHelper fingerprintHelper, FingerPrintLoginDialog fingerPrintLoginDialog) {
        this.fingerPrintLoginDialog = fingerPrintLoginDialog;
        fingerprintHelper.addObserver(this);
    }

    @Override
    public void propertyChange(PropertyChangeEvent event) {
        fingerPrintLoginDialog.changeAuthStatus((boolean) event.getNewValue());
    }
}
