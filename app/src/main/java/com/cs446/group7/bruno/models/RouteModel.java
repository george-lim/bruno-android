package com.cs446.group7.bruno.models;

import android.location.Location;
import android.util.Log;

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
    private Location currentLocation = null;
    private int steps = 0;
    private Date userStartTime = null;
    private Date userStopTime = null;

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
        playlistModel.setRouteSegments(routeSegments);
        checkpointsModel.setRouteSegments(routeSegments);
    }

    public void setRouteColours(final int[] routeColours) {
        playlistModel.setRouteColours(routeColours);
    }

    public BrunoPlaylist getPlaylist() {
        return playlistModel.getPlaylist();
    }

    public void setPlaylist(final BrunoPlaylist playlist) {
        playlistModel.setPlaylist(playlist);
    }

    public List<TrackSegment> getTrackSegments() {
        return playlistModel.getTrackSegments();
    }

    public Location getCurrentLocation() {
        return currentLocation;
    }

    public void setCurrentLocation(final Location currentLocation) {
        this.currentLocation = currentLocation;
    }

<<<<<<< HEAD
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

    public List<BrunoTrack> getAllTracks() {
        return playlist.getTracks();
    }

=======
>>>>>>> master
    public BrunoTrack getCurrentTrack() {
        return playlistModel.getCurrentTrack();
    }

    public void setCurrentTrack(final BrunoTrack currentTrack) {
        playlistModel.setCurrentTrack(currentTrack);
    }

    public void incrementStep() {
        steps++;
    }

    public int getSteps() {
        return steps;
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

    public long getUserDuration() {
        if (userStartTime == null || userStopTime == null) {
            Log.w(getClass().getSimpleName(), "getUserDuration called but start/end time was not set first");
            return -1;
        }

        return userStopTime.getTime() - userStartTime.getTime(); // In Milliseconds
    }

    // Returns difference in distance between the user and the playlist on the route
    public double getDistanceBetweenUserAndPlaylist(long playbackPosition) {
        return checkpointsModel.getUserRouteDistance(currentLocation)
                - playlistModel.getPlaylistRouteDistance(playbackPosition);
    }

    // MARK: - CheckpointsModel methods

    public LatLng getCheckpoint() {
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

        return checkpointsModel.getDistanceToCheckpoint(currentLocation);
    }

    public boolean hasCompletedAllCheckpoints() {
        return checkpointsModel.hasCompletedAllCheckpoints();
    }

    // MARK: - PlaylistModel methods

    public boolean hasTrackSegments() {
        return playlistModel.getTrackSegments() != null;
    }

    /**
     * Resets the progress of the current route, and stats, but keeps the route and checkpoints.
     */
    public void softReset() {
        userStartTime = null;
        userStopTime = null;
        steps = 0;
        playlistModel.resetCurrentTrack();
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
        playlistModel.reset();
        checkpointsModel.reset();
    }
}
