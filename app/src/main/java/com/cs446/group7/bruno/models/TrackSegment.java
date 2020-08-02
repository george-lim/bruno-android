package com.cs446.group7.bruno.models;

import com.cs446.group7.bruno.routing.RouteSegment;
import com.cs446.group7.bruno.utils.LatLngUtils;
import com.google.android.gms.maps.model.LatLng;

<<<<<<< HEAD:app/src/main/java/com/cs446/group7/bruno/colourizedroute/ColourizedRouteSegment.java
import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

public class ColourizedRouteSegment implements Serializable {
=======
import java.util.ArrayList;
import java.util.List;

public class TrackSegment {
>>>>>>> master:app/src/main/java/com/cs446/group7/bruno/models/TrackSegment.java
    private List<RouteSegment> routeSegments;
    private int routeColour;

    public TrackSegment(final List<RouteSegment> routeSegments,
                        int routeColour) {
        this.routeSegments = routeSegments;
        this.routeColour = routeColour;
    }

    public int getRouteColour() {
        return routeColour;
    }

    public List<LatLng> getLocations() {
        List<LatLng> locations = new ArrayList<>(routeSegments.size());

        for (RouteSegment routeSegment : routeSegments) {
            locations.add(routeSegment.getStartLocation());
        }

        RouteSegment lastRouteSegment = routeSegments.get(routeSegments.size() - 1);
        locations.add(lastRouteSegment.getEndLocation());

        return locations;
    }

    // Returns distance of the track segment
    public double getDistance() {
        List<LatLng> locations = getLocations();
        double distance = 0;

        for (int i = 0; i+1 < locations.size(); ++i) {
            distance += LatLngUtils.getLatLngDistanceInMetres(locations.get(i), locations.get(i+1));
        }

        return distance;
    }
}
