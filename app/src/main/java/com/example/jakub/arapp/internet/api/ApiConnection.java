package com.example.jakub.arapp.internet.api;

import com.example.jakub.arapp.model.User;
import com.example.jakub.arapp.model.device.InternetDeviceWrapper;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Single;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface ApiConnection {


    @GET("rest/allDevice")
    Single<List<InternetDeviceWrapper>> getAllInternetDevice(@Header("Authorisation")String token);

    // Create name

    @POST("auth/register")
    Single<String> registerNewUser(@Body User user);


    @POST("auth/login")
    Single<String> loginUser(@Body User user);

}
