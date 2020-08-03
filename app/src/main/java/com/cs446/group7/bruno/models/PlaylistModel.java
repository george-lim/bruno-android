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

                // Represent the difference in lat and long distances, needed for slope calculations
                double diffLat = routeSegmentEnd.getLatitude() - routeSegmentStart.getLatitude();
                double diffLng = routeSegmentEnd.getLongitude() - routeSegmentStart.getLongitude();

                double midPointLat = routeSegmentStart.getLatitude() + (diffLat * segmentDurationRatio);
                double midPointLng = routeSegmentStart.getLongitude() + (diffLng * segmentDurationRatio);

                // Create two new segments based on start, midpoint, end to represent split
                Coordinate segmentMidPoint = new Coordinate(midPointLat, midPointLng);
                RouteSegment segmentFirstHalf = new RouteSegment(routeSegmentStart,
                        segmentMidPoint, segmentDurationFirstHalf);
                RouteSegment segmentSecondHalf = new RouteSegment(segmentMidPoint,
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

    // Returns the total distance of TrackSegments that have been completed (excluding current)
    private double getCompletedTrackSegmentsDistance() {
        List<TrackSegment> trackSegments = getTrackSegments();
        double distance = 0;

        for (int i = 0; i < Math.min(trackIndex, trackSegments.size()); ++i) {
            distance += trackSegments.get(i).getDistance();
        }

        return distance;
    }

    // Returns the travelled distance of current TrackSegment
    private double getCurrentTrackSegmentDistance(long playbackPosition) {
        double currentTrackPlaybackRatio = (double)playbackPosition
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

    public void mergePlaylist(final BrunoPlaylist playlist, long playbackPosition) {
        this.playlist = new MergedBrunoPlaylistImpl(
                this.playlist,
                playlist,
                trackIndex,
                playbackPosition
        );

        trackSegments = processSegments();
    }

    public List<TrackSegment> getTrackSegments() {
        return trackSegments;
    }

    public BrunoTrack getCurrentTrack() {
        if (trackIndex < 0) {
            return null;
        }

        return playlist.getTrack(trackIndex);
    }

    // TODO: Handle current track desync from Spotify.
    public void setCurrentTrack(final BrunoTrack currentTrack) {
        trackIndex++;
    }

    // Returns distance travelled by the playlist on the route
    public double getPlaylistRouteDistance(long playbackPosition) {
        if (trackIndex >= trackSegments.size()) {
            return getCompletedTrackSegmentsDistance();
        }

        return getCompletedTrackSegmentsDistance()
                + getCurrentTrackSegmentDistance(playbackPosition);
    }

    public long getPlaylistRouteDuration(long playbackPosition) {
        if (trackIndex >= trackSegments.size()) {
            return getCompletedTrackSegmentsDuration();
        }

        return getCompletedTrackSegmentsDuration() + playbackPosition;
    }

    // Returns the location on the route corresponding to the current track's playback position
    public Coordinate getPlaylistRouteCoordinate(long playbackPosition) {
        // Bruno has finished and is stationary at the end location of the route
        if (trackIndex >= trackSegments.size()) {
            return routeSegments.get(routeSegments.size() - 1).getEndCoordinate();
        }

        final List<Coordinate> currentTrackSegmentCoordinates =
                trackSegments.get(trackIndex).getCoordinates();

        double distance = 0;
        Coordinate playlistRouteCoordinate = null;

        for (int i = 0; i < currentTrackSegmentCoordinates.size() - 1; ++i) {
            final Coordinate routeSegmentStart = currentTrackSegmentCoordinates.get(i);
            final Coordinate routeSegmentEnd = currentTrackSegmentCoordinates.get(i + 1);

            double routeSegmentDistance = routeSegmentStart.getDistance(routeSegmentEnd);

            if (distance + routeSegmentDistance >= getCurrentTrackSegmentDistance(playbackPosition)) {
                double diffLat = routeSegmentEnd.getLatitude() - routeSegmentStart.getLatitude();
                double diffLng = routeSegmentEnd.getLongitude() - routeSegmentStart.getLongitude();

                double currentTrackPlaybackRatio = (double)playbackPosition
                        / getCurrentTrack().getDuration();
                double playlistCoordinateLat =
                        routeSegmentStart.getLatitude() + (diffLat * currentTrackPlaybackRatio);
                double playlistCoordinateLng =
                        routeSegmentStart.getLongitude() + (diffLng * currentTrackPlaybackRatio);

                playlistRouteCoordinate = new Coordinate(playlistCoordinateLat, playlistCoordinateLng);
                break;
            }

            distance += routeSegmentDistance;
        }

        return playlistRouteCoordinate;
    }

    public void resetPlayback() {
        setCurrentTrack(null);
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
