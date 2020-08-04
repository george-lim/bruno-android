package com.cs446.group7.bruno.models;

import com.cs446.group7.bruno.location.Coordinate;
import com.cs446.group7.bruno.routing.RouteSegment;

import java.util.ArrayList;
import java.util.List;

public class CheckpointsModel {

    // MARK: - Private members

    private List<Coordinate> checkpoints;
    private int checkpointIndex;

    // MARK: - Lifecycle methods

    public CheckpointsModel() {
        reset();
    }

    // MARK: - Private methods

    private List<Coordinate> processCheckpoints(final List<RouteSegment> routeSegments) {
        if (routeSegments == null) {
            checkpoints = null;
        }

        List<Coordinate> checkpoints = new ArrayList<>();

        for (RouteSegment routeSegment : routeSegments) {
            checkpoints.add(routeSegment.getStartCoordinate());
        }

        if (!checkpoints.isEmpty()) {
            checkpoints.add(routeSegments.get(routeSegments.size() - 1).getEndCoordinate());
        }

        return checkpoints;
    }

    // Returns distance from the start of the route to the current checkpoint
    private double getTotalDistanceToCheckpoint() {
        // Fail-safe
        if (hasCompletedAllCheckpoints()) {
            return 0;
        }

        double distance = 0;

        for (int i = 0; i < checkpointIndex; ++i) {
            distance += checkpoints.get(i).getDistance(checkpoints.get(i+1));
        }

        return distance;
    }

    // MARK: - Public methods

    public void setRouteSegments(final List<RouteSegment> routeSegments) {
        checkpoints = processCheckpoints(routeSegments);
    }

    public Coordinate getCurrentCheckpoint() {
        return checkpoints.get(checkpointIndex);
    }

    public void advanceCheckpoint() {
        checkpointIndex++;
    }

    // Returns distance travelled by the user on the route
    public double getUserRouteDistance(final Coordinate coordinate) {
        return getTotalDistanceToCheckpoint() - getDistanceToCheckpoint(coordinate);
    }

    // Returns distance from origin to current checkpoint
    public double getDistanceToCheckpoint(final Coordinate coordinate) {
        return coordinate.getDistance(getCurrentCheckpoint());
    }

    public boolean hasCompletedAllCheckpoints() {
        // Fail-safe
        if (checkpoints == null) {
            return true;
        }

        return checkpointIndex == checkpoints.size();
    }

    public void resetCheckpoint() {
        checkpointIndex = 0;
    }

    public void reset() {
        checkpoints = null;
        resetCheckpoint();
    }
}
