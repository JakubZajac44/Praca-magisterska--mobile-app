package com.example.jakub.arapp.internet.api;

import com.example.jakub.arapp.model.device.InternetDeviceWrapper;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Single;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface ApiService {


    @GET("home")
    Single<List<InternetDeviceWrapper>> getAllInternetDevice();





    // Create name
    @FormUrlEncoded
    @POST("notes/new")
    Single<InternetDeviceWrapper> createNote(@Field("name") String note);

    // Fetch all notes


    // Update single name
    @FormUrlEncoded
    @PUT("notes/{id}")
    Completable updateNote(@Path("id") int noteId, @Field("name") String note);

    // Delete name
    @DELETE("notes/{id}")
    Completable deleteNote(@Path("id") int noteId);
}
