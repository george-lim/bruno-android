package com.cs446.group7.bruno.models;

import android.location.Location;

import com.cs446.group7.bruno.location.Coordinate;
import com.cs446.group7.bruno.routing.RouteSegment;
import com.cs446.group7.bruno.settings.SettingsService;

import java.util.ArrayList;
import java.util.List;

public class CheckpointsModel {

    // MARK: - Private constants

    private static final int BASE_TOLERANCE_RADIUS = 10;
    private static final int EXTRA_TOLERANCE_MARGIN = 1;

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
            return null;
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

    public void updateCurrentCheckpoint(final Location currentLocation) {
        if (hasCompletedAllCheckpoints()) {
            return;
        }

        /*
            Set a tolerance radius depending on how fast the user is moving. The faster they are, the more
            margin we should give them. It should also depend on how accurate the GPS is, the more variance, the bigger
            the margin should be given.
         */

        // give extra tolerance if the user is moving faster as their location is more uncertain
        final double speedMargin = Math.min(SettingsService.PREFERRED_RUNNING_SPEED / 60 + EXTRA_TOLERANCE_MARGIN,
                currentLocation.getSpeed());

        // max amount of deviation from the actual location (meters)
        final double accuracyDeviation = currentLocation.getAccuracy();

        // Update tolerance radius
        double toleranceRadius = BASE_TOLERANCE_RADIUS + speedMargin + accuracyDeviation;

        Coordinate currentCoordinate = new Coordinate(currentLocation);

        // Continuously advance checkpoint if current location is within tolerance radius
        while (getDistanceToCheckpoint(currentCoordinate) <= toleranceRadius) {
            checkpointIndex++;
        }
    }

    /*
        This member refers to the checkpoint radius the user sees, which may not be the same as the
        internal tolerance radius used in the checkpoint calculations.
     */
    public double getCheckpointRadius() {
        return BASE_TOLERANCE_RADIUS;
    }

    // Returns distance travelled by the user on the route
    public double getUserRouteDistance(final Coordinate coordinate) {
        return getTotalDistanceToCheckpoint() - getDistanceToCheckpoint(coordinate);
    }

    // Returns distance from origin to current checkpoint
    public double getDistanceToCheckpoint(final Coordinate coordinate) {
        if (hasCompletedAllCheckpoints()) {
            return 0;
        }

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
