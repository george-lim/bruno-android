package com.cs446.group7.bruno.models;

import android.location.Location;

import com.cs446.group7.bruno.routing.RouteSegment;
import com.cs446.group7.bruno.utils.LatLngUtils;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

public class CheckpointsModel {

    // MARK: - Private members

    private List<LatLng> checkpoints;
    private int checkpointIndex;

    // MARK: - Lifecycle methods

    public CheckpointsModel() {
        reset();
    }

    // MARK: - Private methods

    private List<LatLng> processCheckpoints(final List<RouteSegment> routeSegments) {
        List<LatLng> checkpoints = new ArrayList<>();

        for (RouteSegment routeSegment : routeSegments) {
            checkpoints.add(routeSegment.getStartLocation());
        }

        if (!checkpoints.isEmpty()) {
            checkpoints.add(routeSegments.get(routeSegments.size() - 1).getEndLocation());
        }

        return checkpoints;
    }

    // Returns distance from the start of the route to the current checkpoint
    private double getTotalDistanceToCheckpoint() {
        double distance = 0;

        for (int i = 0; i < checkpointIndex; ++i) {
            distance += LatLngUtils.getLatLngDistanceInMetres(
                    checkpoints.get(i),
                    checkpoints.get(i+1)
            );
        }

        return distance;
    }

    // MARK: - Public methods

    public void setRouteSegments(final List<RouteSegment> routeSegments) {
        checkpoints = processCheckpoints(routeSegments);
    }

    public LatLng getCurrentCheckpoint() {
        return checkpoints.get(checkpointIndex);
    }

    public void advanceCheckpoint() {
        checkpointIndex++;
    }

    // Returns distance travelled by the user on the route
    public double getUserRouteDistance(final Location userLocation) {
        return getTotalDistanceToCheckpoint() - getDistanceToCheckpoint(userLocation);
    }

    // Returns distance from origin to current checkpoint
    public double getDistanceToCheckpoint(final Location origin) {
        LatLng originLatLng = LatLngUtils.locationToLatLng(origin);
        return LatLngUtils.getLatLngDistanceInMetres(originLatLng, getCurrentCheckpoint());
    }

    public boolean hasCompletedAllCheckpoints() {
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
