package com.bruno.android.models;

import android.location.Location;
import android.util.Log;

import androidx.lifecycle.ViewModel;

import com.bruno.android.MainActivity;
import com.bruno.android.location.Coordinate;
import com.bruno.android.music.BrunoPlaylist;
import com.bruno.android.music.BrunoTrack;
import com.bruno.android.persistence.FitnessRecord;
import com.bruno.android.persistence.FitnessRecordDAO;
import com.bruno.android.persistence.FitnessRecordEntry;
import com.bruno.android.routing.RouteSegment;

import java.io.IOException;
import java.util.Date;
import java.util.List;

public class RouteModel extends ViewModel {

    // MARK: - Enums

    public enum Mode { WALK, RUN }
    public enum Stage { PLANNING, NAVIGATION }

    // MARK: - Constants

    public static final int[] DURATIONS_IN_MINUTES = { 15, 30, 45, 60, 75, 90, 105, 120 };

    // MARK: - Private members

    private Stage stage = Stage.PLANNING;
    private Mode mode = Mode.WALK;
    private int durationIndex = 0;
    private Location currentLocation = null;
    private Coordinate currentCoordinate = null;
    private int steps = 0;
    private Date startDate = null;

    private PlaylistModel playlistModel = new PlaylistModel();
    private CheckpointsModel checkpointsModel = new CheckpointsModel();

    // MARK: - private methods

    private void softReset() {
        stage = Stage.PLANNING;
        startDate = null;
        steps = 0;
        playlistModel.resetPlayback();
        checkpointsModel.resetCheckpoint();
    }

    private void hardReset() {
        softReset();
        mode = Mode.WALK;
        durationIndex = 0;
        currentLocation = null;
        currentCoordinate = null;
        playlistModel.reset();
        checkpointsModel.reset();
    }

    // Sets durations of each RouteSegment to correspond with running speed
    private void convertToRunningDurations(final List<RouteSegment> routeSegments) {
        if (routeSegments == null) {
            return;
        }

        for (RouteSegment routeSegment : routeSegments) {
            routeSegment.setRunningDuration();
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

        if (stage == Stage.NAVIGATION) {
            checkpointsModel.updateCurrentCheckpoint(currentLocation);
        }
    }

    public void incrementStep() {
        steps++;
    }

    public void startRouteNavigation() {
        startDate = new Date();
        stage = Stage.NAVIGATION;
    }

    public void stopRouteNavigation() {
        softReset();
    }

    public void completeRouteNavigation() {

        final long userDuration = new Date().getTime() - startDate.getTime();
        final double brunoDistance = playlistModel.getTotalPlaylistRouteDistance();
        final long brunoDuration = playlistModel.getTotalPlaylistRouteDuration();

        // Persist tracks to database.
        final FitnessRecord fitnessRecord = new FitnessRecord(
                mode,
                startDate,
                userDuration,
                brunoDuration,
                brunoDistance,
                steps,
                getPlaylist(),
                getTrackSegments()
        );

        try { // Serialize and store record in DB
            final String serializedString = fitnessRecord.serialize();
            final FitnessRecordDAO fitnessRecordDAO = MainActivity.getPersistenceService().getFitnessRecordDAO();
            final FitnessRecordEntry fitnessRecordEntry = new FitnessRecordEntry();
            fitnessRecordEntry.setRecordDataString(serializedString);
            fitnessRecordDAO.insert(fitnessRecordEntry);
        } catch (IOException e) {
            Log.e(getClass().getSimpleName(), "Failed to store FitnessRecord in DB: " + e.toString());
        }
    }

    public void reset() {
        hardReset();
    }

    // Returns difference in distance between the user and the playlist on the route
    public double getDistanceBetweenUserAndPlaylist(long playbackPosition) {
        if (hasCompletedAllCheckpoints()) {
            return 0;
        }

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

    public double getCheckpointRadius() {
        return checkpointsModel.getCheckpointRadius();
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
}
