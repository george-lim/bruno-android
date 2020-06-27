package com.cs446.group7.bruno.routing;

import com.google.android.gms.maps.model.LatLng;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class RouteGenerator {

    private RouteGenerator() {}

    public static Route generateRoute(final LatLng start, int numPoints, double totalDistance, double rotation) {
        final List<LatLng> markers = generateWaypoints(start, numPoints, totalDistance, rotation);
        final String encodedPath = "abc";
        final List<LatLng> decodedPath = new ArrayList<>();

        return new Route(markers, encodedPath, decodedPath);
    }

    private static List<LatLng> generateWaypoints(final LatLng start, int numPoints, double totalDistance, double rotation) {
        if (numPoints < 3) {
            throw new IllegalArgumentException(String.format("numPoints needs to be at least 3, %s is given", numPoints));
        }
        final double l = totalDistance / numPoints;
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

    private void generatePath() {
        // call google API
    }
}
