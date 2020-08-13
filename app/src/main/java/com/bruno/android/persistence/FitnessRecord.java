package com.bruno.android.persistence;

import android.util.Base64;

import com.bruno.android.models.RouteModel;
import com.bruno.android.models.TrackSegment;
import com.bruno.android.music.BrunoPlaylist;
import com.bruno.android.music.BrunoTrack;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * The class that holds the data for a fitness record to be persisted.
 * This is the object that gets serialized and stored into the database
 */
public class FitnessRecord implements Serializable {
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

    public static FitnessRecord deserialize(final String serializedString) throws IOException, ClassNotFoundException {
        byte [] data = Base64.decode(serializedString, Base64.DEFAULT);
        ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(data));
        Object result = ois.readObject();
        ois.close();
        return (FitnessRecord) result;
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

    public String serialize() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream( baos );
        oos.writeObject(this);
        oos.close();
        return Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT);
    }
}
