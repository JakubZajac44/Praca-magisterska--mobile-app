package com.example.jakub.arapp.internetManager.api;

import com.example.jakub.arapp.model.User;
import com.example.jakub.arapp.model.device.InternetDeviceWrapper;

import java.util.List;

import io.reactivex.Single;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface ApiConnection {


    @GET("rest/allDevice")
    Single<List<InternetDeviceWrapper>> getAllInternetDevice(@Header("Authorisation")String token);

    // Create name

    @POST("auth/register")
    Single<String> registerNewUser(@Body User user);


    @POST("auth/login")
    Single<String> loginUser(@Body User user);

}
