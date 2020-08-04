package com.cs446.group7.bruno.models;

import com.cs446.group7.bruno.location.Coordinate;
import com.cs446.group7.bruno.music.BrunoPlaylist;
import com.cs446.group7.bruno.music.BrunoTrack;
import com.cs446.group7.bruno.music.MergedBrunoPlaylistImpl;
import com.cs446.group7.bruno.routing.RouteSegment;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class PlaylistModel {

    // MARK: - Private members

    private List<RouteSegment> routeSegments;
    private int[] routeColours;
    private BrunoPlaylist playlist;

    private List<TrackSegment> trackSegments;
    private int trackIndex;

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

    private boolean hasCompletedPlaylistRoute(long playbackPosition) {
        if (!hasStartedPlaylistRoute()) {
            return false;
        }
        else if (trackIndex + 1 == trackSegments.size()
                && playbackPosition >= trackSegments.get(trackIndex).getDuration()) {
            return true;
        }
        else {
            return trackIndex >= trackSegments.size();
        }
    }

    // After merge, playbackPosition can still be desynchronized until track change
    private long getSafePlaybackPosition(long playbackPosition) {
        if (!hasStartedPlaylistRoute()) {
            return 0;
        }

        return Math.min(playbackPosition, playlist.getTrack(trackIndex).getDuration());
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
    private double getCurrentTrackSegmentDistance(long playbackPosition) {
        if (!hasStartedPlaylistRoute()) {
            return 0;
        }

        double currentTrackPlaybackRatio = (double)playbackPosition
                / getCurrentTrack().getDuration();
        double currentTrackDistance = trackSegments.get(trackIndex).getDistance();
        return currentTrackPlaybackRatio * currentTrackDistance;
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

    public void mergePlaylist(final BrunoPlaylist playlist, long playbackPosition) {
        long safePlaybackPosition = getSafePlaybackPosition(playbackPosition);

        // No need to merge if this is happening at the start of the playlist
        if (!hasStartedPlaylistRoute() || trackIndex == 0 && safePlaybackPosition == 0) {
            setPlaylist(playlist);
            return;
        }

        // If safePlaybackPosition cannot be determined or is zero, merge from the end of previous song
        if (safePlaybackPosition == 0) {
            trackIndex--;
            safePlaybackPosition = playlist.getTrack(trackIndex).getDuration();
        }

        setPlaylist(new MergedBrunoPlaylistImpl(
                this.playlist,
                playlist,
                trackIndex,
                getSafePlaybackPosition(safePlaybackPosition)
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
    public double getPlaylistRouteDistance(long playbackPosition) {
        long safePlaybackPosition = getSafePlaybackPosition(playbackPosition);

        if (hasCompletedPlaylistRoute(safePlaybackPosition)) {
            return getCompletedTrackSegmentsDistance();
        }

        return getCompletedTrackSegmentsDistance()
                + getCurrentTrackSegmentDistance(safePlaybackPosition);
    }

    // Returns route coordinate of current playlist playback
    public Coordinate getPlaylistRouteCoordinate(long playbackPosition) {
        long safePlaybackPosition = getSafePlaybackPosition(playbackPosition);

        if (!hasStartedPlaylistRoute()) {
            return routeSegments.get(0).getStartCoordinate();
        }
        else if (hasCompletedPlaylistRoute(safePlaybackPosition)) {
            return routeSegments.get(routeSegments.size() - 1).getEndCoordinate();
        }
        else {
            return trackSegments.get(trackIndex).getCoordinate(safePlaybackPosition);
        }
    }

    // MARK: - Total playlist playback calculations

    public double getTotalPlaylistRouteDistance() {
        double distance = 0;

        for (TrackSegment trackSegment : trackSegments) {
            distance += trackSegment.getDistance();
        }

        return distance;
    }

    public long getTotalPlaylistRouteDuration() {
        long duration = 0;

        for (TrackSegment trackSegment : trackSegments) {
            duration += trackSegment.getDuration();
        }

        return duration;
    }

    // MARK: - Music player synchronization methods

    public void onTrackChanged(final BrunoTrack track) {
        // Stay on the same song until the next song matches what we expect
        if (track.equals(playlist.getTrack(trackIndex + 1))) {
            trackIndex++;
        }
    }

    // MARK: - Reset methods

    public void resetPlayback() {
        // NOTE: Must be -1 because setting first song also calls onTrackChanged.
        trackIndex = -1;
    }

    public void reset() {
        routeSegments = null;
        routeColours = null;
        playlist = null;
        trackSegments = null;
        resetPlayback();
    }
}
