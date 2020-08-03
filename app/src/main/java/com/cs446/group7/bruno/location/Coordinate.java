package com.cs446.group7.bruno.location;

import android.location.Location;

import com.google.android.gms.maps.model.LatLng;

import java.io.Serializable;

public class Coordinate implements Serializable {

    // MARK: - Private constants

    private static final int EARTH_RADIUS_METRES = 6371000;

    // MARK: - Private members

    private double latitude;
    private double longitude;

    // MARK: - Lifecycle methods

    public Coordinate(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public Coordinate(final LatLng latLng) {
        this(latLng.latitude, latLng.longitude);
    }

    public Coordinate(final Location location) {
        this(location.getLatitude(), location.getLongitude());
    }

    // MARK: - Private methods

    private double haversine(double theta) {
        return Math.sin(theta / 2) * Math.sin(theta / 2);
    }

    // MARK: - Public methods

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    // Distance is measured in meters
    public double getDistance(final Coordinate destination) {
        double diffLatRad = Math.toRadians(destination.latitude - latitude);
        double diffLngRad = Math.toRadians(destination.longitude - longitude);

        // The haversine formula for distance between two points on a sphere
        return 2 * EARTH_RADIUS_METRES * Math.asin(Math.sqrt(haversine(diffLatRad) +
                (Math.cos(latitude) * Math.cos(destination.latitude) * haversine(diffLngRad))));
    }

    // Returns a proportional coordinate between this and destination using linear interpolation
    public Coordinate getSplitPoint(final Coordinate destination, double proportion) {
        double latitudeDifference = destination.latitude - latitude;
        double longitudeDifference = destination.longitude - longitude;
        return new Coordinate(
                latitude + proportion * latitudeDifference,
                longitude + proportion * longitudeDifference
        );
    }

    public LatLng getLatLng() {
        return new LatLng(latitude, longitude);
    }
}
