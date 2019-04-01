package com.example.jakub.arapp.dataBase.data.internet;

import javax.annotation.Nullable;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "internet_device")
public class InternetDevice {

    @NonNull
    String sample;
    @NonNull
    double lat;
    @NonNull
    double lon;
    @NonNull
    String updateTime;
    @NonNull
    @PrimaryKey()
    int id;
    @Nullable
    String name;

    public InternetDevice() {
    }

    @Nullable
    public String getName() {
        return name;
    }

    public void setName(@Nullable String name) {
        this.name = name;
    }

    @NonNull
    public String getSample() {
        return sample;
    }

    public void setSample(@NonNull String sample) {
        this.sample = sample;
    }

    @NonNull
    public double getLat() {
        return lat;
    }

    public void setLat(@NonNull double lat) {
        this.lat = lat;
    }

    @NonNull
    public double getLon() {
        return lon;
    }

    public void setLon(@NonNull double lon) {
        this.lon = lon;
    }

    @NonNull
    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(@NonNull String updateTime) {
        this.updateTime = updateTime;
    }

    @NonNull
    public int getId() {
        return id;
    }

    public void setId(@NonNull int id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "InternetDevice{" +
                "sample='" + sample + '\'' +
                ", lat=" + lat +
                ", lon=" + lon +
                ", updateTime='" + updateTime + '\'' +
                ", id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}
