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
            // this distance is not accurate, see below. We currently always overestimate distance
            // so calculated duration is always greater than Google's estimate
            double distanceMetres = getLatLngDistance(decodedPath.get(i), decodedPath.get(i + 1))
                    * RouteGenerator.METRES_PER_LAT_DEG;
            // should not overflow since (distanceMetres / WALKING_SPEED) is very small
            double durationMs = (distanceMetres / WALKING_SPEED) * 1000;
            RouteSegment routeSegment = new RouteSegment(decodedPath.get(i), decodedPath.get(i + 1), (long) durationMs);
            routeSegments.add(routeSegment);
        }
        return routeSegments;
    }

    // Note: not a very accurate calculation since 1 lat deg != 1 lng degree, but the difference
    // should not be pronounced for short distances. We may need to adjust depending on how
    // challenging it is for the user to keep the pace that we set
    private static double getLatLngDistance(final LatLng start, final LatLng end) {
        double diffLat = end.latitude - start.latitude;
        double diffLng = end.longitude - start.longitude;
        return Math.sqrt((diffLat * diffLat) + (diffLng * diffLng));
    }
}
