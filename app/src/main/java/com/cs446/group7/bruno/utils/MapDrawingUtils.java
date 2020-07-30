package com.cs446.group7.bruno.utils;

import com.cs446.group7.bruno.colourizedroute.ColourizedRoute;
import com.cs446.group7.bruno.colourizedroute.ColourizedRouteSegment;
import com.cs446.group7.bruno.routing.RouteSegment;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.List;

public class MapDrawingUtils {

    public static void drawColourizedRoute(final ColourizedRoute colourizedRoute,
                                           final GoogleMap map) {
        final float routeWidth = 14;
        final List<LatLng> segmentPoints = new ArrayList<>();

        for (ColourizedRouteSegment colourizedRouteSegment : colourizedRoute.getSegments()) {
            List<RouteSegment> routeSegments = colourizedRouteSegment.getRouteSegments();
            for (RouteSegment routeSegment : routeSegments) {
                segmentPoints.add(routeSegment.getStartLocation());
            }

            RouteSegment lastRouteSegment = routeSegments.get(routeSegments.size() - 1);
            segmentPoints.add(lastRouteSegment.getEndLocation());

            map.addPolyline(new PolylineOptions()
                    .addAll(segmentPoints)
                    .color(colourizedRouteSegment.getRouteColour())
                    .width(routeWidth));

            segmentPoints.clear();
        }
    }
}
