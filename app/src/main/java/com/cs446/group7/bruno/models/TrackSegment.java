package com.cs446.group7.bruno.models;

import com.cs446.group7.bruno.location.Coordinate;
import com.cs446.group7.bruno.routing.RouteSegment;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

public class TrackSegment {

    // MARK: - Private members

    private List<Coordinate> coordinates;
    private transient List<LatLng> latLngs;
    private int routeColour;

    // MARK: - Lifecycle methods

    public TrackSegment(final List<RouteSegment> routeSegments,
                        int routeColour) {
        processCoordinates(routeSegments);
        this.routeColour = routeColour;
    }

    // MARK: - Private methods

    public void processCoordinates(final List<RouteSegment> routeSegments) {
        coordinates = new ArrayList<>(routeSegments.size());
        latLngs = new ArrayList<>(routeSegments.size());

        for (RouteSegment routeSegment : routeSegments) {
            Coordinate startCoordinate = routeSegment.getStartCoordinate();
            coordinates.add(startCoordinate);
            latLngs.add(startCoordinate.getLatLng());
        }

        RouteSegment lastRouteSegment = routeSegments.get(routeSegments.size() - 1);
        Coordinate endCoordinate = lastRouteSegment.getStartCoordinate();
        coordinates.add(endCoordinate);
        latLngs.add(endCoordinate.getLatLng());
    }

    // MARK: - Public methods

    public List<Coordinate> getCoordinates() {
        return coordinates;
    }

    public List<LatLng> getLatLngs() {
        return latLngs;
    }

    public int getRouteColour() {
        return routeColour;
    }

    // Returns distance of the track segment
    public double getDistance() {
        List<Coordinate> coordinates = getCoordinates();
        double distance = 0;

        for (int i = 0; i+1 < coordinates.size(); ++i) {
            distance += coordinates.get(i).getDistance(coordinates.get(i+1));
        }

        return distance;
    }
}
