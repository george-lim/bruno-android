package com.cs446.group7.bruno.routing;

import com.google.android.gms.maps.model.LatLng;

public class RouteSegment {
    private LatLng startLocation;
    private LatLng endLocation;
    private long duration; // milliseconds

    public RouteSegment(final LatLng startLocation, final LatLng endLocation, final long milliseconds) {
        this.startLocation = startLocation;
        this.endLocation = endLocation;
        this.duration = milliseconds;
    }

    // Overload for duration in seconds
    public RouteSegment(final LatLng startLocation, final LatLng endLocation, final int seconds) {
        this(startLocation, endLocation, (long)seconds * 1000);
    }

    public LatLng getStartLocation() {
        return startLocation;
    }

    public LatLng getEndLocation() {
        return endLocation;
    }

    // Returns location in milliseconds
    public long getDuration() {
        return duration;
    }
}
