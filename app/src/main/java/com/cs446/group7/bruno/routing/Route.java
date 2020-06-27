package com.cs446.group7.bruno.routing;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;

public class Route {
    private List<LatLng> markers;

    private String encodedPath;

    private List<LatLng> decodedPath;

    public Route(final List<LatLng> markers, final String encodedPath, final List<LatLng> decodedPath) {
        this.markers = markers;
        this.encodedPath = encodedPath;
        this.decodedPath = decodedPath;
    }


    public List<LatLng> getMarkers() {
        return markers;
    }

    public String getEncodedPath() {
        return encodedPath;
    }

    public List<LatLng> getDecodedPath() {
        return decodedPath;
    }
}
