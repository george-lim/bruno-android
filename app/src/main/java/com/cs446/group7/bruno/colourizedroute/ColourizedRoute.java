package com.cs446.group7.bruno.colourizedroute;

import com.cs446.group7.bruno.music.BrunoPlaylist;
import com.cs446.group7.bruno.music.BrunoTrack;
import com.cs446.group7.bruno.routing.RouteSegment;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

// Data structure that contains colourized song route segments
public class ColourizedRoute {

    // MARK: - Private members

    private List<ColourizedRouteSegment> colourizedRouteSegments;

    // MARK: - Lifecycle methods

    public ColourizedRoute(final List<RouteSegment> routeSegments,
                           final int[] routeColours,
                           final BrunoPlaylist playlist) {
        colourizedRouteSegments = processSegments(routeSegments, routeColours, playlist);
    }

    // MARK: - Private methods

    /**
     * Re-segment route segments into colourized song segments
     * NOTE: Total playlist duration is expected to be longer than the total route segment duration.
     */
    private List<ColourizedRouteSegment> processSegments(final List<RouteSegment> routeSegments,
                                                         final int[] routeColours,
                                                         final BrunoPlaylist playlist) {
        List<ColourizedRouteSegment> result = new ArrayList<>();
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

                ColourizedRouteSegment colourizedRouteSegment = new ColourizedRouteSegment(
                        accumulatedRouteSegments,
                        routeColours[routeColourIndex]
                );

                routeColourIndex = (routeColourIndex + 1) % routeColours.length;
                result.add(colourizedRouteSegment);
                accumulatedRouteSegments = new LinkedList<>();

                // Accommodate the second half of route segment for the next track
                routeSegmentsCopy.push(segmentSecondHalf);
                accumulatedRouteSegmentDuration = 0;
                currTrackInd++;
            } else if (lastSongSegment == currTrack.getDuration()) {
                accumulatedRouteSegments.add(currentRouteSegment);

                ColourizedRouteSegment colourizedRouteSegment = new ColourizedRouteSegment(
                        accumulatedRouteSegments,
                        routeColours[routeColourIndex]
                );

                routeColourIndex = (routeColourIndex + 1) % routeColours.length;
                result.add(colourizedRouteSegment);
                accumulatedRouteSegments = new LinkedList<>();
                accumulatedRouteSegmentDuration = 0;
                currTrackInd++;
            } else {
                accumulatedRouteSegments.add(currentRouteSegment);
                accumulatedRouteSegmentDuration += routeSegmentDuration;
            }
        }

        if (accumulatedRouteSegments.size() > 0) {
            ColourizedRouteSegment colourizedRouteSegment = new ColourizedRouteSegment(
                    accumulatedRouteSegments,
                    routeColours[routeColourIndex]
            );

            result.add(colourizedRouteSegment);
        }

        return result;
    }

    // MARK: - Public methods

    public List<ColourizedRouteSegment> getSegments() {
        return colourizedRouteSegments;
    }
}
