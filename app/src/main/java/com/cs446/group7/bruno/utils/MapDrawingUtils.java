package com.cs446.group7.bruno.utils;

import com.cs446.group7.bruno.routing.RouteSegment;
import com.cs446.group7.bruno.routing.RouteTrackMapping;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.List;

public class MapDrawingUtils {

    public static void drawColourizedRoute(final List<RouteTrackMapping> routeTrackMappings,
                                           final int[] colours,
                                           final GoogleMap map) {
        final float routeWidth = 14;
        final List<LatLng> segmentPoints = new ArrayList<>();

        for (int i = 0; i < routeTrackMappings.size(); ++i) {
            RouteTrackMapping rtm = routeTrackMappings.get(i);
            for (RouteSegment rs : rtm.routeSegments) {
                segmentPoints.add(rs.getStartLocation());
            }
            segmentPoints.add(rtm.routeSegments.get(rtm.routeSegments.size() - 1).getEndLocation());

            map.addPolyline(new PolylineOptions()
                    .addAll(segmentPoints)
                    .color(colours[i % colours.length])
                    .width(routeWidth));

            segmentPoints.clear();
        }
    }
}
