package com.cs446.group7.bruno.routing;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

public class RouteProcessor {
    class BrunoTrack {
        public String album;
        public String artist;
        public ArrayList<String> artists;
        public long duration; // This is in milliseconds
        public String name;
    }

    class ProcessedRoute {
        RouteSegment[] routeSegments;
        BrunoTrack track;

        ProcessedRoute(RouteSegment[] routeSegments, BrunoTrack track) {
            this.routeSegments = routeSegments;
            this.track = track;
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append(this.track.name);
            sb.append(" has routes:\n");
            for (RouteSegment rs : routeSegments) {
                sb.append(rs.toString());
            }
            return sb.toString();
        }
    }

    // RouteSegment duration is in seconds
    public ProcessedRoute[] execute(RouteSegment[] routeSegments, BrunoTrack[] tracks) {
        int currSegmentIndex = 0;
        ArrayList<ProcessedRoute> processedRoutes = new ArrayList<>();
        ArrayList<RouteSegment> associatedSegments = new ArrayList<>();
        int accumulatedLegDuration = 0;
        for (BrunoTrack track : tracks) {
            while (accumulatedLegDuration < track.duration) {
                RouteSegment currSegment = routeSegments[currSegmentIndex];
                if (accumulatedLegDuration + (currSegment.getDuration() * 1000) > track.duration) {
                    // The next segment added will overflow the track duration
                    // THIS IS THE SPECIAL CASE TO CUT THE segment up
                    int overflow = (int) (accumulatedLegDuration + (currSegment.getDuration() * 1000) - track.duration);
                    int cutoffDuration = (int) (track.duration - overflow);
                    long percentTrackIncluded = (overflow - track.duration) / track.duration;

                    // end - start = total distance of line
                    double diffLat = currSegment.getEndLocation().latitude - currSegment.getStartLocation().latitude;
                    double diffLng = currSegment.getEndLocation().longitude - currSegment.getEndLocation().longitude;

                    double cutoffLat = currSegment.getStartLocation().latitude + (diffLat / percentTrackIncluded);
                    double cutoffLng = currSegment.getStartLocation().longitude + (diffLng / percentTrackIncluded);

                    LatLng cutoffPoint = new LatLng(cutoffLat, cutoffLng);
                    RouteSegment firstCutSegment = new RouteSegment(
                            currSegment.getStartLocation(), cutoffPoint, cutoffDuration);
                    RouteSegment secondCutSegment = new RouteSegment(
                            cutoffPoint, currSegment.getEndLocation(), overflow);

                    associatedSegments.add(firstCutSegment);

                    ProcessedRoute resultingRoute = new ProcessedRoute(associatedSegments.toArray(
                            new RouteSegment[associatedSegments.size()]), track);
                    processedRoutes.add(resultingRoute);
                    associatedSegments.clear();

                    // Adding the overflow route to next track association
                    associatedSegments.add(secondCutSegment);
                    accumulatedLegDuration += secondCutSegment.getDuration();
                } else {
                    // This segment can be apart of this track
                    associatedSegments.add(currSegment);
                    accumulatedLegDuration += currSegment.getDuration() * 1000;
                }
                currSegmentIndex++;
            }
        }
        return processedRoutes.toArray(new ProcessedRoute[processedRoutes.size()]);
    }
}
