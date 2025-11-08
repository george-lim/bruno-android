package com.bruno.android.routing;

import com.bruno.android.location.Coordinate;
import com.bruno.android.models.RouteModel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

class ComputeRoutesRequest implements Serializable {
    Waypoint origin;
    Waypoint destination;
    List<Waypoint> intermediates;
    String travelMode = "WALK";
    Boolean computeAlternativeRoutes = false;
    String languageCode = "en-US";
    String units = "METRIC";

    public ComputeRoutesRequest(Coordinate origin, Coordinate destination, List<Coordinate> intermediates) {
        this.origin = createWaypoint(origin);
        this.destination = createWaypoint(destination);
        this.intermediates = intermediates.stream().map(ComputeRoutesRequest::createWaypoint).collect(Collectors.toList());
    }

    private static Waypoint createWaypoint(Coordinate coordinate) {
        return new Waypoint(new Location(coordinate));
    }
}

class Waypoint implements Serializable {
    Location location;

    Waypoint(Location location) {
        this.location = location;
    }
}

class Location implements Serializable {
    Coordinate latLng;

    Location(Coordinate coordinate) {
        this.latLng = coordinate;
    }
}