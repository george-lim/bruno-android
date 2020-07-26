package com.cs446.group7.bruno.utils;

import android.location.Location;

import com.google.android.gms.maps.model.LatLng;

public class LatLngUtils {
    private static final int EARTH_RADIUS_METRES = 6371000;

    private static double haversine(double theta) {
        return Math.sin(theta / 2) * Math.sin(theta / 2);
    }

    public static double getLatLngDistanceInMetres(final LatLng start, final LatLng end) {
        double diffLatRad = Math.toRadians(end.latitude - start.latitude);
        double diffLngRad = Math.toRadians(end.longitude - start.longitude);

        // the haversine formula for distance between two points on a sphere
        return 2 * EARTH_RADIUS_METRES * Math.asin(Math.sqrt(haversine(diffLatRad) +
                (Math.cos(start.latitude) * Math.cos(end.latitude) * haversine(diffLngRad))));
    }

    public static LatLng locationToLatLng(final Location location) {
        return new LatLng(location.getLatitude(), location.getLongitude());
    }

    public static boolean LatLngEquals(final LatLng pointA, final LatLng pointB) {
        return pointA.latitude == pointB.latitude &&
                pointA.longitude == pointB.longitude;
    }
}
