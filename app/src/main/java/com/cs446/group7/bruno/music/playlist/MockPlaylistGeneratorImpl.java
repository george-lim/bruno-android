package com.cs446.group7.bruno.music.playlist;

import com.cs446.group7.bruno.music.BrunoPlaylist;
import com.cs446.group7.bruno.music.BrunoPlaylistImpl;
import com.cs446.group7.bruno.music.BrunoTrack;
import com.cs446.group7.bruno.utils.Callback;

import java.util.ArrayList;

public class MockPlaylistGeneratorImpl implements PlaylistGenerator {

    private final String TAG = getClass().getSimpleName();

    // NOTE: Since ArrayList.clone should not be used, this function is used instead
    private ArrayList<String> cloneArtists(final ArrayList<String> artists) {
        ArrayList<String> clone = new ArrayList<>();

        for (String artist : artists) {
            clone.add(String.valueOf(artist));
        }

        return clone;
    }

    public void getPlaylist(final Callback<BrunoPlaylist, Exception> callback) {
        ArrayList<BrunoTrack> tracks = new ArrayList<BrunoTrack>(5);
        ArrayList<String> artists = new ArrayList<String>(2);
        artists.add("Jimin");
        artists.add("Taylor Swift");
        long threeMinutes = 180000; // Milliseconds
        int trackCount = 200;
        for (int i = 0; i < trackCount; ++i) {
            tracks.add(new BrunoTrack("name" + i, "album" + i,
                    threeMinutes, cloneArtists(artists)));
        }

        callback.onSuccess(new BrunoPlaylistImpl("id", "name", tracks));
    }
}
