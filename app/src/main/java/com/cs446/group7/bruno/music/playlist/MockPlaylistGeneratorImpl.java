package com.cs446.group7.bruno.music.playlist;

import android.util.Log;

import com.cs446.group7.bruno.music.BrunoPlaylistImpl;
import com.cs446.group7.bruno.music.BrunoPlaylist;
import com.cs446.group7.bruno.music.BrunoTrack;
import com.cs446.group7.bruno.utils.Callback;

import java.util.ArrayList;

public class MockPlaylistGeneratorImpl implements PlaylistGenerator {

    private final String TAG = getClass().getSimpleName();

    public void getPlaylist(final Callback<BrunoPlaylist, Exception> callback) {
        ArrayList<BrunoTrack> tracks = new ArrayList<BrunoTrack>(5);
        long threeMinutes = 180000; // Milliseconds
        int trackCount = 200;

        for (int i = 0; i < trackCount; ++i) {
            tracks.add(new BrunoTrack("name" + i, "album" + i, threeMinutes));
        }

        BrunoPlaylist playlist = new BrunoPlaylistImpl("id", "name", tracks);
        Log.i(TAG, "getPlaylist(): Returning playlist: " + playlist.getName());
        callback.onSuccess(playlist);
    }

}
