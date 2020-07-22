package com.cs446.group7.bruno.routing;

import com.cs446.group7.bruno.music.BrunoPlaylist;
import com.cs446.group7.bruno.music.BrunoTrack;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class RouteProcessor {

    /**
     * Custom exception thrown when the number of tracks in a playlist is less than the required
     * number to map to a series of route segments
     */
    public static class TrackIndexOutOfBoundsException extends ArrayIndexOutOfBoundsException {
        public TrackIndexOutOfBoundsException() {
            super("Expected total playlist duration length to be longer than the total route segment duration");
        }
    }

    /**
     * Given a series of route segments and spotify tracks, return a mapping of route segments
     * to each track.
     * @param routeSegments
     * @param playlist
     * @return
     */
    public static List<RouteTrackMapping> execute(final List<RouteSegment> routeSegments, final BrunoPlaylist playlist)
            throws TrackIndexOutOfBoundsException {
        List<RouteTrackMapping> result = new ArrayList<>();
        int currTrackInd = 0;
        // Duration is measured in milliseconds
        long accumulatedRouteSegmentDuration = 0;
        List<RouteSegment> accumulatedRouteSegments = new LinkedList<>();
        LinkedList<RouteSegment> routeSegmentsCopy = new LinkedList<>(routeSegments);
        List<BrunoTrack> tracks = playlist.tracks;
        while (routeSegmentsCopy.size() > 0) {
            if (currTrackInd >= tracks.size()) {
                throw new TrackIndexOutOfBoundsException();
            }

            BrunoTrack currTrack = tracks.get(currTrackInd);

            RouteSegment currentRouteSegment = routeSegmentsCopy.poll();
            LatLng routeSegmentStart = currentRouteSegment.getStartLocation();
            LatLng routeSegmentEnd = currentRouteSegment.getEndLocation();
            long routeSegmentDuration = currentRouteSegment.getDuration();
            long lastSongSegment = accumulatedRouteSegmentDuration + routeSegmentDuration;

            if (lastSongSegment > currTrack.duration) {
                // Represents the duration of each half of a segment since it needs to be split
                long segmentDurationFirstHalf = currTrack.duration - accumulatedRouteSegmentDuration;
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
                RouteTrackMapping rtm = new RouteTrackMapping(accumulatedRouteSegments, currTrack);
                result.add(rtm);
                accumulatedRouteSegments = new LinkedList<>();

                // Accommodate the second half of route segment for the next track
                routeSegmentsCopy.push(segmentSecondHalf);
                accumulatedRouteSegmentDuration = 0;
                currTrackInd++;
            } else if (lastSongSegment == currTrack.duration) {
                accumulatedRouteSegments.add(currentRouteSegment);
                RouteTrackMapping rtm = new RouteTrackMapping(accumulatedRouteSegments, currTrack);
                result.add(rtm);
                accumulatedRouteSegments = new LinkedList<>();
                accumulatedRouteSegmentDuration = 0;
                currTrackInd++;
            } else {
                accumulatedRouteSegments.add(currentRouteSegment);
                accumulatedRouteSegmentDuration += routeSegmentDuration;
            }
        }
        if (accumulatedRouteSegments.size() > 0) {
            RouteTrackMapping rtm = new RouteTrackMapping(accumulatedRouteSegments, tracks.get(currTrackInd));
            result.add(rtm);
        }
        return result;
    }

    public static List<LatLng> getCheckpoints(final List<RouteTrackMapping> routeTrackMappings) {
        List<LatLng> result = new ArrayList<>();
        for (final RouteTrackMapping mapping : routeTrackMappings) {
            for (final RouteSegment segment : mapping.routeSegments) {
                result.add(segment.getStartLocation());
            }
        }

        // Last checkpoint should be same as first point
        if (!result.isEmpty()) {
            result.add(result.get(0));
        }

        return result;
    }
}
