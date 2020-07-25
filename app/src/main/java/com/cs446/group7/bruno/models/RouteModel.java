package com.cs446.group7.bruno.models;

import androidx.lifecycle.ViewModel;

import com.cs446.group7.bruno.music.BrunoPlaylist;
import com.cs446.group7.bruno.music.BrunoTrack;
import com.cs446.group7.bruno.routing.Route;
import com.cs446.group7.bruno.routing.RouteTrackMapping;
import com.google.android.gms.maps.model.LatLng;

import java.util.List;

public class RouteModel extends ViewModel {

    // MARK: - Enums

    public enum Mode { WALK, RUN }

    // MARK: - Constants

    public static final int[] DURATIONS_IN_MINUTES = { 15, 30, 45, 60, 75 };

    // MARK: - Private members

    private Mode mode = Mode.WALK;
    private int durationIndex = 0;
    private Route route = null;
    private LatLng currentLocation = null;
    private BrunoTrack currentTrack = null;
    private BrunoPlaylist playlist = null;
    private List<RouteTrackMapping> routeTrackMappings = null;
    private int steps = 0;

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

    public LatLng getCurrentLocation() {
        return currentLocation;
    }

    public void setCurrentLocation(final LatLng currentLocation) {
        this.currentLocation = currentLocation;
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

    public void incrementStep() {
        steps++;
    }

    public void resetSteps() {
        steps = 0;
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
        resetSteps();
    }
}
