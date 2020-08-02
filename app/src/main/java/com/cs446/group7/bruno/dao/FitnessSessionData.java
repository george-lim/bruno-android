package com.cs446.group7.bruno.dao;

import android.util.Base64;

import com.cs446.group7.bruno.colourizedroute.ColourizedRoute;
import com.cs446.group7.bruno.music.BrunoTrack;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * The class that holds the data for a run.
 */
public class FitnessSessionData implements Serializable {

    public enum Mode { WALK, RUN }

    public FitnessSessionData(
            final Mode mode,
            final Date startTime,
            long userDuration,
            long expectedDuration,
            double routeDistance,
            int steps,
            final List<BrunoTrack> tracks,
            final ColourizedRoute colourizedRoute) {
        this.mode = mode;
        this.startTime = startTime;
        this.userDuration = userDuration;
        this.expectedDuration = expectedDuration;
        this.routeDistance = routeDistance;
        this.steps = steps;
        this.tracks = tracks;
        this.colourizedRoute = colourizedRoute;
    }

    private final Mode mode;
    private final Date startTime;
    private final long userDuration;
    private final long expectedDuration;
    private final double routeDistance;
    private final int steps;
    private final List<BrunoTrack> tracks;
    private final ColourizedRoute colourizedRoute;

    public static FitnessSessionData deserialize(final String serializedString) throws IOException, ClassNotFoundException {
        byte [] data = Base64.decode(serializedString, Base64.DEFAULT);
        ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(data));
        Object result = ois.readObject();
        ois.close();
        return (FitnessSessionData) result;
    }

    public boolean isWalk() { return mode == Mode.WALK; }

    public boolean isRun() { return mode == Mode.RUN; }

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

    public List<BrunoTrack> getTracks() {
        return tracks;
    }

    public ColourizedRoute getColourizedRoute() {
        return colourizedRoute;
    }

    public String serialize() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream( baos );
        oos.writeObject(this);
        oos.close();
        return Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT);
    }
}
