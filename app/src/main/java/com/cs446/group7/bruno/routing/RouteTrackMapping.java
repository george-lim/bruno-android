package com.cs446.group7.bruno.routing;

import com.cs446.group7.bruno.music.BrunoTrack;

import java.util.List;

public class RouteTrackMapping {
    public List<RouteSegment> routeSegments;
    public BrunoTrack track;

    public RouteTrackMapping(List<RouteSegment> routeSegments, BrunoTrack track) {
        this.routeSegments = routeSegments;
        this.track = track;
    }
}
