package com.cs446.group7.bruno.routing;

import com.google.android.gms.maps.model.LatLng;

import java.io.Serializable;

public class RouteSegment implements Serializable {
    private transient LatLng startLocation;
    private transient LatLng endLocation;

    // Used for serialization since LatLng's cannot be serialized
    private double startLocationLat;
    private double startLocationLng;
    private double endLocationLat;
    private double endLocationLng;

    private long duration; // milliseconds

    public RouteSegment(final LatLng startLocation, final LatLng endLocation, final long milliseconds) {
        this.startLocation = startLocation;
        this.endLocation = endLocation;
        this.startLocationLat = startLocation.latitude;
        this.startLocationLng = startLocation.longitude;
        this.endLocationLat = endLocation.latitude;
        this.endLocationLng = endLocation.longitude;
        this.duration = milliseconds;
    }

    public LatLng getStartLocation() {
        if (startLocation == null) {
            startLocation = new LatLng(startLocationLat, startLocationLng);
        }
        return startLocation;
    }

    public LatLng getEndLocation() {
        if (endLocation == null) {
            endLocation = new LatLng(endLocationLat, endLocationLng);
        }
        return endLocation;
    }

    // Returns location in milliseconds
    public long getDuration() {
        return duration;
    }
}
