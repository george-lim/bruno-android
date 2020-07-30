package com.cs446.group7.bruno.colourizedroute;

import com.cs446.group7.bruno.routing.RouteSegment;

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

    public int getRouteColour() {
        return routeColour;
    }
}
