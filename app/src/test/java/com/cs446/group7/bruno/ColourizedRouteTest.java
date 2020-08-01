package com.cs446.group7.bruno;

import com.cs446.group7.bruno.colourizedroute.ColourizedRoute;
import com.cs446.group7.bruno.colourizedroute.ColourizedRouteSegment;
import com.cs446.group7.bruno.music.BrunoPlaylist;
import com.cs446.group7.bruno.music.BrunoPlaylistImpl;
import com.cs446.group7.bruno.music.BrunoTrack;
import com.cs446.group7.bruno.routing.RouteSegment;
import com.google.android.gms.maps.model.LatLng;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ColourizedRouteTest {
    // These mock segments create a box around the university of waterloo
    RouteSegment mockSegment1 = new RouteSegment(
            new LatLng(43.476861, -80.539940),
            new LatLng(43.478633, -80.535248),
            60000l
    );
    RouteSegment mockSegment2 = new RouteSegment(
            new LatLng(43.478633, -80.535248),
            new LatLng(43.473752, -80.531724),
            80000l
    );
    RouteSegment mockSegment3 = new RouteSegment(
            new LatLng(43.473752, -80.531724),
            new LatLng(43.472029, -80.536262),
            60000l
    );
    RouteSegment mockSegment4 = new RouteSegment(
            new LatLng(43.472029, -80.536262),
            new LatLng(43.476861, -80.539940),
            80000l
    );
    LinkedList<RouteSegment> mockSegments = new LinkedList<>();

    private static int[] DEFAULT_ROUTE_COLOURS = new int[] { 0 };

    @Before
    public void setup() {
        mockSegments.add(mockSegment1);
        mockSegments.add(mockSegment2);
        mockSegments.add(mockSegment3);
        mockSegments.add(mockSegment4);
    }

    @After
    public void teardown() {
        mockSegments.clear();
    }

    private boolean segmentEquals(RouteSegment a, RouteSegment b) {
        return a.getStartLocation().equals(b.getStartLocation()) &&
                a.getEndLocation().equals(b.getEndLocation()) &&
                a.getDuration() == b.getDuration();
    }

    private void assertSameColourizedSegments(ColourizedRoute route, List<ColourizedRouteSegment> segments) {
        List<ColourizedRouteSegment> colourizedRouteSegments = route.getSegments();
        assertEquals(colourizedRouteSegments.size(), segments.size());

        for (int i = 0; i < colourizedRouteSegments.size(); ++i) {
            List<RouteSegment> routeSegments1 = colourizedRouteSegments.get(i).getRouteSegments();
            List<RouteSegment> routeSegments2 = segments.get(i).getRouteSegments();
            assertEquals(routeSegments1.size(), routeSegments2.size());

            for (int j = 0; j < routeSegments1.size(); ++j) {
                assertTrue(segmentEquals(routeSegments1.get(j), routeSegments2.get(j)));
            }
        }
    }

    @Test
    public void routeDurationEqualToSingleTrackDuration() {
        List<String> mockArtists = new LinkedList<>();
        mockArtists.add("test");
        List<BrunoTrack> tracks = new LinkedList<>();
        tracks.add(new BrunoTrack("testName", "testAlbum", 280000, mockArtists));
        BrunoPlaylist playlist = new BrunoPlaylistImpl("id", "playlistName", tracks);

        List<ColourizedRouteSegment> answer = new ArrayList<>();
        answer.add(new ColourizedRouteSegment(mockSegments, DEFAULT_ROUTE_COLOURS[0]));

        assertSameColourizedSegments(
                new ColourizedRoute(mockSegments, DEFAULT_ROUTE_COLOURS, playlist),
                answer
        );
    }

    @Test
    public void routeDurationEqualsToMultipleTrackDuration() {
        List<String> mockArtists = new LinkedList<>();
        mockArtists.add("test");
        List<BrunoTrack> tracks = new LinkedList<>();
        tracks.add(new BrunoTrack("testName1", "testAlbum1", 140000, mockArtists));
        tracks.add(new BrunoTrack("testName2", "testAlbum2", 140000, mockArtists));
        BrunoPlaylist playlist = new BrunoPlaylistImpl("id", "playlistName", tracks);

        List<RouteSegment> route1 = new LinkedList<>();
        route1.add(mockSegment1);
        route1.add(mockSegment2);
        List<RouteSegment> route2 = new LinkedList<>();
        route2.add(mockSegment3);
        route2.add(mockSegment4);
        List<ColourizedRouteSegment> answer = new ArrayList<>();
        answer.add(new ColourizedRouteSegment(route1, DEFAULT_ROUTE_COLOURS[0]));
        answer.add(new ColourizedRouteSegment(route2, DEFAULT_ROUTE_COLOURS[0]));

        assertSameColourizedSegments(
                new ColourizedRoute(mockSegments, DEFAULT_ROUTE_COLOURS, playlist),
                answer
        );
    }

    @Test
    public void routeDurationShorterThanSingleTrackDuration() {
        List<String> mockArtists = new LinkedList<>();
        mockArtists.add("test");
        List<BrunoTrack> tracks = new LinkedList<>();
        tracks.add(new BrunoTrack("testName1", "testAlbum1", 300000, mockArtists));
        BrunoPlaylist playlist = new BrunoPlaylistImpl("id", "playlistName", tracks);

        List<ColourizedRouteSegment> answer = new ArrayList<>();
        answer.add(new ColourizedRouteSegment(mockSegments, DEFAULT_ROUTE_COLOURS[0]));

        assertSameColourizedSegments(
                new ColourizedRoute(mockSegments, DEFAULT_ROUTE_COLOURS, playlist),
                answer
        );
    }

    @Test
    public void routeDurationShorterThanMultipleTrackDuration() {
        List<String> mockArtists = new LinkedList<>();
        mockArtists.add("test");
        List<BrunoTrack> tracks = new LinkedList<>();
        tracks.add(new BrunoTrack("testName1", "testAlbum1", 200000, mockArtists));
        tracks.add(new BrunoTrack("testName2", "testAlbum2", 300000, mockArtists));
        BrunoPlaylist playlist = new BrunoPlaylistImpl("id", "playlistName", tracks);

        List<RouteSegment> route1 = new LinkedList<>();
        route1.add(mockSegment1);
        route1.add(mockSegment2);
        route1.add(mockSegment3);
        List<RouteSegment> route2 = new LinkedList<>();
        route2.add(mockSegment4);
        List<ColourizedRouteSegment> answer = new ArrayList<>();
        answer.add(new ColourizedRouteSegment(route1, DEFAULT_ROUTE_COLOURS[0]));
        answer.add(new ColourizedRouteSegment(route2, DEFAULT_ROUTE_COLOURS[0]));

        assertSameColourizedSegments(
                new ColourizedRoute(mockSegments, DEFAULT_ROUTE_COLOURS, playlist),
                answer
        );
    }

    @Test(expected=IndexOutOfBoundsException.class)
    public void routeDurationLongerThanPlaylistDuration() {
        List<String> mockArtists = new LinkedList<>();
        mockArtists.add("test");
        List<BrunoTrack> tracks = new LinkedList<>();
        tracks.add(new BrunoTrack("testName1", "testAlbum1", 140000, mockArtists));
        BrunoPlaylist playlist = new BrunoPlaylistImpl("id", "playlistName", tracks);

        ColourizedRoute _ = new ColourizedRoute(mockSegments, DEFAULT_ROUTE_COLOURS, playlist);
    }

    @Test
    public void routeSegmentDurationLongerThanTrackDuration() {
        List<String> mockArtists = new LinkedList<>();
        mockArtists.add("test");
        List<BrunoTrack> tracks = new LinkedList<>();
        tracks.add(new BrunoTrack("testName1", "testAlbum1", 70000, mockArtists));
        tracks.add(new BrunoTrack("testName2", "testAlbum2", 60000, mockArtists));
        tracks.add(new BrunoTrack("testName3", "testAlbum3", 200000, mockArtists));
        BrunoPlaylist playlist = new BrunoPlaylistImpl("id", "playlistName", tracks);

        ColourizedRoute result = new ColourizedRoute(mockSegments, DEFAULT_ROUTE_COLOURS, playlist);
        List<RouteSegment> routeSegments = result.getSegments().get(0).getRouteSegments();
        assertEquals(routeSegments.size(), 2);
        assertEquals(routeSegments.get(0).getDuration(), 60000);
        assertEquals(routeSegments.get(1).getDuration(), 10000);

        routeSegments = result.getSegments().get(1).getRouteSegments();
        assertEquals(routeSegments.size(), 1);
        assertEquals(routeSegments.get(0).getDuration(), 60000);

        routeSegments = result.getSegments().get(2).getRouteSegments();
        assertEquals(routeSegments.size(), 3);
        assertEquals(routeSegments.get(0).getDuration(), 10000);
        assertEquals(routeSegments.get(1).getDuration(), 60000);
        assertEquals(routeSegments.get(2).getDuration(), 80000);
    }
}
