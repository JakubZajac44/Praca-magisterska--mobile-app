package com.example.jakub.arapp.utility;

import android.location.Location;
import android.location.LocationManager;

import com.google.android.gms.maps.model.LatLng;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class MathOperation {

    public static <T extends Number> Number round(T value, int places) throws NumberFormatException {
        if (places < 0) throw new IllegalArgumentException();
        BigDecimal bd;
        if (value instanceof Double) {
            bd = new BigDecimal((Double) value);
            bd = bd.setScale(places, RoundingMode.HALF_UP);
            return bd.doubleValue();
        } else if (value instanceof Float) {
            bd = new BigDecimal((Float) value);
            bd = bd.setScale(places, RoundingMode.HALF_UP);
            return bd.floatValue();
        } else if (value instanceof Long) {
            bd = new BigDecimal((Long) value);
            bd = bd.setScale(places, RoundingMode.HALF_UP);
            return bd.longValue();
        } else throw new NumberFormatException();

    }

    public static <T extends Number> String numberToStringRound(T value, int places) {
        try {
            Number newValue = MathOperation.round(value, places);
            String stringValue = String.valueOf(newValue);
            return stringValue;
        } catch (NumberFormatException e) {
            return "Błędny format danych";
        }

    }

    public static double measureDistanceBetweenKm(Location location1, Location location2) {
        double latDistance = Math.toRadians(location1.getLatitude() - location2.getLatitude());
        double lonDistance = Math.toRadians(location1.getLongitude() - location2.getLongitude());
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(location1.getLatitude())) * Math.cos(Math.toRadians(location2.getLatitude()))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = Constants.ERATH_R * c;
        return distance;
    }

    public static double measureDistanceBetweenM(Location location1, Location location2) {
        double distance = 1000* measureDistanceBetweenKm(location1,location2);
        return distance;
    }

    public static Location getLocation(LatLng latLng){
        Location locationFromLatLng = new Location(LocationManager.GPS_PROVIDER);
        locationFromLatLng.setLatitude(latLng.latitude);
        locationFromLatLng.setLongitude(latLng.longitude);
        return locationFromLatLng;
    }
    public static Location getLocation(double lat,double lng){
        Location locationFromDouble = new Location(LocationManager.GPS_PROVIDER);
        locationFromDouble.setLatitude(lat);
        locationFromDouble.setLongitude(lng);
        return locationFromDouble;
    }

    public static double angleFromCoordinate(double lat1, double long1, double lat2,
                                      double long2) {
        double dLon = (long2 - long1);

        double y = Math.sin(dLon) * Math.cos(lat2);
        double x = Math.cos(lat1) * Math.sin(lat2) - Math.sin(lat1)
                * Math.cos(lat2) * Math.cos(dLon);

        double brng = Math.atan2(y, x);

        brng = Math.toDegrees(brng);
        brng = (brng + 360) % 360;
        brng = 360 - brng;

        return brng;
    }
    public static double angleFromLatLong(LatLng latLng1, LatLng latLng2){
        return MathOperation.angleFromCoordinate(latLng1.latitude,latLng1.longitude,latLng2.latitude,latLng2.longitude);
    }

    public static double angleFromLocation(Location location1,Location location2){
        return MathOperation.angleFromCoordinate(location1.getLatitude(),location1.getLongitude(),location2.getLatitude(),location2.getLongitude());
    }

}
