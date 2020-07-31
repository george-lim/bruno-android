package com.cs446.group7.bruno.models;

import android.location.Location;

import com.cs446.group7.bruno.colourizedroute.ColourizedRoute;
import com.cs446.group7.bruno.colourizedroute.ColourizedRouteSegment;
import com.cs446.group7.bruno.music.BrunoPlaylist;
import com.cs446.group7.bruno.music.BrunoTrack;
import com.cs446.group7.bruno.routing.RouteSegment;
import com.cs446.group7.bruno.utils.LatLngUtils;
import com.google.android.gms.maps.model.LatLng;

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
    private int currentCheckpointIndex = 0;
    private int currentTrackEndpointIndex = 0;
    private BrunoTrack currentTrack = null;
    private int steps = 0;

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

    public LatLng getCurrentCheckpoint() {
        if (colourizedRoute == null) {
            return null;
        }

        return colourizedRoute.getCheckpoints().get(currentCheckpointIndex);
    }

    public LatLng advanceCheckpoint() {
        if (colourizedRoute == null) {
            return null;
        }

        List<LatLng> checkpoints = colourizedRoute.getCheckpoints();
        if (currentCheckpointIndex >= checkpoints.size() - 1) return null;
        return checkpoints.get(++currentCheckpointIndex);
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

    public double getDistanceToTrackEndpoint() {
        LatLng currentCheckpoint = getCurrentCheckpoint();
        LatLng currentTrackEndpoint = getCurrentTrackEndpoint();

        // Fail-safe
        if (currentLocation == null || currentCheckpoint == null || currentTrackEndpoint == null) {
            return 0;
        }

        LatLng currentLatLng = LatLngUtils.locationToLatLng(currentLocation);
        double result = LatLngUtils.getLatLngDistanceInMetres(currentLatLng, currentCheckpoint);
        List<LatLng> checkpoints = colourizedRoute.getCheckpoints();

        for (int i = currentCheckpointIndex; !checkpoints.get(i).equals(currentTrackEndpoint); ++i) {
            result += LatLngUtils.getLatLngDistanceInMetres(
                    checkpoints.get(i),
                    checkpoints.get(i+1)
            );
        }

        return result;
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

    public void reset() {
        mode = Mode.WALK;
        durationIndex = 0;

        routeSegments = null;
        routeColours = null;
        playlist = null;
        colourizedRoute = null;

        currentLocation = null;
        currentCheckpointIndex = 0;
        currentTrackEndpointIndex = 0;
        currentTrack = null;
        steps = 0;
    }
}
