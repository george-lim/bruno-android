package com.cs446.group7.bruno.routing;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;

public class Route {
    private String encodedPath;
    private List<LatLng> decodedPath;

    public Route(final String encodedPath, final List<LatLng> decodedPath) {
        this.encodedPath = encodedPath;
        this.decodedPath = decodedPath;
    }

    public String getEncodedPath() {
        return encodedPath;
    }

    public List<LatLng> getDecodedPath() {
        return decodedPath;
    }
}
