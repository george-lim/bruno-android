package com.cs446.group7.bruno.routing;

import android.content.Context;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

public abstract class RouteGenerator {
    static final String DIRECTIONS_ENDPOINT = "https://maps.googleapis.com/maps/api/directions/";
    static final double METRES_PER_LAT_DEG = 110947.2;
    static final double EARTH_CIRCUMFERENCE_METRES = 40075000;
    static final int DEG_PER_PI_RADIAN = 180;

    Context context;
    final String gMapsApiKey;
    public final String TAG = this.getClass().getSimpleName();

    public static RouteGenerator create(Context context, final String gMapsApiKey, final boolean isMock) {
        return isMock ? new RouteGeneratorMockImpl(context, gMapsApiKey) : new RouteGeneratorRealImpl(context, gMapsApiKey);
    }

    /**
     * Selects a random path from the stored paths.
     *
     * @param callback callback handler
     * @param start start location
     * @param numPoints number points used to generated the route
     * @param totalDistance target route distance (meters)
     * @param rotation rotation of route w.r.t starting position (rad), 0 being due south
     */
    public abstract void generateRoute(final OnRouteReadyCallback callback, final LatLng start, int numPoints, double totalDistance, double rotation);

    /**
     *  Generate waypoints forming a regular {@code numPointed}-polygon with perimeter {@code totalDistance} anchored
     * around {@code start} with rotation {@code rotation}.
     * @param start start location
     * @param numPoints number of points ( >= 3 )
     * @param totalDistance distance (meters)
     * @param rotation rotation (rad)
     * @return list of {@code LatLng} points in route order
     */
    static List<LatLng> generateWaypoints(final LatLng start, int numPoints, double totalDistance, double rotation) {
        final double distanceInLatLngDegree = distanceToLatLngDegree(start, totalDistance);
        final double l = distanceInLatLngDegree / numPoints;
        final double a = 2 * Math.PI / numPoints;
        final double r = l / Math.sqrt(2 * (1 - Math.cos(a)));

        List<LatLng> result = new ArrayList<>(numPoints);

        for (int i = 0; i < numPoints; ++i) {
            final double b = i * a + rotation;
            result.add(new LatLng(
                    r * (Math.cos(b) - Math.cos(rotation)) + start.latitude,
                    r * (Math.sin(b) - Math.sin(rotation)) + start.longitude
            ));
        }
        return result;
    }

    static double distanceToLatLngDegree(final LatLng start, double totalDistance) {
        final double metresPerLngDegree = metresPerLngDegree(start.latitude);
        // assuming that we typically travel in N-S direction as much as in E-W for now
        final double averageMetresPerLatLngDegree = (METRES_PER_LAT_DEG + metresPerLngDegree) / 2;
        return totalDistance / averageMetresPerLatLngDegree;
}

    static double metresPerLngDegree(double lat) {
        return (EARTH_CIRCUMFERENCE_METRES * Math.cos(lat * (Math.PI / DEG_PER_PI_RADIAN))) / (2 * DEG_PER_PI_RADIAN);
    }

    RouteGenerator(Context context, final String gMapsApiKey) {
        this.context = context;
        this.gMapsApiKey = gMapsApiKey;
    }
}
