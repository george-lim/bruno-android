package com.cs446.group7.bruno.routing;

import com.cs446.group7.bruno.settings.SettingsService;
import com.cs446.group7.bruno.utils.LatLngUtils;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.PolyUtil;

import java.util.LinkedList;
import java.util.List;

public class Route {
    private List<LatLng> decodedPath;
    private List<RouteSegment> routeSegments;

    public Route(final String encodedPath) {
        this.decodedPath = PolyUtil.decode(encodedPath);
        this.routeSegments = calculateSegments(decodedPath);
    }

    public List<RouteSegment> getRouteSegments() {
        return routeSegments;
    }

    private static List<RouteSegment> calculateSegments(final List<LatLng> decodedPath) {
        List<RouteSegment> routeSegments = new LinkedList<>();
        for (int i = 0; i < decodedPath.size() - 1; ++i) {
            double distanceMetres = LatLngUtils.getLatLngDistanceInMetres(decodedPath.get(i), decodedPath.get(i + 1));
            // should not overflow since (distanceMetres / WALKING_SPEED) is small enough
            double durationMs = (distanceMetres / (SettingsService.PREFERRED_WALKING_SPEED / 60)) * 1000;
            RouteSegment routeSegment = new RouteSegment(decodedPath.get(i), decodedPath.get(i + 1), (long) durationMs);
            routeSegments.add(routeSegment);
        }
        return routeSegments;
    }
}
