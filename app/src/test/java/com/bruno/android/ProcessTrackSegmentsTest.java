package com.bruno.android;

import static org.junit.Assert.assertEquals;

import com.bruno.android.location.Coordinate;
import com.bruno.android.models.PlaylistModel;
import com.bruno.android.models.TrackSegment;
import com.bruno.android.music.BrunoPlaylist;
import com.bruno.android.music.BrunoPlaylistImpl;
import com.bruno.android.music.BrunoTrack;
import com.bruno.android.routing.RouteSegment;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class ProcessTrackSegmentsTest {
    // These mock segments create a box around the university of waterloo
    final RouteSegment mockSegment1 = new RouteSegment(
            new Coordinate(43.476861, -80.539940),
            new Coordinate(43.478633, -80.535248),
            60000L
    );
    final RouteSegment mockSegment2 = new RouteSegment(
            new Coordinate(43.478633, -80.535248),
            new Coordinate(43.473752, -80.531724),
            80000L
    );
    final RouteSegment mockSegment3 = new RouteSegment(
            new Coordinate(43.473752, -80.531724),
            new Coordinate(43.472029, -80.536262),
            60000L
    );
    final RouteSegment mockSegment4 = new RouteSegment(
            new Coordinate(43.472029, -80.536262),
            new Coordinate(43.476861, -80.539940),
            80000L
    );
    final LinkedList<RouteSegment> mockSegments = new LinkedList<>();

    private static final int[] DEFAULT_ROUTE_COLOURS = new int[]{0};

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

    private void assertEqualTrackSegments(final List<TrackSegment> segments1,
                                          final List<TrackSegment> segments2) {
        assertEquals(segments1.size(), segments2.size());
        for (int i = 0; i < segments1.size(); ++i) {
            List<Coordinate> locations1 = segments1.get(i).getCoordinates();
            List<Coordinate> locations2 = segments2.get(i).getCoordinates();
            assertEquals(locations1, locations2);
        }
    }

    @Test
    public void routeDurationEqualToSingleTrackDuration() {
        List<BrunoTrack> tracks = new LinkedList<>();
        tracks.add(new BrunoTrack("testName", "testArtist", 280000));
        BrunoPlaylist playlist = new BrunoPlaylistImpl("id", "playlistName", tracks);

        List<TrackSegment> answer = new ArrayList<>();
        answer.add(new TrackSegment(mockSegments, DEFAULT_ROUTE_COLOURS[0]));

        PlaylistModel model = new PlaylistModel();
        model.setRouteSegments(mockSegments);
        model.setRouteColours(DEFAULT_ROUTE_COLOURS);
        model.setPlaylist(playlist);
        assertEqualTrackSegments(model.getTrackSegments(), answer);
    }

    @Test
    public void routeDurationEqualsToMultipleTrackDuration() {
        List<BrunoTrack> tracks = new LinkedList<>();
        tracks.add(new BrunoTrack("testName1", "testArtist1", 140000));
        tracks.add(new BrunoTrack("testName2", "testArtist2", 140000));
        BrunoPlaylist playlist = new BrunoPlaylistImpl("id", "playlistName", tracks);

        List<RouteSegment> route1 = new LinkedList<>();
        route1.add(mockSegment1);
        route1.add(mockSegment2);
        List<RouteSegment> route2 = new LinkedList<>();
        route2.add(mockSegment3);
        route2.add(mockSegment4);
        List<TrackSegment> answer = new ArrayList<>();
        answer.add(new TrackSegment(route1, DEFAULT_ROUTE_COLOURS[0]));
        answer.add(new TrackSegment(route2, DEFAULT_ROUTE_COLOURS[0]));

        PlaylistModel model = new PlaylistModel();
        model.setRouteSegments(mockSegments);
        model.setRouteColours(DEFAULT_ROUTE_COLOURS);
        model.setPlaylist(playlist);
        assertEqualTrackSegments(model.getTrackSegments(), answer);
    }

    @Test
    public void routeDurationShorterThanSingleTrackDuration() {
        List<BrunoTrack> tracks = new LinkedList<>();
        tracks.add(new BrunoTrack("testName1", "testArtist1", 300000));
        BrunoPlaylist playlist = new BrunoPlaylistImpl("id", "playlistName", tracks);

        List<TrackSegment> answer = new ArrayList<>();
        answer.add(new TrackSegment(mockSegments, DEFAULT_ROUTE_COLOURS[0]));

        PlaylistModel model = new PlaylistModel();
        model.setRouteSegments(mockSegments);
        model.setRouteColours(DEFAULT_ROUTE_COLOURS);
        model.setPlaylist(playlist);
        assertEqualTrackSegments(model.getTrackSegments(), answer);
    }

    @Test
    public void routeDurationShorterThanMultipleTrackDuration() {
        List<BrunoTrack> tracks = new LinkedList<>();
        tracks.add(new BrunoTrack("testName1", "testArtist1", 200000));
        tracks.add(new BrunoTrack("testName2", "testArtist2", 300000));
        BrunoPlaylist playlist = new BrunoPlaylistImpl("id", "playlistName", tracks);

        List<RouteSegment> route1 = new LinkedList<>();
        route1.add(mockSegment1);
        route1.add(mockSegment2);
        route1.add(mockSegment3);
        List<RouteSegment> route2 = new LinkedList<>();
        route2.add(mockSegment4);
        List<TrackSegment> answer = new ArrayList<>();
        answer.add(new TrackSegment(route1, DEFAULT_ROUTE_COLOURS[0]));
        answer.add(new TrackSegment(route2, DEFAULT_ROUTE_COLOURS[0]));

        PlaylistModel model = new PlaylistModel();
        model.setRouteSegments(mockSegments);
        model.setRouteColours(DEFAULT_ROUTE_COLOURS);
        model.setPlaylist(playlist);
        assertEqualTrackSegments(model.getTrackSegments(), answer);
    }
}
