package com.cs446.group7.bruno.routing;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

public class RouteProcessor {
    /**
     * Given a series of route segments and spotify tracks, return a mapping of route segments
     * to each track.
     * @param routeSegments
     * @param tracks
     * @return
     */
    public RouteTrackMapping[] execute(RouteSegment[] routeSegments, BrunoTrack[] tracks) {
        List<RouteTrackMapping> result = new ArrayList<>();
        int currTrackInd = 0;
        long accumulatedRouteSegmentDuration = 0;
        List<RouteSegment> accumulatedRouteSegments = new ArrayList<>();
        for (RouteSegment routeSegment : routeSegments) {
            if (currTrackInd > tracks.length) {
                // throw corresponding exception
            }

            BrunoTrack currTrack = tracks[currTrackInd];
            if (accumulatedRouteSegmentDuration + routeSegment.getDurationInMilliseconds() > currTrack.duration) {
                long routeDurationUnder = currTrack.duration - accumulatedRouteSegmentDuration;
                long routeDurationOver = routeSegment.getDurationInMilliseconds() - routeDurationUnder;
                long percentOfRouteUnder = routeDurationUnder / routeSegment.getDurationInMilliseconds();

                double diffLat = routeSegment.getEndLocation().latitude - routeSegment.getStartLocation().latitude;
                double diffLng = routeSegment.getEndLocation().longitude - routeSegment.getStartLocation().longitude;

                double cutoffLat = routeSegment.getStartLocation().latitude + (diffLat / percentOfRouteUnder);
                double cutoffLng = routeSegment.getStartLocation().longitude + (diffLng / percentOfRouteUnder);

                LatLng cutoffPoint = new LatLng(cutoffLat, cutoffLng);
                RouteSegment underRouteSegment = new RouteSegment(routeSegment.getStartLocation(),
                        cutoffPoint, (int) (routeDurationUnder / 1000));
                RouteSegment overRouteSegment = new RouteSegment(cutoffPoint,
                        routeSegment.getEndLocation(), (int) (routeDurationOver / 1000));
                accumulatedRouteSegments.add(underRouteSegment);
                RouteTrackMapping rtm = new RouteTrackMapping(accumulatedRouteSegments.toArray(
                        new RouteSegment[accumulatedRouteSegments.size()]), currTrack);
                result.add(rtm);
                accumulatedRouteSegments.clear();
                accumulatedRouteSegments.add(overRouteSegment);
                accumulatedRouteSegmentDuration = routeDurationOver;
                currTrackInd++;
            } else if (accumulatedRouteSegmentDuration + routeSegment.getDurationInMilliseconds() == currTrack.duration) {
                accumulatedRouteSegments.add(routeSegment);
                RouteTrackMapping rtm = new RouteTrackMapping(accumulatedRouteSegments.toArray(
                        new RouteSegment[accumulatedRouteSegments.size()]), currTrack);
                result.add(rtm);
                accumulatedRouteSegments.clear();
                accumulatedRouteSegmentDuration = 0;
                currTrackInd++;
            } else {
                accumulatedRouteSegments.add(routeSegment);
                accumulatedRouteSegmentDuration += routeSegment.getDurationInMilliseconds();
            }
        }
        return result.toArray(new RouteTrackMapping[result.size()]);
    }
}
