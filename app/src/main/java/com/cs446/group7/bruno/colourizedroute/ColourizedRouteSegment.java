package com.cs446.group7.bruno.colourizedroute;

import com.cs446.group7.bruno.routing.RouteSegment;
import com.google.android.gms.maps.model.LatLng;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

public class ColourizedRouteSegment implements Serializable {
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
        List<LatLng> locations = new LinkedList<>();

        for (RouteSegment routeSegment : routeSegments) {
            locations.add(routeSegment.getStartLocation());
        }

        RouteSegment lastRouteSegment = routeSegments.get(routeSegments.size() - 1);
        locations.add(lastRouteSegment.getEndLocation());

        return locations;
    }

    public int getRouteColour() {
        return routeColour;
    }
}
