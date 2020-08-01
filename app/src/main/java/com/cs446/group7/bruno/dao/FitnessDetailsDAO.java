package com.cs446.group7.bruno.dao;

import com.cs446.group7.bruno.colourizedroute.ColourizedRoute;
import com.cs446.group7.bruno.music.BrunoTrack;

import java.util.Date;
import java.util.List;

/**
 * The class that holds the DAO data for a run.
 */
public class FitnessDetailsDAO {

    public FitnessDetailsDAO(
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

    public enum Mode { WALK, RUN }

    private final Mode mode;
    private final Date startTime;
    private final long userDuration;
    private final long expectedDuration;
    private final double routeDistance;
    private final int steps;
    private final List<BrunoTrack> tracks;
    private final ColourizedRoute colourizedRoute;

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
}
