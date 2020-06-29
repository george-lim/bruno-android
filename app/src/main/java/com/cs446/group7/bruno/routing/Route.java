package com.cs446.group7.bruno.routing;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;

public class Route {
    private List<LatLng> waypoints;
    private String encodedPath;
    private List<LatLng> decodedPath;
    private double routeDistance;

    public Route(final List<LatLng> waypoints, final String encodedPath, final List<LatLng> decodedPath, double routeDistance) {
        this.waypoints = waypoints;
        this.encodedPath = encodedPath;
        this.decodedPath = decodedPath;
        this.routeDistance = routeDistance;
    }

    public List<LatLng> getMarkers() {
        return waypoints;
    }

    public String getEncodedPath() {
        return encodedPath;
    }

    public List<LatLng> getDecodedPath() {
        return decodedPath;
    }

    public double getRouteDistance() { return routeDistance; }
}
