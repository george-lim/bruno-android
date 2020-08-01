package com.cs446.group7.bruno.music.playlist;

import com.cs446.group7.bruno.music.BrunoPlaylist;
import com.cs446.group7.bruno.music.BrunoPlaylistImpl;
import com.cs446.group7.bruno.music.BrunoTrack;
import com.cs446.group7.bruno.utils.Callback;

import java.util.ArrayList;

public class MockPlaylistGeneratorImpl implements PlaylistGenerator {
    public void getPlaylist(final Callback<BrunoPlaylist, Exception> callback) {
        ArrayList<BrunoTrack> tracks = new ArrayList<BrunoTrack>(5);
        String artists = "Jimin, Taylor Swift";
        long threeMinutes = 180000; // Milliseconds
        int trackCount = 200;

        for (int i = 0; i < trackCount; ++i) {
            tracks.add(new BrunoTrack("name" + i, artists, threeMinutes));
        }

        callback.onSuccess(new BrunoPlaylistImpl("id", "name", tracks));
    }
}
