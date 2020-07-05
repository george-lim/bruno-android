package com.cs446.group7.bruno.spotify;

import android.content.Context;
import android.util.Log;

// To demonstrate how to get a playlist using SpotifyWebAPI and SpotifyService,
public class MockSpotifyAPIImpl implements OnPlaylistCallback {

    public MockSpotifyAPIImpl(Context context) {
        SpotifyWebAPI api = new SpotifyWebAPI(context);
        api.getPlaylist(this);
    }

    public void onPlaylistReady(BrunoPlaylist playlist) {
        Log.d("onPlaylistReady", "Playlist name: " + playlist.name);
        Log.d("onPlaylistReady", "Playlist description: " + playlist.description);
        Log.d("onPlaylistReady", "Number of tracks: " + playlist.totalTracks);
        Log.d("onPlaylistReady", "Playlist duration: " + playlist.totalDuration);

        for (int i = 0; i < playlist.totalTracks; ++i ) {
            BrunoTrack track = playlist.tracks.get(i);
            Log.d("onPlaylistReady", "Track " + i + " name: " + track.name);
            Log.d("onPlaylistReady", "Track " + i + " album: " + track.album);
            Log.d("onPlaylistReady", "Track " + i + " duration: " + track.duration);

            for (int j = 0; j < track.artists.size(); ++j) {
                Log.d("onPlaylistReady", "Track " + i +
                        " Artist " + j + " name: " + track.artists.get(j));
            }
        }
    }

    public void onPlaylistError(Exception underlyingException) {}
}
