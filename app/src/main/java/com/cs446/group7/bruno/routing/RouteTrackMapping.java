package com.cs446.group7.bruno.routing;

public class RouteTrackMapping {
    public RouteSegment[] routeSegments;
    public BrunoTrack track;

    public RouteTrackMapping(RouteSegment[] routeSegments, BrunoTrack track) {
        this.routeSegments = routeSegments;
        this.track = track;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }

        if (!(o instanceof RouteTrackMapping)) {
            return false;
        }

        RouteTrackMapping a = (RouteTrackMapping) o;
        return a.routeSegments.equals(this.routeSegments) &&
                a.track.equals(this.track);
    }
}
