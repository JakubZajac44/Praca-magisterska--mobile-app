package com.example.jakub.arapp.model.device;

import android.os.Parcel;
import android.os.Parcelable;

import lombok.Getter;
import lombok.Setter;

public class InternetDeviceWrapper extends IoTDevice implements Parcelable {

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public InternetDeviceWrapper createFromParcel(Parcel in) {
            return new InternetDeviceWrapper(in);
        }

        public InternetDeviceWrapper[] newArray(int size) {
            return new InternetDeviceWrapper[size];
        }
    };

    @Getter
    @Setter
    int id;
    @Getter
    @Setter
    double lat;
    @Getter
    @Setter
    double lon;
    @Getter
    @Setter
    String updatetime;

    public InternetDeviceWrapper() {
    }

    public InternetDeviceWrapper(Parcel in) {
        id = in.readInt();
        name = in.readString();
        sample = in.readString();
        lat = in.readDouble();
        lon = in.readDouble();
        updatetime = in.readString();
    }

    @Override
    public String toString() {
        return "InternetDeviceWrapper{" +
                "id=" + id +
                ", sample='" + sample + '\'' +
                ", lat=" + lat +
                ", lon=" + lon +
                ", updatetime='" + updatetime +
                ", name='" + name + '\'' +
                ", status=" + status +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(id);
        parcel.writeString(name);
        parcel.writeString(sample);
        parcel.writeDouble(lat);
        parcel.writeDouble(lon);
        parcel.writeString(updatetime);
    }
}
