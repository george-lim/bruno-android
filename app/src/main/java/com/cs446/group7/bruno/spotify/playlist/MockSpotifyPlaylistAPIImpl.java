package com.cs446.group7.bruno.spotify.playlist;

import com.cs446.group7.bruno.music.BrunoPlaylist;
import com.cs446.group7.bruno.music.BrunoPlaylistImpl;
import com.cs446.group7.bruno.music.BrunoTrack;
import com.cs446.group7.bruno.music.playlist.PlaylistMetadata;
import com.cs446.group7.bruno.utils.Callback;

import java.util.ArrayList;
import java.util.List;

/**
 * Mock class which will generate fake responses from the Spotify API.
 */
public class MockSpotifyPlaylistAPIImpl implements SpotifyPlaylistAPI {
    public void getPublicAuthorizationToken(final Callback<String, Exception> callback) {
        callback.onSuccess("token");
    }

    public void getPlaylist(final String playlistId,
                     final String accessToken,
                     final Callback<BrunoPlaylist, Exception> callback) {
        ArrayList<BrunoTrack> tracks = new ArrayList<BrunoTrack>(5);
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
