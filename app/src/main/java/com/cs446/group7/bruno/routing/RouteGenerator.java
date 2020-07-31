package com.cs446.group7.bruno.routing;

import android.content.Context;

import com.cs446.group7.bruno.settings.SettingsService;
import com.cs446.group7.bruno.utils.LatLngUtils;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.PolyUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedList;
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

    /**
     * Selects a random path from the stored paths.
     *
     * @param callback callback handler
     * @param start start location
     * @param totalDistance target route distance (meters)
     * @param rotation rotation of route w.r.t starting position (rad), 0 being due south
     */
    public abstract void generateRoute(final OnRouteResponseCallback callback, final LatLng start, double totalDistance, double rotation);

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
     * Parse the route generate response into a list of route segments.
     *
     * @param routeJson json containing the raw response
     * @throws JSONException
     */
    static List<RouteSegment> parseRouteSegmentsFromJson(final JSONObject routeJson) throws JSONException {
        String encodedPath = routeJson.getJSONArray("routes")
                .getJSONObject(0)
                .getJSONObject("overview_polyline")
                .getString("points");

        List<LatLng> decodedPath = PolyUtil.decode(encodedPath);

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
