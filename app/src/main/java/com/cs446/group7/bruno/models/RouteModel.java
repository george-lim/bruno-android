package com.cs446.group7.bruno.models;

import android.location.Location;
import android.util.Log;

import com.cs446.group7.bruno.colourizedroute.ColourizedRoute;
import com.cs446.group7.bruno.colourizedroute.ColourizedRouteSegment;
import com.cs446.group7.bruno.music.BrunoPlaylist;
import com.cs446.group7.bruno.music.BrunoTrack;
import com.cs446.group7.bruno.routing.RouteSegment;
import com.google.android.gms.maps.model.LatLng;

import java.util.Date;
import java.util.List;

import androidx.lifecycle.ViewModel;

public class RouteModel extends ViewModel {

    // MARK: - Enums

    public enum Mode { WALK, RUN }

    // MARK: - Constants

    public static final int[] DURATIONS_IN_MINUTES = { 15, 30, 45, 60, 75 };

    // MARK: - Private members

    private Mode mode = Mode.WALK;
    private int durationIndex = 0;

    private List<RouteSegment> routeSegments = null;
    private int[] routeColours = null;
    private BrunoPlaylist playlist = null;
    private ColourizedRoute colourizedRoute = null;
    private Location currentLocation = null;
    private int currentTrackEndpointIndex = 0;
    private BrunoTrack currentTrack = null;
    private int steps = 0;
    private Date userStartTime = null;
    private Date userStopTime = null;

    private CheckpointsModel checkpointsModel = null;

    // MARK: - Private methods

    private void updateColourizedRoute() {
        if (routeSegments != null && routeColours != null && playlist != null) {
            colourizedRoute = new ColourizedRoute(routeSegments, routeColours, playlist);
        }
        else {
            colourizedRoute = null;
        }
    }

    // MARK: - Public methods

    public Mode getMode() {
        return mode;
    }

    public void setMode(Mode mode) {
        this.mode = mode;
    }

    public int getDurationIndex() {
        return durationIndex;
    }

    public void setDurationIndex(int durationIndex) {
        this.durationIndex = durationIndex;
    }

    public int getDurationInMinutes() {
        return DURATIONS_IN_MINUTES[durationIndex];
    }

    public void setRouteSegments(final List<RouteSegment> routeSegments) {
        this.routeSegments = routeSegments;
        this.checkpointsModel = new CheckpointsModel(routeSegments);
        updateColourizedRoute();
    }

    public void setRouteColours(final int[] routeColours) {
        this.routeColours = routeColours;
    }

    public BrunoPlaylist getPlaylist() {
        return playlist;
    }

    public void setPlaylist(final BrunoPlaylist playlist) {
        this.playlist = playlist;
        updateColourizedRoute();
    }

    public ColourizedRoute getColourizedRoute() {
        return colourizedRoute;
    }

    public Location getCurrentLocation() {
        return currentLocation;
    }

    public void setCurrentLocation(final Location currentLocation) {
        this.currentLocation = currentLocation;
    }

    public LatLng getCurrentTrackEndpoint() {
        if (colourizedRoute == null) {
            return null;
        }

        int colourizedRouteSegmentCount = colourizedRoute.getSegments().size();

        // index should always be valid because we would have finished the route otherwise
        if (currentTrackEndpointIndex >= colourizedRouteSegmentCount) {
            return null;
        }

        ColourizedRouteSegment currentColourizedSegment = colourizedRoute
                .getSegments()
                .get(currentTrackEndpointIndex);

        int routeSegmentCount = currentColourizedSegment.getRouteSegments().size();
        return currentColourizedSegment
                .getRouteSegments()
                .get(routeSegmentCount - 1)
                .getEndLocation();
    }

    public void advanceTrackEndpoint() {
        ++currentTrackEndpointIndex;
    }

    public BrunoTrack getCurrentTrack() {
        return currentTrack;
    }

    public void setCurrentTrack(final BrunoTrack currentTrack) {
        this.currentTrack = currentTrack;
    }

    public void incrementStep() {
        steps++;
    }

    public void setUserStartTime() {
        userStartTime = new Date();
    }

    public Date getUserStartTime() {
        return userStartTime;
    }

    public void setUserStopTime() {
        userStopTime = new Date();
    }

    public Date getUserStopTime() {
        return userStopTime;
    }

    public long getUserDuration() {
        if (userStartTime == null || userStopTime == null) {
            Log.w(getClass().getSimpleName(), "getUserDuration called but start/end time was not set first");
            return -1;
        }

        return userStopTime.getTime() - userStartTime.getTime(); // In Milliseconds
    }

    // MARK: - CheckpointsModel methods

    public LatLng getCurrentCheckpoint() {
        // Fail-safe
        if (hasCompletedAllCheckpoints()) {
            return null;
        }

        return checkpointsModel.getCurrentCheckpoint();
    }

    public void advanceCheckpoint() {
        // Fail-safe
        if (hasCompletedAllCheckpoints()) {
            return;
        }

        checkpointsModel.advanceCheckpoint();
    }

    public double getDistanceToCurrentCheckpoint() {
        // Fail-safe
        if (hasCompletedAllCheckpoints()) {
            return 0;
        }

        return checkpointsModel.getDistanceToCurrentCheckpoint(currentLocation);
    }

    public boolean hasCompletedAllCheckpoints() {
        // Fail-safe
        if (checkpointsModel == null) {
            return true;
        }

        return checkpointsModel.hasCompletedAllCheckpoints();
    }

    /**
     * Resets the progress of the current route, and stats, but keeps the route and checkpoints.
     */
    public void softReset() {
        currentTrackEndpointIndex = 0;
        userStartTime = null;
        userStopTime = null;

        checkpointsModel.resetCheckpoint();
    }

    /**
     * Resets everything to the state it was first constructed.
     */
    public void hardReset() {
        softReset();
        mode = Mode.WALK;
        durationIndex = 0;
        routeSegments = null;
        routeColours = null;
        playlist = null;
        colourizedRoute = null;
        currentLocation = null;
        checkpointsModel = null;
    }
}
