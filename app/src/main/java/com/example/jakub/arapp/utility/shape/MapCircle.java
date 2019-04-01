package com.example.jakub.arapp.utility.shape;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.annotations.SerializedName;

import lombok.Getter;
import lombok.Setter;

public class MapCircle {

    @Setter
    @Getter
    @SerializedName("location")
    private LatLng location;
    @Setter
    @Getter
    private int ratio;

    public MapCircle(LatLng location, int ratio) {
        this.location = location;
        this.ratio = ratio;
    }

    public MapCircle() {
    }

}
