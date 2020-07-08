package com.cs446.group7.bruno;

import com.cs446.group7.bruno.routing.BrunoTrack;
import com.cs446.group7.bruno.routing.RouteProcessor;
import com.cs446.group7.bruno.routing.RouteSegment;
import com.cs446.group7.bruno.routing.RouteTrackMapping;
import com.google.android.gms.maps.model.LatLng;


import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class RouteProcessorTest {
    // These mock segments create a box around the university of waterloo
    RouteSegment mockSegment1 = new RouteSegment(
            new LatLng(43.476861, -80.539940),
            new LatLng(43.478633, -80.535248),
            60
    );
    RouteSegment mockSegment2 = new RouteSegment(
            new LatLng(43.478633, -80.535248),
            new LatLng(43.473752, -80.531724),
            80
    );
    RouteSegment mockSegment3 = new RouteSegment(
            new LatLng(43.473752, -80.531724),
            new LatLng(43.472029, -80.536262),
            60
    );
    RouteSegment mockSegment4 = new RouteSegment(
            new LatLng(43.472029, -80.536262),
            new LatLng(43.476861, -80.539940),
            80
    );

    RouteSegment[] mockSegments = { mockSegment1, mockSegment2, mockSegment3, mockSegment4 };

    private boolean trackEquals(BrunoTrack a, BrunoTrack b) {
        return a.album == b.album &&
                a.artists.equals(b.artists) &&
                a.duration == b.duration &&
                a.name == b.name;
    }

    private boolean segmentEquals(RouteSegment a, RouteSegment b) {
        return a.getStartLocation().equals(b.getStartLocation()) &&
                a.getEndLocation().equals(b.getEndLocation()) &&
                a.getDuration() == b.getDuration();
    }

    @Test
    public void routeDurationEqualToTrackDuration() {
        RouteProcessor rp = new RouteProcessor();
        BrunoTrack[] tracks = {
                new BrunoTrack(280000)
        };

        RouteTrackMapping[] result = rp.execute(mockSegments, tracks);
        RouteTrackMapping[] answer = {
                new RouteTrackMapping(mockSegments, tracks[0])
        };
        assertEquals(result.length, answer.length);
        for (int i = 0; i < result.length; i++) {
            assertEquals(result[i].routeSegments.length, answer[i].routeSegments.length);
            assertTrue(trackEquals(result[i].track, answer[i].track));
            for (int j = 0; j < result[i].routeSegments.length; j++) {
                assertTrue(segmentEquals(result[i].routeSegments[j], answer[i].routeSegments[j]));
            }
        }
    }
}
