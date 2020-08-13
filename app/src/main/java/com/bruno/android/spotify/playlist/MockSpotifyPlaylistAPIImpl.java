package com.bruno.android.spotify.playlist;

import com.bruno.android.music.BrunoPlaylist;
import com.bruno.android.music.BrunoPlaylistImpl;
import com.bruno.android.music.BrunoTrack;
import com.bruno.android.music.playlist.PlaylistMetadata;
import com.bruno.android.utils.Callback;

import java.util.ArrayList;
import java.util.List;

/**
 * Mock class which will generate fake responses from the Spotify API.
 */
public class MockSpotifyPlaylistAPIImpl implements SpotifyPlaylistAPI {
    public void getPlaylist(final String accessToken,
                            final String playlistId,
                            final Callback<BrunoPlaylist, Exception> callback) {
        ArrayList<BrunoTrack> tracks = new ArrayList<>(5);
        String artists = "Jimin, Taylor Swift";
        long threeMinutes = 180000; // Milliseconds
        int trackCount = 200;

        for (int i = 0; i < trackCount; ++i) {
            tracks.add(new BrunoTrack("name" + i, artists, threeMinutes));
        }

        callback.onSuccess(new BrunoPlaylistImpl(playlistId, "name", tracks));
    }

    public void getUserPlaylistLibrary(String accessToken, final Callback<List<PlaylistMetadata>, Exception> callback) {
        final List<PlaylistMetadata> playlists = new ArrayList<>();
        final int playlistCount = 20;
        for (int i = 0; i < playlistCount; ++i) {
            playlists.add(new PlaylistMetadata("id " + i, "name " + i, i));
        }
        callback.onSuccess(playlists);
    }
}
