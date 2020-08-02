package com.cs446.group7.bruno.models;

import com.cs446.group7.bruno.routing.RouteSegment;
import com.cs446.group7.bruno.utils.LatLngUtils;
import com.google.android.gms.maps.model.LatLng;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class TrackSegment implements Serializable {

    // MARK: - Private members

    private List<LatLng> locations;
    private int routeColour;

    // MARK: - Lifecycle methods

    public TrackSegment(final List<RouteSegment> routeSegments, int routeColour) {
        locations = processLocations(routeSegments);
        this.routeColour = routeColour;
    }

    // MARK: - Private methods

    public List<LatLng> processLocations(final List<RouteSegment> routeSegments) {
        List<LatLng> locations = new ArrayList<>(routeSegments.size());

        for (RouteSegment routeSegment : routeSegments) {
            locations.add(routeSegment.getStartLocation());
        }

        RouteSegment lastRouteSegment = routeSegments.get(routeSegments.size() - 1);
        locations.add(lastRouteSegment.getEndLocation());

        return locations;
    }

    // MARK: - Public methods

    public List<LatLng> getLocations() {
        return locations;
    }

    public int getRouteColour() {
        return routeColour;
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
