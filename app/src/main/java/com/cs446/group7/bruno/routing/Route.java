package com.cs446.group7.bruno.routing;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.PolyUtil;

import java.util.List;

public class Route {
    private List<LatLng> decodedPath;
    private List<RouteSegment> routeSegments;

    public Route(final String encodedPath, final List<RouteSegment> routeSegments) {
        this.decodedPath = PolyUtil.decode(encodedPath);
        this.routeSegments = routeSegments;
    }

    public List<LatLng> getDecodedPath() {
        return decodedPath;
    }

    public int getTotalDuration() {
        int totalDuration = 0;
        for (RouteSegment routeSegment : routeSegments) {
            totalDuration += routeSegment.getDuration();
        }
        return  totalDuration;
    }
}
