package com.cs446.group7.bruno.models;

import android.location.Location;

import com.cs446.group7.bruno.music.BrunoPlaylist;
import com.cs446.group7.bruno.music.BrunoTrack;
import com.cs446.group7.bruno.routing.Route;
import com.cs446.group7.bruno.routing.RouteTrackMapping;
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
    private Route route = null;
    private Location currentLocation = null;
    private BrunoTrack currentTrack = null;
    private BrunoPlaylist playlist = null;
    private List<RouteTrackMapping> routeTrackMappings = null;
    private List<LatLng> routeCheckpoints = null;
    private int steps = 0;
    private int currentCheckpointIndex = 0;
    private int currentTrackEndpointIndex = 0;

    // MARK: - Getters and setters

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

    public Route getRoute() {
        return route;
    }

    public void setRoute(final Route route) {
        this.route = route;
    }

    public Location getCurrentLocation() {
        return currentLocation;
    }

    public void setCurrentLocation(final Location currentLocation) {
        this.currentLocation = currentLocation;
    }

    public LatLng getCurrentCheckpoint() {
        return routeCheckpoints.get(currentCheckpointIndex);
    }

    public LatLng advanceCheckpoint() {
        if (currentCheckpointIndex >= routeCheckpoints.size() - 1) return null;
        return routeCheckpoints.get(++currentCheckpointIndex);
    }

    public LatLng getCurrentTrackEndpoint() {
        // index should always be valid because we would have finished the route otherwise
        if (currentTrackEndpointIndex >= routeTrackMappings.size()) return null;
        RouteTrackMapping currentRtm = routeTrackMappings.get(currentTrackEndpointIndex);
        int numSegments = currentRtm.routeSegments.size();
        return currentRtm.routeSegments.get(numSegments - 1).getEndLocation();
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

    public BrunoPlaylist getPlaylist() {
        return playlist;
    }

    public void setPlaylist(final BrunoPlaylist playlist) {
        this.playlist = playlist;
    }

    public List<RouteTrackMapping> getRouteTrackMappings() {
        return routeTrackMappings;
    }

    public void setRouteTrackMappings(final List<RouteTrackMapping> routeTrackMappings) {
        this.routeTrackMappings = routeTrackMappings;
    }

    public List<LatLng> getRouteCheckpoints() {
        return routeCheckpoints;
    }

    public void setRouteCheckpoints(final List<LatLng> routeCheckpoints) {
        this.routeCheckpoints = routeCheckpoints;
    }

    public void incrementStep() {
        steps++;
    }

    private void resetSteps() {
        steps = 0;
    }

    private void resetCheckpointIndex() {
        currentCheckpointIndex = 0;
    }

    private void resetTrackEndpointIndex() {
        currentTrackEndpointIndex = 0;
    }

    // MARK: - Public methods

    public int getDurationInMinutes() {
        return DURATIONS_IN_MINUTES[durationIndex];
    }

    public void reset() {
        setMode(Mode.WALK);
        setDurationIndex(0);
        setRoute(null);
        setCurrentLocation(null);
        setCurrentTrack(null);
        setPlaylist(null);
        setRouteTrackMappings(null);
        setRouteCheckpoints(null);
        resetCheckpointIndex();
        resetTrackEndpointIndex();
        resetSteps();
    }
}
