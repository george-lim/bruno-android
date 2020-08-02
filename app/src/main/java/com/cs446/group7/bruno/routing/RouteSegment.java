package com.cs446.group7.bruno.routing;

import com.cs446.group7.bruno.location.Coordinate;

public class RouteSegment {
    private Coordinate startCoordinate;
    private Coordinate endCoordinate;
    private long duration; // milliseconds

    public RouteSegment(final Coordinate startCoordinate,
                        final Coordinate endCoordinate,
                        final long duration) {
        this.startCoordinate = startCoordinate;
        this.endCoordinate = endCoordinate;
        this.duration = duration;
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
