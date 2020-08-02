package com.cs446.group7.bruno.models;

import com.cs446.group7.bruno.location.Coordinate;
import com.cs446.group7.bruno.routing.RouteSegment;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

public class TrackSegment {

    // MARK: - Private members

    private List<Coordinate> coordinates;
    private int routeColour;

    // MARK: - Lifecycle methods

    public TrackSegment(final List<RouteSegment> routeSegments,
                        int routeColour) {
        coordinates = processCoordinates(routeSegments);
        this.routeColour = routeColour;
    }

    // MARK: - Private methods

    public List<Coordinate> processCoordinates(final List<RouteSegment> routeSegments) {
        List<Coordinate> coordinates = new ArrayList<>(routeSegments.size());

        for (RouteSegment routeSegment : routeSegments) {
            coordinates.add(routeSegment.getStartCoordinate());
        }

        RouteSegment lastRouteSegment = routeSegments.get(routeSegments.size() - 1);
        coordinates.add(lastRouteSegment.getEndCoordinate());

        return coordinates;
    }

    // MARK: - Public methods

    public List<Coordinate> getCoordinates() {
        return coordinates;
    }

    public List<LatLng> getLatLngs() {
        List<LatLng> latLngs = new ArrayList<>(coordinates.size());

        for (Coordinate coordinate : coordinates) {
            latLngs.add(coordinate.getLatLng());
        }

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
