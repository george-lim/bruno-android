package com.cs446.group7.bruno.models;

import com.cs446.group7.bruno.location.Coordinate;
import com.cs446.group7.bruno.music.BrunoPlaylist;
import com.cs446.group7.bruno.music.BrunoTrack;
import com.cs446.group7.bruno.music.MergedBrunoPlaylistImpl;
import com.cs446.group7.bruno.routing.RouteSegment;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public class PlaylistModel {

    // MARK: - Private members

    private List<RouteSegment> routeSegments;
    private int[] routeColours;
    private BrunoPlaylist playlist;

    private List<TrackSegment> trackSegments;
    private int trackIndex;
    private long trackStartTime;

    // MARK: - Lifecycle methods

    public PlaylistModel() {
        reset();
    }

    // MARK: - Private methods

    // Group route segments into track segments
    private List<TrackSegment> processSegments() {
        if (routeSegments == null || routeColours == null || playlist == null) {
            return null;
        }

        List<TrackSegment> result = new ArrayList<>();
        int routeColourIndex = 0;
        int trackIndex = 0;
        // Duration is measured in milliseconds
        long accumulatedRouteSegmentDuration = 0;
        List<RouteSegment> accumulatedRouteSegments = new LinkedList<>();
        LinkedList<RouteSegment> routeSegmentsCopy = new LinkedList<>(routeSegments);
        while (routeSegmentsCopy.size() > 0) {
            BrunoTrack track = playlist.getTrack(trackIndex);

            RouteSegment currentRouteSegment = routeSegmentsCopy.poll();
            Coordinate routeSegmentStart = currentRouteSegment.getStartCoordinate();
            Coordinate routeSegmentEnd = currentRouteSegment.getEndCoordinate();
            long routeSegmentDuration = currentRouteSegment.getDuration();
            long lastSongSegment = accumulatedRouteSegmentDuration + routeSegmentDuration;

            if (lastSongSegment > track.getDuration()) {
                // Represents the duration of each half of a segment since it needs to be split
                long segmentDurationFirstHalf = track.getDuration() - accumulatedRouteSegmentDuration;
                long segmentDurationSecondHalf = routeSegmentDuration - segmentDurationFirstHalf;
                double segmentDurationRatio = (double) segmentDurationFirstHalf / routeSegmentDuration;

                Coordinate segmentSplitPoint = routeSegmentStart.getSplitPoint(
                        routeSegmentEnd,
                        segmentDurationRatio
                );

                RouteSegment segmentFirstHalf = new RouteSegment(routeSegmentStart,
                        segmentSplitPoint, segmentDurationFirstHalf);
                RouteSegment segmentSecondHalf = new RouteSegment(segmentSplitPoint,
                        routeSegmentEnd, segmentDurationSecondHalf);

                // Create mapping of accumulated segments and first half segment with current track
                accumulatedRouteSegments.add(segmentFirstHalf);

                TrackSegment trackSegment = new TrackSegment(
                        accumulatedRouteSegments,
                        routeColours[routeColourIndex]
                );

                routeColourIndex = (routeColourIndex + 1) % routeColours.length;
                result.add(trackSegment);
                accumulatedRouteSegments = new LinkedList<>();

                // Accommodate the second half of route segment for the next track
                routeSegmentsCopy.push(segmentSecondHalf);
                accumulatedRouteSegmentDuration = 0;
                trackIndex++;
            } else if (lastSongSegment == track.getDuration()) {
                accumulatedRouteSegments.add(currentRouteSegment);

                TrackSegment trackSegment = new TrackSegment(
                        accumulatedRouteSegments,
                        routeColours[routeColourIndex]
                );

                routeColourIndex = (routeColourIndex + 1) % routeColours.length;
                result.add(trackSegment);
                accumulatedRouteSegments = new LinkedList<>();
                accumulatedRouteSegmentDuration = 0;
                trackIndex++;
            } else {
                accumulatedRouteSegments.add(currentRouteSegment);
                accumulatedRouteSegmentDuration += routeSegmentDuration;
            }
        }

        if (accumulatedRouteSegments.size() > 0) {
            TrackSegment trackSegment = new TrackSegment(
                    accumulatedRouteSegments,
                    routeColours[routeColourIndex]
            );

            result.add(trackSegment);
        }

        return result;
    }

    private boolean hasStartedPlaylistRoute() {
        return trackIndex >= 0;
    }

    private boolean hasCompletedPlaylistRoute() {
        return trackIndex >= trackSegments.size();
    }

    private long getPlaybackPosition() {
        if (!hasStartedPlaylistRoute()) {
            return 0;
        }

        // Force playbackPosition to be capped at the current track duration
        return Math.min(new Date().getTime() - trackStartTime, getCurrentTrack().getDuration());
    }

    // Returns the total distance of TrackSegments that have been completed (excluding current)
    private double getCompletedTrackSegmentsDistance() {
        double distance = 0;

        for (int i = 0; i < Math.min(trackIndex, trackSegments.size()); ++i) {
            distance += trackSegments.get(i).getDistance();
        }

        return distance;
    }

    // Returns the travelled distance of current TrackSegment
    private double getCurrentTrackSegmentDistance() {
        if (!hasStartedPlaylistRoute()) {
            return 0;
        }

        double currentTrackPlaybackRatio = (double)getPlaybackPosition()
                / getCurrentTrack().getDuration();
        double currentTrackDistance = trackSegments.get(trackIndex).getDistance();
        return currentTrackPlaybackRatio * currentTrackDistance;
    }

    // Returns the total duration of TrackSegments that have been completed (excluding current)
    private long getCompletedTrackSegmentsDuration() {
        long duration = 0;

        for (int i = 0; i < Math.min(trackIndex, trackSegments.size()); ++i) {
            duration += playlist.getTrack(i).getDuration();
        }

        return duration;
    }

    // MARK: - Public methods

    public void setRouteSegments(final List<RouteSegment> routeSegments) {
        this.routeSegments = routeSegments;
        trackSegments = processSegments();
    }

    public void setRouteColours(final int[] routeColours) {
        this.routeColours = routeColours;
        trackSegments = processSegments();
    }

    public BrunoPlaylist getPlaylist() {
        return playlist;
    }

    public void setPlaylist(final BrunoPlaylist playlist) {
        this.playlist = playlist;
        trackSegments = processSegments();
    }

    public void mergePlaylist(final BrunoPlaylist playlist) {
        setPlaylist(new MergedBrunoPlaylistImpl(
                this.playlist,
                playlist,
                trackIndex,
                getPlaybackPosition()
        ));
    }

    public List<TrackSegment> getTrackSegments() {
        return trackSegments;
    }

    public BrunoTrack getCurrentTrack() {
        if (!hasStartedPlaylistRoute()) {
            return null;
        }

        return playlist.getTrack(trackIndex);
    }

    // MARK: - Current playlist playback calculations

    // Returns route distance of current playlist playback
    public double getPlaylistRouteDistance() {
        if (hasCompletedPlaylistRoute()) {
            return getCompletedTrackSegmentsDistance();
        }

        return getCompletedTrackSegmentsDistance()
                + getCurrentTrackSegmentDistance();
    }

    // Returns route duration of current playlist playback
    public long getPlaylistRouteDuration() {
        if (hasCompletedPlaylistRoute()) {
            return getCompletedTrackSegmentsDuration();
        }

        return getCompletedTrackSegmentsDuration() + getPlaybackPosition();
    }

    // Returns route coordinate of current playlist playback
    public Coordinate getPlaylistRouteCoordinate() {
        if (!hasStartedPlaylistRoute()) {
            return routeSegments.get(0).getStartCoordinate();
        }
        else if (hasCompletedPlaylistRoute()) {
            return routeSegments.get(routeSegments.size() - 1).getEndCoordinate();
        }
        else {
            return trackSegments.get(trackIndex).getCoordinate(getPlaybackPosition());
        }
    }

    // MARK: - Music player synchronization methods

    public void onTrackChanged(final BrunoTrack track) {
        // Stay on the same song until the next song matches what we expect
        if (hasStartedPlaylistRoute() && !hasCompletedPlaylistRoute()
                && track == playlist.getTrack(trackIndex + 1)) {
            trackIndex++;
            trackStartTime = new Date().getTime();
        }
    }

    // MARK: - Reset methods

    public void resetPlayback() {
        // NOTE: Must be -1 because setting first song also calls onTrackChanged.
        trackIndex = -1;
        trackStartTime = 0;
    }

    public void reset() {
        routeSegments = null;
        routeColours = null;
        playlist = null;
        trackSegments = null;
        resetPlayback();
    }
}
