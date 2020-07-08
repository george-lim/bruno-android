package com.cs446.group7.bruno.music.playlist;

import com.cs446.group7.bruno.music.BrunoPlaylist;
import com.cs446.group7.bruno.music.BrunoTrack;
import com.cs446.group7.bruno.utils.Callback;

import java.util.ArrayList;

public class MockPlaylistGeneratorImpl implements PlaylistGenerator {

    public void getPlaylist(String playlistId, Callback<BrunoPlaylist, Exception> callback) {
        ArrayList<BrunoTrack> tracks = new ArrayList<BrunoTrack>(5);
        ArrayList<String> artists = new ArrayList<String>(2);
        artists.add("Bruno Mars");
        artists.add("Burno Mars");
        long three_minutes = 180000; // Milliseconds
        int track_count = 50;
        for (int i = 0; i < track_count; ++i) {
            ArrayList<String> artistsClone = (ArrayList<String>) artists.clone();
            tracks.add(new BrunoTrack("name" + i, "album" + i,
                    three_minutes, artistsClone));
        }

        BrunoPlaylist playlist = new BrunoPlaylist("playlist", "description",
                track_count, three_minutes * track_count, tracks);
        callback.onSuccess(playlist);

    }

}
