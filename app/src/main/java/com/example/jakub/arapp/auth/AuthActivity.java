package com.example.jakub.arapp.auth;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.jakub.arapp.MainActivity;
import com.example.jakub.arapp.MyApplication;
import com.example.jakub.arapp.R;
import com.example.jakub.arapp.utility.Logger;

import javax.inject.Inject;

import androidx.appcompat.app.AppCompatActivity;

public class AuthActivity extends AppCompatActivity {

    public static final String TAG = AuthActivity.class.getSimpleName();

    @Inject
    Logger logger;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);
        ((MyApplication) getApplication()).getAppComponent().inject(this);


    }

    public void startMainActivity(View view) {
        Intent myIntent = new Intent(AuthActivity.this, MainActivity.class);
        startActivity(myIntent);
        finish();

    }

}
