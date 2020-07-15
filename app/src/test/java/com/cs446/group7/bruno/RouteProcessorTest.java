package com.cs446.group7.bruno;

import com.cs446.group7.bruno.music.BrunoPlaylist;
import com.cs446.group7.bruno.music.BrunoTrack;
import com.cs446.group7.bruno.routing.RouteProcessor;
import com.cs446.group7.bruno.routing.RouteSegment;
import com.cs446.group7.bruno.routing.RouteTrackMapping;
import com.google.android.gms.maps.model.LatLng;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

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
    List<RouteSegment> mockSegments = new ArrayList<>();

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

    // Equality functions for tested objects
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

    private void assertEqualRouteTrackMapping(List<RouteTrackMapping> result, List<RouteTrackMapping> answer) {
        assertEquals(result.size(), answer.size());
        for (int i = 0; i < result.size(); i++) {
            assertEquals(result.get(i).routeSegments.size(), answer.get(i).routeSegments.size());
            assertTrue(trackEquals(result.get(i).track, answer.get(i).track));
            for (int j = 0; j < result.get(i).routeSegments.size(); j++) {
                assertTrue(segmentEquals(result.get(i).routeSegments.get(j), answer.get(i).routeSegments.get(j)));
            }
        }
    }

    @Test
    public void routeDurationEqualToSingleTrackDuration() {
        RouteProcessor rp = new RouteProcessor();
        ArrayList<String> mockArtists = new ArrayList<>();
        mockArtists.add("test");
        ArrayList<BrunoTrack> tracks = new ArrayList<>();
        tracks.add(new BrunoTrack("testName", "testAlbum", 280000, mockArtists));
        long totalTrackDuration = 0;
        for (BrunoTrack track : tracks) {
            totalTrackDuration += track.duration;
        }

        BrunoPlaylist playlist = new BrunoPlaylist(
                "playlistName",
                "playlistDescription",
                tracks.size(),
                totalTrackDuration,
                tracks
        );

        List<RouteTrackMapping> result = rp.execute(mockSegments, playlist);
        List<RouteTrackMapping> answer = new ArrayList<>();
        answer.add(new RouteTrackMapping(mockSegments, tracks.get(0)));

        assertEqualRouteTrackMapping(result, answer);
    }

    @Test
    public void routeDurationEqualsToMultipleTrackDuration() {
        RouteProcessor rp = new RouteProcessor();
        ArrayList<String> mockArtists = new ArrayList<>();
        mockArtists.add("test");
        ArrayList<BrunoTrack> tracks = new ArrayList<>();
        tracks.add(new BrunoTrack("testName1", "testAlbum1", 140000, mockArtists));
        tracks.add(new BrunoTrack("testName2", "testAlbum2", 140000, mockArtists));
        long totalTrackDuration = 0;
        for (BrunoTrack track : tracks) {
            totalTrackDuration += track.duration;
        }

        BrunoPlaylist playlist = new BrunoPlaylist(
                "playlistName",
                "playlistDescription",
                tracks.size(),
                totalTrackDuration,
                tracks
        );

        List<RouteTrackMapping> result = rp.execute(mockSegments, playlist);
        List<RouteSegment> route1 = new ArrayList<>();

        route1.add(mockSegment1);
        route1.add(mockSegment2);
        List<RouteSegment> route2 = new ArrayList<>();
        route2.add(mockSegment3);
        route2.add(mockSegment4);
        List<RouteTrackMapping> answer = new ArrayList<>();
        answer.add(new RouteTrackMapping(route1, tracks.get(0)));
        answer.add(new RouteTrackMapping(route2, tracks.get(1)));

        assertEqualRouteTrackMapping(result, answer);
    }

    @Test
    public void routeDurationShorterThanSingleTrackDuration() {
        RouteProcessor rp = new RouteProcessor();
        ArrayList<String> mockArtists = new ArrayList<>();
        mockArtists.add("test");
        ArrayList<BrunoTrack> tracks = new ArrayList<>();
        tracks.add(new BrunoTrack("testName1", "testAlbum1", 300000, mockArtists));
        long totalTrackDuration = 0;
        for (BrunoTrack track : tracks) {
            totalTrackDuration += track.duration;
        }

        BrunoPlaylist playlist = new BrunoPlaylist(
                "playlistName",
                "playlistDescription",
                tracks.size(),
                totalTrackDuration,
                tracks
        );

        List<RouteTrackMapping> result = rp.execute(mockSegments, playlist);
        List<RouteTrackMapping> answer = new ArrayList<>();
        answer.add(new RouteTrackMapping(mockSegments, tracks.get(0)));

        assertEqualRouteTrackMapping(result, answer);
    }

    @Test
    public void routeDurationShorterThanMultipleTrackDuration() {
        RouteProcessor rp = new RouteProcessor();
        ArrayList<String> mockArtists = new ArrayList<>();
        mockArtists.add("test");
        ArrayList<BrunoTrack> tracks = new ArrayList<>();
        tracks.add(new BrunoTrack("testName1", "testAlbum1", 200000, mockArtists));
        tracks.add(new BrunoTrack("testName2", "testAlbum2", 300000, mockArtists));
        long totalTrackDuration = 0;
        for (BrunoTrack track : tracks) {
            totalTrackDuration += track.duration;
        }

        BrunoPlaylist playlist = new BrunoPlaylist(
                "playlistName",
                "playlistDescription",
                tracks.size(),
                totalTrackDuration,
                tracks
        );

        List<RouteTrackMapping> result = rp.execute(mockSegments, playlist);
        List<RouteSegment> route1 = new ArrayList<>();
        route1.add(mockSegment1);
        route1.add(mockSegment2);
        route1.add(mockSegment3);
        List<RouteSegment> route2 = new ArrayList<>();
        route2.add(mockSegment4);
        List<RouteTrackMapping> answer = new ArrayList<>();
        answer.add(new RouteTrackMapping(route1, tracks.get(0)));
        answer.add(new RouteTrackMapping(route2, tracks.get(1)));

        assertEqualRouteTrackMapping(result, answer);
    }

    @Test(expected=RouteProcessor.TrackIndexOutOfBoundsException.class)
    public void routeDurationLongerThanPlaylistDuration() {
        RouteProcessor rp = new RouteProcessor();
        ArrayList<String> mockArtists = new ArrayList<>();
        mockArtists.add("test");
        ArrayList<BrunoTrack> tracks = new ArrayList<>();
        tracks.add(new BrunoTrack("testName1", "testAlbum1", 140000, mockArtists));
        long totalTrackDuration = 0;
        for (BrunoTrack track : tracks) {
            totalTrackDuration += track.duration;
        }

        BrunoPlaylist playlist = new BrunoPlaylist(
                "playlistName",
                "playlistDescription",
                tracks.size(),
                totalTrackDuration,
                tracks
        );

        rp.execute(mockSegments, playlist);
    }

    @Test
    public void routeSegmentDurationLongerThanTrackDuration() {
        RouteProcessor rp = new RouteProcessor();
        ArrayList<String> mockArtists = new ArrayList<>();
        mockArtists.add("test");
        ArrayList<BrunoTrack> tracks = new ArrayList<>();
        tracks.add(new BrunoTrack("testName1", "testAlbum1", 70000, mockArtists));
        tracks.add(new BrunoTrack("testName2", "testAlbum2", 60000, mockArtists));
        tracks.add(new BrunoTrack("testName3", "testAlbum3", 200000, mockArtists));

        long totalTrackDuration = 0;
        for (BrunoTrack track : tracks) {
            totalTrackDuration += track.duration;
        }

        BrunoPlaylist playlist = new BrunoPlaylist(
                "playlistName",
                "playlistDescription",
                tracks.size(),
                totalTrackDuration,
                tracks
        );

        List<RouteTrackMapping> result = rp.execute(mockSegments, playlist);

        assertEquals(result.get(0).routeSegments.size(), 2);
        assertEquals(result.get(0).routeSegments.get(0).getDuration(), 60000);
        assertEquals(result.get(0).routeSegments.get(1).getDuration(), 10000);
        assertEquals(result.get(1).routeSegments.size(), 1);
        assertEquals(result.get(1).routeSegments.get(0).getDuration(), 60000);
        assertEquals(result.get(2).routeSegments.size(), 3);
        assertEquals(result.get(2).routeSegments.get(0).getDuration(), 10000);
        assertEquals(result.get(2).routeSegments.get(1).getDuration(), 60000);
        assertEquals(result.get(2).routeSegments.get(2).getDuration(), 80000);
    }
}
