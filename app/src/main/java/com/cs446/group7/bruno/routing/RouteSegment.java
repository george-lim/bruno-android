package com.cs446.group7.bruno.routing;

import com.google.android.gms.maps.model.LatLng;

public class RouteSegment {
    private LatLng startLocation;
    private LatLng endLocation;
    private int duration;

    public RouteSegment(final LatLng startLocation, final LatLng endLocation, final int duration) {
        this.startLocation = startLocation;
        this.endLocation = endLocation;
        this.duration = duration;
    }

    public LatLng getStartLocation() {
        return startLocation;
    }

    public LatLng getEndLocation() {
        return endLocation;
    }

    public int getDuration() {
        return duration;
    }

    public long getDurationInMilliseconds() { return duration * 1000; }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }

        if (!(o instanceof RouteSegment)) {
            return false;
        }

        RouteSegment a = (RouteSegment) o;
        return a.startLocation.equals(this.startLocation) &&
                a.endLocation.equals(this.endLocation) && a.duration == this.duration;
    }
}