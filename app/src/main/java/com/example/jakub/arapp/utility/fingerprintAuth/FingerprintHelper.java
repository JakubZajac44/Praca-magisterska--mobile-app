package com.example.jakub.arapp.utility.fingerprintAuth;

import android.Manifest;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyPermanentlyInvalidatedException;
import android.security.keystore.KeyProperties;

import com.example.jakub.arapp.utility.Logger;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.List;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.inject.Inject;
import javax.inject.Singleton;

import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;

@Singleton
public class FingerprintHelper {

    public static final String TAG = FingerprintHelper.class.getSimpleName();
    public static final String AUTH_STATUS = "authStatus";
    @Inject
    Logger logger;
    @Inject
    public  Context mContext;
    private FingerprintManager fingerprintManager;
    private FingerprintHandler fingerprintHandler;
    private boolean isAuth;

    private boolean isFingerprintAvailable;

    private List<PropertyChangeListener> observers = new ArrayList<>();

    @Inject
    public FingerprintHelper() {
        this.isAuth = false;
    }

    public boolean checkFingerprintStatus(){
        this.isFingerprintAvailable = this.checkFingerprintAvailable();
        return  this.isFingerprintAvailable;
    }



    public boolean checkFingerprintAvailable() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            fingerprintManager = (FingerprintManager) mContext.getSystemService(Context.FINGERPRINT_SERVICE);
            if (!fingerprintManager.isHardwareDetected()) {
                logger.log(TAG, "Fingerprint sensor not detected");
                return false;
            } else if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.USE_FINGERPRINT) != PackageManager.PERMISSION_GRANTED) {
                logger.log(TAG, "Permission not granted");
                return false;
            } else if (!fingerprintManager.hasEnrolledFingerprints()) {
                logger.log(TAG, "No fingerprints enrolled");
                return false;
            } else {
                logger.log(TAG, "Fingerprint is available");
                return true;
            }
        } else {
            logger.log(TAG, "SDK version is invalid");
            return false;
        }
    }


    public void setAuth(boolean authStatus) {
        notifyObservers(
                AUTH_STATUS,
                this.isAuth,
                this.isAuth = authStatus);
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    public void startFingerPrintAuth() {
        if (this.isFingerprintAvailable) {
            fingerprintHandler = new FingerprintHandler(mContext, this);
            fingerprintHandler.startAuth(fingerprintManager, null);
        }

    }

    public void stopFingerPrintAuth() {
        fingerprintHandler.stopListening();
        fingerprintHandler = null;
    }

    private void notifyObservers( String property, boolean oldValue, boolean newValue) {
        for (PropertyChangeListener observer : observers) {
            observer.propertyChange(new PropertyChangeEvent(this, property, oldValue, newValue));
        }
    }

    public void addObserver(PropertyChangeListener newObserver) {
        observers.add(newObserver);
    }

}

