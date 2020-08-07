package com.cs446.group7.bruno.persistence;

import com.cs446.group7.bruno.models.RouteModel;
import com.cs446.group7.bruno.models.TrackSegment;
import com.cs446.group7.bruno.music.BrunoPlaylist;
import com.cs446.group7.bruno.music.BrunoTrack;

import java.util.Date;
import java.util.List;

/**
 * The class that holds the data for a fitness record to be persisted.
 * This is the object that gets serialized and stored into the database
 */
public class FitnessRecord {
    private final RouteModel.Mode mode;
    private final Date startTime;
    private final long userDuration;
    private final long expectedDuration;
    private final double routeDistance;
    private final int steps;
    private final BrunoPlaylist playlist;
    private final List<TrackSegment> trackSegments;

    public FitnessRecord(
            final RouteModel.Mode mode,
            final Date startTime,
            long userDuration,
            long expectedDuration,
            double routeDistance,
            int steps,
            final BrunoPlaylist playlist,
            final List<TrackSegment> trackSegments) {
        this.mode = mode;
        this.startTime = startTime;
        this.userDuration = userDuration;
        this.expectedDuration = expectedDuration;
        this.routeDistance = routeDistance;
        this.steps = steps;
        this.playlist = playlist;
        this.trackSegments = trackSegments;
    }

    public RouteModel.Mode getMode() {
        return mode;
    }

    public Date getStartTime() {
        return startTime;
    }

    public long getUserDuration() {
        return userDuration;
    }

    public long getExpectedDuration() {
        return expectedDuration;
    }

    public double getRouteDistance() {
        return routeDistance;
    }

    public int getSteps() {
        return steps;
    }

    public List<BrunoTrack> getPlaylistTracks() {
        return playlist.getTracksUpToDuration(userDuration);
    }

    public List<TrackSegment> getTrackSegments() {
        return trackSegments;
    }
}
