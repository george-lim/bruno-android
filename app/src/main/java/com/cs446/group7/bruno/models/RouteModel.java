package com.cs446.group7.bruno.models;

import android.location.Location;
import android.util.Log;

import com.cs446.group7.bruno.MainActivity;
import com.cs446.group7.bruno.location.Coordinate;
import com.cs446.group7.bruno.music.BrunoPlaylist;
import com.cs446.group7.bruno.music.BrunoTrack;
import com.cs446.group7.bruno.persistence.FitnessRecordDAO;
import com.cs446.group7.bruno.persistence.FitnessRecordData;
import com.cs446.group7.bruno.persistence.FitnessRecordEntry;
import com.cs446.group7.bruno.routing.RouteSegment;

import java.io.IOException;
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

    public void mergePlaylist(final BrunoPlaylist playlist, long playbackPosition) {
        playlistModel.mergePlaylist(playlist, playbackPosition);
    }

    public List<TrackSegment> getTrackSegments() {
        return playlistModel.getTrackSegments();
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

    public BrunoTrack getCurrentTrack() {
        return playlistModel.getCurrentTrack();
    }

    public void setCurrentTrack(final BrunoTrack currentTrack) {
        playlistModel.setCurrentTrack(currentTrack);
    }

    public void incrementStep() {
        steps++;
    }

    public void setUserStartTime() {
        userStartTime = new Date();
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
        return checkpointsModel.getUserRouteDistance(currentCoordinate)
                - playlistModel.getPlaylistRouteDistance(playbackPosition);
    }

    /**
     * Persists the fitness record data to DB
     */
    public void saveRecordToDatabase() {
        final FitnessRecordData fitnessRecordData = new FitnessRecordData(
                mode == RouteModel.Mode.RUN ? FitnessRecordData.Mode.RUN : FitnessRecordData.Mode.WALK,
                userStartTime,
                getUserDuration(),
                playlistModel.getPlaylist().getDuration(),
                playlistModel.getTotalRouteDistance(),
                steps,
                getPlaylist().getTracks(),
                getTrackSegments()
        );

        try { // Serialize and store record in DB
            final String serializedString = fitnessRecordData.serialize();
            final FitnessRecordDAO fitnessRecordDAO = MainActivity.getPersistenceService().getFitnessRecordDAO();
            final FitnessRecordEntry newRecord = new FitnessRecordEntry();
            newRecord.setRecordDataString(serializedString);
            fitnessRecordDAO.insert(newRecord);
        } catch (IOException e) {
            Log.e(getClass().getSimpleName(), "Failed to store FitnessRecord in DB: " + e.toString());
        }
    }

    public Coordinate getPlaylistRouteCoordinate(long playbackPosition) {
        return playlistModel.getPlaylistRouteCoordinate(playbackPosition);
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
}
