package com.cs446.group7.bruno.routing;

import com.cs446.group7.bruno.location.Coordinate;
import com.cs446.group7.bruno.settings.SettingsService;

public class RouteSegment {
    private static final int MILLISECONDS_PER_MINUTE = 60000;
    private static final double RUNNING_SPEED_METRES_PER_MILLISECOND =
            SettingsService.PREFERRED_RUNNING_SPEED / MILLISECONDS_PER_MINUTE;

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

    // Sets duration to correspond to running speed
    public void setRunningDuration() {
        double distance = startCoordinate.getDistance(endCoordinate);
        duration = (long) (distance / RUNNING_SPEED_METRES_PER_MILLISECOND);
    }
}
