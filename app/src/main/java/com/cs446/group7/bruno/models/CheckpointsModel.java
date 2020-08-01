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

    public CheckpointsModel(final List<RouteSegment> routeSegments) {
        checkpoints = processCheckpoints(routeSegments);
        checkpointIndex = 0;
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

    // MARK: - Public methods

    public LatLng getCurrentCheckpoint() {
        return checkpoints.get(checkpointIndex);
    }

    public void advanceCheckpoint() {
        checkpointIndex++;
    }

    public void resetCheckpoint() {
        checkpointIndex = 0;
    }

    public double getDistanceToCurrentCheckpoint(final Location origin) {
        LatLng originLatLng = LatLngUtils.locationToLatLng(origin);
        return LatLngUtils.getLatLngDistanceInMetres(originLatLng, getCurrentCheckpoint());
    }

    public boolean hasCompletedAllCheckpoints() {
        return checkpointIndex == checkpoints.size();
    }
}
