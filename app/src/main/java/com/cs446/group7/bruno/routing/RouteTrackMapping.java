package com.cs446.group7.bruno.routing;

import com.cs446.group7.bruno.music.BrunoTrack;

public class RouteTrackMapping {
    public RouteSegment[] routeSegments;
    public BrunoTrack track;

    public RouteTrackMapping(RouteSegment[] routeSegments, BrunoTrack track) {
        this.routeSegments = routeSegments;
        this.track = track;
    }
}
