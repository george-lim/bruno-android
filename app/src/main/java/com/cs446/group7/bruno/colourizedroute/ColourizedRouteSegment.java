package com.cs446.group7.bruno.colourizedroute;

import com.cs446.group7.bruno.routing.RouteSegment;
import com.cs446.group7.bruno.utils.LatLngUtils;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

public class ColourizedRouteSegment {
    private List<RouteSegment> routeSegments;
    private int routeColour;

    public ColourizedRouteSegment(final List<RouteSegment> routeSegments,
                                  int routeColour) {
        this.routeSegments = routeSegments;
        this.routeColour = routeColour;
    }

    public List<RouteSegment> getRouteSegments() {
        return routeSegments;
    }

    public List<LatLng> getLocations() {
        List<LatLng> locations = new ArrayList<>();

        for (RouteSegment routeSegment : routeSegments) {
            locations.add(routeSegment.getStartLocation());
        }

        RouteSegment lastRouteSegment = routeSegments.get(routeSegments.size() - 1);
        locations.add(lastRouteSegment.getEndLocation());

        return locations;
    }

    // Returns distance of the colourized route segment
    public double getDistance() {
        List<LatLng> locations = getLocations();
        double distance = 0;

        for (int i = 0; i+1 < locations.size(); ++i) {
            distance += LatLngUtils.getLatLngDistanceInMetres(locations.get(i), locations.get(i+1));
        }

        return distance;
    }

    public int getRouteColour() {
        return routeColour;
    }
}
