package com.cs446.group7.bruno.models;

import com.cs446.group7.bruno.music.BrunoPlaylist;
import com.cs446.group7.bruno.music.BrunoTrack;
import com.cs446.group7.bruno.routing.RouteSegment;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class PlaylistModel {

    // MARK: - Private members

    private List<RouteSegment> routeSegments;
    private int[] routeColours;
    private BrunoPlaylist playlist;
    private List<TrackSegment> trackSegments;
    private BrunoTrack currentTrack;

    // MARK: - Lifecycle methods

    public PlaylistModel() {
        reset();
    }

    // MARK: - Private methods

    /**
     * Group route segments into track segments
     * NOTE: Total playlist duration is expected to be longer than the total route segment duration.
     */
    private List<TrackSegment> processSegments() {
        if (routeSegments == null || routeColours == null || playlist == null) {
            return null;
        }

        List<TrackSegment> result = new ArrayList<>();
        int routeColourIndex = 0;
        int currTrackInd = 0;
        // Duration is measured in milliseconds
        long accumulatedRouteSegmentDuration = 0;
        List<RouteSegment> accumulatedRouteSegments = new LinkedList<>();
        LinkedList<RouteSegment> routeSegmentsCopy = new LinkedList<>(routeSegments);
        List<BrunoTrack> tracks = playlist.getTracks();
        while (routeSegmentsCopy.size() > 0) {
            BrunoTrack currTrack = tracks.get(currTrackInd);

            RouteSegment currentRouteSegment = routeSegmentsCopy.poll();
            LatLng routeSegmentStart = currentRouteSegment.getStartLocation();
            LatLng routeSegmentEnd = currentRouteSegment.getEndLocation();
            long routeSegmentDuration = currentRouteSegment.getDuration();
            long lastSongSegment = accumulatedRouteSegmentDuration + routeSegmentDuration;

            if (lastSongSegment > currTrack.getDuration()) {
                // Represents the duration of each half of a segment since it needs to be split
                long segmentDurationFirstHalf = currTrack.getDuration() - accumulatedRouteSegmentDuration;
                long segmentDurationSecondHalf = routeSegmentDuration - segmentDurationFirstHalf;
                double segmentDurationRatio = (double) segmentDurationFirstHalf / routeSegmentDuration;

                // Represent the difference in lat and long distances, needed for slope calculations
                double diffLat = routeSegmentEnd.latitude - routeSegmentStart.latitude;
                double diffLng = routeSegmentEnd.longitude - routeSegmentStart.longitude;

                double midPointLat = routeSegmentStart.latitude + (diffLat * segmentDurationRatio);
                double midPointLng = routeSegmentStart.longitude + (diffLng * segmentDurationRatio);

                // Create two new segments based on start, midpoint, end to represent split
                LatLng segmentMidPoint = new LatLng(midPointLat, midPointLng);
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
                currTrackInd++;
            } else if (lastSongSegment == currTrack.getDuration()) {
                accumulatedRouteSegments.add(currentRouteSegment);

                TrackSegment trackSegment = new TrackSegment(
                        accumulatedRouteSegments,
                        routeColours[routeColourIndex]
                );

                routeColourIndex = (routeColourIndex + 1) % routeColours.length;
                result.add(trackSegment);
                accumulatedRouteSegments = new LinkedList<>();
                accumulatedRouteSegmentDuration = 0;
                currTrackInd++;
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

    public List<TrackSegment> getTrackSegments() {
        return trackSegments;
    }

    public BrunoTrack getCurrentTrack() {
        return currentTrack;
    }

    public void setCurrentTrack(final BrunoTrack currentTrack) {
        this.currentTrack = currentTrack;
    }

    // Returns distance travelled by the playlist on the route
    public double getPlaylistRouteDistance(long playbackPosition) {
        List<BrunoTrack> tracks = playlist.getTracks();
        List<TrackSegment> trackSegments = getTrackSegments();
        int i = 0;
        double distance = 0;

        while (tracks.get(i) != currentTrack) {
            distance += trackSegments.get(i).getDistance();
            i++;
        }

        double currentTrackPlaybackRatio = (double)playbackPosition / currentTrack.getDuration();
        distance += currentTrackPlaybackRatio * trackSegments.get(i).getDistance();

        return distance;
    }

    public void reset() {
        routeSegments = null;
        routeColours = null;
        playlist = null;
        trackSegments = null;
        currentTrack = null;
    }
}
