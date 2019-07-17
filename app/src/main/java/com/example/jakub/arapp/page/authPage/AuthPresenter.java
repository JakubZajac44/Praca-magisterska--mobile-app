package com.example.jakub.arapp.page.authPage;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.jakub.arapp.R;
import com.example.jakub.arapp.application.ConfigApp;
import com.example.jakub.arapp.internetManager.api.ApiConnection;
import com.example.jakub.arapp.model.User;
import com.example.jakub.arapp.utility.Constants;
import com.example.jakub.arapp.utility.Logger;
import com.example.jakub.arapp.utility.Utils;
import com.example.jakub.arapp.utility.keyStore.Decrypt;
import com.example.jakub.arapp.utility.keyStore.Encrypt;
import com.jakewharton.retrofit2.adapter.rxjava2.HttpException;

import java.util.Arrays;

import javax.inject.Inject;

import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;

public class AuthPresenter implements AuthContract.Presenter {
    public static final String TAG = AuthPresenter.class.getSimpleName();
    @Inject
    Retrofit retrofit;
    @Inject
    Logger logger;
    @Inject
    Context context;
    @Inject
    SharedPreferences.Editor editor;
    @Inject
    SharedPreferences preferences;
    private AuthContract.LoginView loginView;
    private AuthContract.RegisterView registerView;
    private AuthContract.ReLoginView reLoginView;

    @Inject
    public AuthPresenter() {
    }

    @Override
    public void attachView(AuthContract.View view) {
        if (view instanceof AuthContract.RegisterView) {
            registerView = (AuthContract.RegisterView) view;
        } else if (view instanceof AuthContract.LoginView) {
            loginView = (AuthContract.LoginView) view;
        } else if (view instanceof AuthContract.ReLoginView) {
            reLoginView = (AuthContract.ReLoginView) view;
        }
    }

    @Override
    public void detachView() {
        this.loginView = null;
    }

    //*********************** registration ************************************//

    @Override
    public void registerUser(String name, String password) {
        boolean isUserDataCorrect = this.validateUserData(name, password);
        if (isUserDataCorrect) {
            User user = new User(name, password);
            this.registerUserAPI(user);
        } else {
            registerView.showToast(context.getResources().getString(R.string.register_error_not_valid_text));
        }
    }

    private boolean validateUserData(String name, String password) {
        if (name.length() >= 4 && password.length() >= 6) return true;
        else return false;
    }

    private void registerUserAPI(User user) {
        ApiConnection apiConnection = retrofit.create(ApiConnection.class);
        apiConnection.registerNewUser(user).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new SingleObserver<String>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onSuccess(String token) {
                        saveToken(token);
                        saveUserCredentials(user);
                        registerView.userRegistered();
                    }

                    @Override
                    public void onError(Throwable e) {
                        HttpException error = (HttpException) e;
                        logger.log(TAG, "Error " + e.getMessage());
                        if (error.code() == Constants.USER_ALREADY_EXIST)
                            registerView.showToast(context.getResources().getString(R.string.register_error_user_exist));
                        else
                            registerView.showToast(context.getResources().getString(R.string.api_error));
                    }
                });
    }

    @Override
    public void saveUserCredentials(User user) {

        Encrypt encrypt = new Encrypt();
        try {
            byte[] IV;
            byte[] encryptedName = encrypt.encryptText(Constants.ACCOUNT_NAME_KEY, user.getName());
            IV = encrypt.getIv();
            editor.putString(Constants.ACCOUNT_NAME_IV_KEY, Arrays.toString(IV));
            editor.putString(Constants.ACCOUNT_NAME_KEY, Arrays.toString(encryptedName));
            byte[] encryptedPassword = encrypt.encryptText(Constants.ACCOUNT_PASSWORD_KEY, user.getPassword());
            IV = encrypt.getIv();
            editor.putString(Constants.ACCOUNT_PASSWORD_IV_KEY, Arrays.toString(IV));
            editor.putString(Constants.ACCOUNT_PASSWORD_KEY, Arrays.toString(encryptedPassword));
            editor.commit();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void savePin(String pin) {
        editor.putBoolean(Constants.ACCOUNT_REGISTRATION_STATUS_KEY, true);
        try {
            Encrypt encrypt = new Encrypt();
            byte[] IV;
            byte[] encryptedName = encrypt.encryptText(Constants.ACCOUNT_PIN_KEY, pin);
            IV = encrypt.getIv();
            editor.putString(Constants.ACCOUNT_PIN_IV_KEY, Arrays.toString(IV));
            editor.putString(Constants.ACCOUNT_PIN_KEY, Arrays.toString(encryptedName));
            editor.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    //*************************** login **************************************//

    @Override
    public void loginUserPin(String pin) {

        boolean isPinValid = this.validatePin(pin);

        if (isPinValid) {
            String savedPin = this.getCredentialsFromPref(Constants.ACCOUNT_PIN_KEY, Constants.ACCOUNT_PIN_KEY, Constants.ACCOUNT_PIN_IV_KEY);
            if (pin.equals(savedPin)) {
                this.loginUser();
            } else
                loginView.showToast(context.getResources().getString(R.string.login_error_invalid_pin));
        } else {
            loginView.showToast(context.getResources().getString(R.string.login_error_invalid_pin));
        }

    }

    @Override
    public void loginUserFingerPrint(){
        this.loginUser();
    }

    private void loginUser(){
        String name = this.getCredentialsFromPref(Constants.ACCOUNT_NAME_KEY, Constants.ACCOUNT_NAME_KEY, Constants.ACCOUNT_NAME_IV_KEY);
        String password = this.getCredentialsFromPref(Constants.ACCOUNT_PASSWORD_KEY, Constants.ACCOUNT_PASSWORD_KEY, Constants.ACCOUNT_PASSWORD_IV_KEY);
        User user = new User(name, password);
        this.loginUserAPI(user);
    }

    public String getCredentialsFromPref(String alias, String key, String IVkey) {
        String IV = preferences.getString(IVkey, "");
        byte[] IVbyte = Utils.getByteArryFromString(IV);
        String data = preferences.getString(key, "");
        byte[] dataByte = Utils.getByteArryFromString(data);

        return this.decryptByAlias(alias, dataByte, IVbyte);
    }

    private String decryptByAlias(String alias, byte[] dataToDecrypt, byte[] IV) {
        try {
            Decrypt decrypt = new Decrypt();
            return decrypt.decryptData(alias, dataToDecrypt, IV);
        } catch (Exception e) {
            e.printStackTrace();
            return "error";
        }
    }

    private boolean validatePin(String pin) {
        if (pin.length() >= 4) {
            try {
                Integer.parseInt(pin);
            } catch (NumberFormatException | NullPointerException nfe) {
                loginView.showToast(context.getResources().getString(R.string.login_error_invalid_pin));
                return false;
            }
            return true;
        } else {
            loginView.showToast(context.getResources().getString(R.string.login_error_wrong_pin));
            return false;
        }
    }

    private void loginUserAPI(User user) {
        ApiConnection apiConnection = retrofit.create(ApiConnection.class);
        apiConnection.loginUser(user).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new SingleObserver<String>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onSuccess(String token) {
                        saveToken(token);
                        loginView.userLogged();
                    }

                    @Override
                    public void onError(Throwable e) {
                        try {
                            HttpException error = (HttpException) e;
                            logger.log(TAG, "Error " + e.getMessage());
                            if (error.code() == Constants.USER_NOT_EXIST)
                                loginView.showToast(context.getResources().getString(R.string.login_error_user_not_exist));
                            else
                                loginView.showToast(context.getResources().getString(R.string.api_error));
                        } catch (Exception exception) {
                            loginView.showToast(context.getResources().getString(R.string.api_error));
                        }

                    }
                });
    }

    private void saveToken(String token) {
        ConfigApp.TOKEN = token;
    }

    //********************* relogin ********************************************************//

    @Override
    public void reLoginUser(String name, String password) {

        boolean isUserDataCorrect = this.validateUserData(name, password);
        if (isUserDataCorrect) {
            User user = new User(name, password);
            this.reLoginUserAPI(user);
        } else {
            reLoginView.showToast(context.getResources().getString(R.string.register_error_not_valid_text));
        }
    }

    private void reLoginUserAPI(User user) {
        ApiConnection apiConnection = retrofit.create(ApiConnection.class);
        apiConnection.loginUser(user).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new SingleObserver<String>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onSuccess(String token) {
                        saveToken(token);
                        reLoginView.userReLogged(user);
                    }

                    @Override
                    public void onError(Throwable e) {
                        try {
                            HttpException error = (HttpException) e;
                            logger.log(TAG, "Error " + e.getMessage());
                            if (error.code() == Constants.USER_NOT_EXIST)
                                reLoginView.showToast(context.getResources().getString(R.string.login_error_user_not_exist));
                            else
                                reLoginView.showToast(context.getResources().getString(R.string.api_error));
                        } catch (Exception exception) {
                            reLoginView.showToast(context.getResources().getString(R.string.api_error));
                        }

                    }
                });
    }
}
