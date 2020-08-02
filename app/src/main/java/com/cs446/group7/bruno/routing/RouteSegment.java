package com.cs446.group7.bruno.routing;

import com.cs446.group7.bruno.location.Coordinate;

public class RouteSegment {
    private Coordinate startCoordinate;
    private Coordinate endCoordinate;
    private long duration; // milliseconds

    public RouteSegment(final Coordinate startCoordinate,
                        final Coordinate endCoordinate,
                        final long milliseconds) {
        this.startCoordinate = startCoordinate;
        this.endCoordinate = endCoordinate;
        this.duration = milliseconds;
    }

    public Coordinate getStartCoordinate() {
        return startCoordinate;
    }

    public Coordinate getEndCoordinate() {
        return endCoordinate;
    }

    // Returns duration in milliseconds
    public long getDuration() {
        return duration;
    }
}
