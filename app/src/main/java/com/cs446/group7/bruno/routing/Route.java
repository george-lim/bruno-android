package com.cs446.group7.bruno.routing;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.PolyUtil;

import java.util.ArrayList;
import java.util.List;

public class Route {
    private static final double WALKING_SPEED = 5000d / 3600d; // in m/s

    private List<LatLng> decodedPath;
    private List<RouteSegment> routeSegments;

    public Route(final String encodedPath) {
        this.decodedPath = PolyUtil.decode(encodedPath);
        this.routeSegments = calculateSegments(decodedPath);
    }

    public List<LatLng> getDecodedPath() {
        return decodedPath;
    }

    public List<RouteSegment> getRouteSegments() {
        return routeSegments;
    }

    private static List<RouteSegment> calculateSegments(final List<LatLng> decodedPath) {
        List<RouteSegment> routeSegments = new ArrayList<>();
        for (int i = 0; i < decodedPath.size() - 1; ++i) {
            double distanceMetres = LatLngUtils.getLatLngDistanceInMetres(decodedPath.get(i), decodedPath.get(i + 1));
            // should not overflow since (distanceMetres / WALKING_SPEED) is small enough
            double durationMs = (distanceMetres / WALKING_SPEED) * 1000;
            RouteSegment routeSegment = new RouteSegment(decodedPath.get(i), decodedPath.get(i + 1), (long) durationMs);
            routeSegments.add(routeSegment);
        }
        return routeSegments;
    }
}
