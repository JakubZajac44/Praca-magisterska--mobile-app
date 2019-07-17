package com.example.jakub.arapp.utility.fingerprintAuth;

import android.content.Context;
import android.hardware.fingerprint.FingerprintManager;
import android.hardware.fingerprint.FingerprintManager.AuthenticationCallback;
import android.os.Build;
import android.os.CancellationSignal;
import android.widget.Toast;

import com.example.jakub.arapp.R;


import androidx.annotation.RequiresApi;

@RequiresApi(api = Build.VERSION_CODES.M)
public class FingerprintHandler extends AuthenticationCallback {

    private Context mContext;
    private FingerprintHelper fingerprintHelper;
    private CancellationSignal cancellationSignal;

    public FingerprintHandler(Context mContext, FingerprintHelper fingerprintHelper) {
        this.mContext = mContext;
        this.fingerprintHelper = fingerprintHelper;
    }

    public void startAuth(FingerprintManager fingerprintManager, FingerprintManager.CryptoObject cryptoObject) {
        cancellationSignal = new CancellationSignal();
        fingerprintManager.authenticate(cryptoObject, cancellationSignal, 0, this, null);
    }

    @Override
    public void onAuthenticationError(int errorCode, CharSequence errString) {
        super.onAuthenticationError(errorCode, errString);
    }

    @Override
    public void onAuthenticationHelp(int helpCode, CharSequence helpString) {
        super.onAuthenticationHelp(helpCode, helpString);
        Toast.makeText(mContext,helpString.toString(),Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onAuthenticationSucceeded(FingerprintManager.AuthenticationResult result) {
        super.onAuthenticationSucceeded(result);
        fingerprintHelper.setAuth(true);
        cancellationSignal.cancel();
    }

    @Override
    public void onAuthenticationFailed() {
        super.onAuthenticationFailed();
        fingerprintHelper.setAuth(false);
        Toast.makeText(mContext,mContext.getString(R.string.fingerprintNotRecognized),Toast.LENGTH_SHORT).show();
    }

    public void stopListening() {
        cancellationSignal.cancel();
        cancellationSignal = null;
    }
}
