package com.cs446.group7.bruno;

import com.cs446.group7.bruno.music.BrunoPlaylist;
import com.cs446.group7.bruno.music.BrunoTrack;
import com.cs446.group7.bruno.routing.RouteProcessor;
import com.cs446.group7.bruno.routing.RouteSegment;
import com.cs446.group7.bruno.routing.RouteTrackMapping;
import com.google.android.gms.maps.model.LatLng;

import org.junit.Test;

import java.util.ArrayList;

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

    private void assertEqualRouteTrackMapping(RouteTrackMapping[] result, RouteTrackMapping[] answer) {
        assertEquals(result.length, answer.length);
        for (int i = 0; i < result.length; i++) {
            assertEquals(result[i].routeSegments.length, answer[i].routeSegments.length);
            assertTrue(trackEquals(result[i].track, answer[i].track));
            for (int j = 0; j < result[i].routeSegments.length; j++) {
                assertTrue(segmentEquals(result[i].routeSegments[j], answer[i].routeSegments[j]));
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

        RouteTrackMapping[] result = rp.execute(mockSegments, playlist);
        RouteTrackMapping[] answer = {
                new RouteTrackMapping(mockSegments, tracks.get(0))
        };

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

        RouteTrackMapping[] result = rp.execute(mockSegments, playlist);
        RouteSegment[] route1 = { mockSegment1, mockSegment2 };
        RouteSegment[] route2 = { mockSegment3, mockSegment4 };
        RouteTrackMapping[] answer = {
                new RouteTrackMapping(route1, tracks.get(0)),
                new RouteTrackMapping(route2, tracks.get(1))
        };

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

        RouteTrackMapping[] result = rp.execute(mockSegments, playlist);
        RouteTrackMapping[] answer = {
                new RouteTrackMapping(mockSegments, tracks.get(0)),
        };

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

        RouteTrackMapping[] result = rp.execute(mockSegments, playlist);
        RouteSegment[] route1 = { mockSegment1, mockSegment2, mockSegment3 };
        RouteSegment[] route2 = { mockSegment4 };
        RouteTrackMapping[] answer = {
                new RouteTrackMapping(route1, tracks.get(0)),
                new RouteTrackMapping(route2, tracks.get(1)),
        };

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
}
