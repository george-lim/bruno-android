package com.bruno.android.models;

import com.bruno.android.location.Coordinate;
import com.bruno.android.routing.RouteSegment;
import com.google.android.gms.maps.model.LatLng;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class TrackSegment implements Serializable {

    // MARK: - Private members

    private List<Coordinate> coordinates;
    private List<Long> durations;
    private int routeColour;

    // MARK: - Lifecycle methods

    public TrackSegment(final List<RouteSegment> routeSegments,
                        int routeColour) {
        processCoordinates(routeSegments);
        this.routeColour = routeColour;
    }

    // MARK: - Private methods

    private void processCoordinates(final List<RouteSegment> routeSegments) {
        coordinates = new ArrayList<>(routeSegments.size());
        durations = new ArrayList<>(routeSegments.size());

        for (RouteSegment routeSegment : routeSegments) {
            coordinates.add(routeSegment.getStartCoordinate());
            durations.add(routeSegment.getDuration());
        }

        RouteSegment lastRouteSegment = routeSegments.get(routeSegments.size() - 1);
        coordinates.add(lastRouteSegment.getEndCoordinate());
    }

    // MARK: - Public methods

    public List<Coordinate> getCoordinates() {
        return coordinates;
    }

    public List<LatLng> getLatLngs() {
        List<LatLng> latLngs = new ArrayList<>(coordinates.size());

        for (Coordinate coordinate : coordinates) {
            latLngs.add(coordinate.getLatLng());
        }

        return latLngs;
    }

    public int getRouteColour() {
        return routeColour;
    }

    // Returns distance of the track segment
    public double getDistance() {
        double distance = 0;

        for (int i = 0; i+1 < coordinates.size(); ++i) {
            distance += coordinates.get(i).getDistance(coordinates.get(i+1));
        }

        return distance;
    }

    public long getDuration() {
        long totalDuration = 0;

        for (long duration : durations) {
            totalDuration += duration;
        }

        return totalDuration;
    }

    // Returns the coordinate at the playbackPosition of a track
    public Coordinate getCoordinate(long playbackPosition) {
        int routeSegmentIndex;
        long routeSegmentPlaybackPosition = playbackPosition;

        for (routeSegmentIndex = 0; routeSegmentIndex < durations.size(); ++routeSegmentIndex) {
            if (routeSegmentPlaybackPosition - durations.get(routeSegmentIndex) <= 0) {
                break;
            }

            routeSegmentPlaybackPosition -= durations.get(routeSegmentIndex);
        }

        Coordinate routeSegmentStart = coordinates.get(routeSegmentIndex);
        // i+1 is safe because coordinates contains last segment's end coordinate as well
        Coordinate routeSegmentEnd = coordinates.get(routeSegmentIndex+1);

        long routeSegmentDuration = durations.get(routeSegmentIndex);
        double routeSegmentPlaybackRatio = (double)routeSegmentPlaybackPosition / routeSegmentDuration;

        return routeSegmentStart.getSplitPoint(routeSegmentEnd, routeSegmentPlaybackRatio);
    }
}
