package com.cs446.group7.bruno.routing;

import com.cs446.group7.bruno.music.BrunoPlaylist;
import com.cs446.group7.bruno.music.BrunoTrack;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
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
    public static List<RouteTrackMapping> execute(List<RouteSegment> routeSegments, BrunoPlaylist playlist) throws TrackIndexOutOfBoundsException {
        List<RouteTrackMapping> result = new ArrayList<>();
        int currTrackInd = 0;
        // Duration is measured in milliseconds
        long accumulatedRouteSegmentDuration = 0;
        List<RouteSegment> accumulatedRouteSegments = new ArrayList<>();
        List<BrunoTrack> tracks = playlist.tracks;
        for (RouteSegment routeSegment : routeSegments) {
            if (currTrackInd >= tracks.size()) {
                throw new TrackIndexOutOfBoundsException();
            }

            BrunoTrack currTrack = tracks.get(currTrackInd);

            LatLng routeSegmentStart = routeSegment.getStartLocation();
            LatLng routeSegmentEnd = routeSegment.getEndLocation();
            long routeSegmentDuration = routeSegment.getDuration();

            long lastSongSegment = accumulatedRouteSegmentDuration + routeSegmentDuration;

            if (lastSongSegment > currTrack.duration) {
                // Represents the duration of each half of a segment since it needs to be split
                long segmentDurationFirstHalf = currTrack.duration - accumulatedRouteSegmentDuration;
                long segmentDurationSecondHalf = routeSegmentDuration - segmentDurationFirstHalf;
                long segmentDurationRatio = segmentDurationFirstHalf / routeSegmentDuration;

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
                        routeSegment.getEndLocation(), segmentDurationSecondHalf);

                // Create mapping of accumulated segments and first half segment with current track
                accumulatedRouteSegments.add(segmentFirstHalf);
                RouteTrackMapping rtm = new RouteTrackMapping(new ArrayList<>(accumulatedRouteSegments), currTrack);
                result.add(rtm);
                accumulatedRouteSegments.clear();

                // Accommodate the second half of route segment for the next track
                accumulatedRouteSegments.add(segmentSecondHalf);
                accumulatedRouteSegmentDuration = segmentDurationSecondHalf;
                currTrackInd++;
            } else if (lastSongSegment == currTrack.duration) {
                accumulatedRouteSegments.add(routeSegment);
                RouteTrackMapping rtm = new RouteTrackMapping(new ArrayList<>(accumulatedRouteSegments), currTrack);
                result.add(rtm);
                accumulatedRouteSegments.clear();
                accumulatedRouteSegmentDuration = 0;
                currTrackInd++;
            } else {
                accumulatedRouteSegments.add(routeSegment);
                accumulatedRouteSegmentDuration += routeSegmentDuration;
            }
        }
        if (accumulatedRouteSegments.size() > 0) {
            RouteTrackMapping rtm = new RouteTrackMapping(accumulatedRouteSegments, tracks.get(currTrackInd));
            result.add(rtm);
        }
        return result;
    }
}
