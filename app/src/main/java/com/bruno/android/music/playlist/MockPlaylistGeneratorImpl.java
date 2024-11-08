package com.bruno.android.music.playlist;

import com.bruno.android.music.BrunoPlaylist;
import com.bruno.android.music.BrunoPlaylistImpl;
import com.bruno.android.music.BrunoTrack;
import com.bruno.android.utils.Callback;

import java.util.ArrayList;
import java.util.Random;

public class MockPlaylistGeneratorImpl implements PlaylistGenerator {
    public void discoverPlaylist(final Callback<BrunoPlaylist, Exception> callback) {
        Random random = new Random();
        int randomTrackCount = 1 + random.nextInt(50);
        String artists = "Bruno";

        ArrayList<BrunoTrack> tracks = new ArrayList<>(randomTrackCount);

        for (int i = 1; i <= randomTrackCount; ++i) {
            long randomTrackDuration = 30000 * (1 + random.nextInt(10)); // Milliseconds
            tracks.add(new BrunoTrack("Track " + i, artists, randomTrackDuration));
        }

        callback.onSuccess(new BrunoPlaylistImpl("id", "name", tracks));
    }
}
