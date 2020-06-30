package com.cs446.group7.bruno.routing;

import android.content.Context;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Class used to generate routes using the Google maps API
 */
public abstract class RouteGenerator {
    static final String DIRECTIONS_ENDPOINT = "https://maps.googleapis.com/maps/api/directions/";
    static final double METRES_PER_LAT_DEG = 110947.2;
    static final double EARTH_CIRCUMFERENCE_METRES = 40075000;
    static final int DEG_PER_PI_RADIAN = 180;
    static final int NUMBER_OF_POINTS = 3;

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
     * @param totalDistance target route distance (meters)
     * @param rotation rotation of route w.r.t starting position (rad), 0 being due south
     */
    public abstract void generateRoute(final OnRouteReadyCallback callback, final LatLng start, double totalDistance, double rotation);

    /**
     *  Generate waypoints forming an equilateral triangle with perimeter {@code totalDistance} anchored
     * around {@code start} with rotation {@code rotation}.
     * @param start start location
     * @param totalDistance distance (meters)
     * @param rotation rotation (rad)
     * @return list of {@code LatLng} points in route order
     */
    static List<LatLng> generateWaypoints(final LatLng start, double totalDistance, double rotation) {
        final double distanceInLatLngDegree = distanceToLatLngDegree(start, totalDistance);
        final double l = distanceInLatLngDegree / NUMBER_OF_POINTS;
        final double a = 2 * Math.PI / NUMBER_OF_POINTS;
        final double r = l / Math.sqrt(2 * (1 - Math.cos(a)));

        List<LatLng> result = new ArrayList<>(NUMBER_OF_POINTS);

        // calculate points to form an equilateral triangle
        for (int i = 0; i < NUMBER_OF_POINTS; ++i) {
            final double b = i * a + rotation;
            result.add(new LatLng(
                    r * (Math.cos(b) - Math.cos(rotation)) + start.latitude,
                    r * (Math.sin(b) - Math.sin(rotation)) + start.longitude
            ));
        }
        return result;
    }

    /**
     * Parse the route generate response into the {@link Route} object
     *
     * @param routeJson json containing the raw response\
     */
    static Route parseRouteFromJson(final JSONObject routeJson) {
        try {
            return Route.parseFromJson(routeJson);
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Converts distance in meters to Latitude and longitude measures
     *
     * @param start starting location
     * @param totalDistance distance (m)
     */
    static double distanceToLatLngDegree(final LatLng start, double totalDistance) {
        final double metresPerLngDegree = metresPerLngDegree(start.latitude);
        // assuming that we typically travel in N-S direction as much as in E-W for now
        final double averageMetresPerLatLngDegree = (METRES_PER_LAT_DEG + metresPerLngDegree) / 2;
        return totalDistance / averageMetresPerLatLngDegree;
    }

    /**
     * Converts Latitude and longitude measures to distance in meters
     */
    static double metresPerLngDegree(double lat) {
        return (EARTH_CIRCUMFERENCE_METRES * Math.cos(lat * (Math.PI / DEG_PER_PI_RADIAN))) / (2 * DEG_PER_PI_RADIAN);
    }

    RouteGenerator(Context context, final String gMapsApiKey) {
        this.context = context;
        this.gMapsApiKey = gMapsApiKey;
    }
}
