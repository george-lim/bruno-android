package com.cs446.group7.bruno.routing;

public class RouteTrackMapping {
    public RouteSegment[] routeSegments;
    public BrunoTrack track;

    public RouteTrackMapping(RouteSegment[] routeSegments, BrunoTrack track) {
        this.routeSegments = routeSegments;
        this.track = track;
    }
}
