package com.cs446.group7.bruno.models;

import android.location.Location;

import com.cs446.group7.bruno.location.Coordinate;
import com.cs446.group7.bruno.music.BrunoPlaylist;
import com.cs446.group7.bruno.music.BrunoTrack;
import com.cs446.group7.bruno.routing.RouteSegment;

import java.util.Date;
import java.util.List;

import androidx.lifecycle.ViewModel;

public class RouteModel extends ViewModel {

    // MARK: - Enums

    public enum Mode { WALK, RUN }

    // MARK: - Constants

    public static final int[] DURATIONS_IN_MINUTES = { 15, 30, 45, 60, 75, 90, 105, 120 };

    // MARK: - Private members

    private Mode mode = Mode.WALK;
    private int durationIndex = 0;
    private Location currentLocation = null;
    private Coordinate currentCoordinate = null;
    private int steps = 0;
    private Date startDate = null;

    private PlaylistModel playlistModel = new PlaylistModel();
    private CheckpointsModel checkpointsModel = new CheckpointsModel();

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
        if (mode == Mode.RUN) {
            convertToRunningDurations(routeSegments);
        }

        playlistModel.setRouteSegments(routeSegments);
        checkpointsModel.setRouteSegments(routeSegments);
    }

    public Location getCurrentLocation() {
        return currentLocation;
    }

    public Coordinate getCurrentCoordinate() {
        return currentCoordinate;
    }

    public void setCurrentLocation(final Location currentLocation) {
        this.currentLocation = currentLocation;
        this.currentCoordinate = new Coordinate(currentLocation);
    }

    public void incrementStep() {
        steps++;
    }

    public void startRouteNavigation() {
        startDate = new Date();
    }

    public void stopRouteNavigation() {
        double userDistance = checkpointsModel.getUserRouteDistance(currentCoordinate);
        long userDuration = new Date().getTime() - startDate.getTime();
        double brunoDistance = playlistModel.getTotalPlaylistRouteDistance();
        long brunoDuration = playlistModel.getTotalPlaylistRouteDuration();
        BrunoPlaylist playlist = playlistModel.getPlaylist();
        List<TrackSegment> trackSegments = playlistModel.getTrackSegments();
        // TODO: Persist these to database.
    }

    // Returns difference in distance between the user and the playlist on the route
    public double getDistanceBetweenUserAndPlaylist(long playbackPosition) {
        return checkpointsModel.getUserRouteDistance(currentCoordinate)
                - playlistModel.getPlaylistRouteDistance(playbackPosition);
    }

    // MARK: - CheckpointsModel methods

    public Coordinate getCheckpoint() {
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

    public double getDistanceToCheckpoint() {
        // Fail-safe
        if (hasCompletedAllCheckpoints()) {
            return 0;
        }

        return checkpointsModel.getDistanceToCheckpoint(currentCoordinate);
    }

    public boolean hasCompletedAllCheckpoints() {
        return checkpointsModel.hasCompletedAllCheckpoints();
    }

    // MARK: - PlaylistModel methods

    public void setRouteColours(final int[] routeColours) {
        playlistModel.setRouteColours(routeColours);
    }

    public BrunoPlaylist getPlaylist() {
        return playlistModel.getPlaylist();
    }

    public void setPlaylist(final BrunoPlaylist playlist) {
        playlistModel.setPlaylist(playlist);
    }

    public boolean hasTrackSegments() {
        return getTrackSegments() != null;
    }

    public List<TrackSegment> getTrackSegments() {
        return playlistModel.getTrackSegments();
    }

    public BrunoTrack getCurrentTrack() {
        return playlistModel.getCurrentTrack();
    }

    public void mergePlaylist(final BrunoPlaylist playlist, long playbackPosition) {
        playlistModel.mergePlaylist(playlist, playbackPosition);
    }

    public Coordinate getPlaylistRouteCoordinate(long playbackPosition) {
        return playlistModel.getPlaylistRouteCoordinate(playbackPosition);
    }

    public void onTrackChanged(final BrunoTrack currentTrack) {
        playlistModel.onTrackChanged(currentTrack);
    }

    /**
     * Resets the progress of the current route, and stats, but keeps the route and checkpoints.
     */
    public void softReset() {
        startDate = null;
        steps = 0;
        playlistModel.resetPlayback();
        checkpointsModel.resetCheckpoint();
    }

    /**
     * Resets everything to the state it was first constructed.
     */
    public void hardReset() {
        softReset();
        mode = Mode.WALK;
        durationIndex = 0;
        currentLocation = null;
        currentCoordinate = null;
        playlistModel.reset();
        checkpointsModel.reset();
    }

    // MARK: - private methods

    // Sets durations of each RouteSegment to correspond with running speed
    private void convertToRunningDurations(final List<RouteSegment> routeSegments) {
        for (RouteSegment routeSegment : routeSegments) {
            routeSegment.setRunningDuration();
        }
    }
}
