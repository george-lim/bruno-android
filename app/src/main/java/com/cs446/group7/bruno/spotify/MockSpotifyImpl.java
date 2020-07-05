package com.cs446.group7.bruno.spotify;

import android.content.Context;
import android.util.Log;

// To demonstrate how to get a playlist and play music using SpotifyWebAPI and SpotifyService,
// using interfaces OnPlaylistCallback and OnPlayerCallback respectively
public class MockSpotifyImpl implements OnPlaylistCallback, OnPlayerCallback {

    final SpotifyService player;

    public MockSpotifyImpl(Context context) {
        final SpotifyWebAPI api = new SpotifyWebAPI(context);
        api.getPlaylist(this);
        player = new SpotifyService();
        player.connectToSpotify(this, context);
    }

    public void onPlaylistReady(BrunoPlaylist playlist) {
        Log.i("onPlaylistReady", "Playlist name: " + playlist.name);
        Log.i("onPlaylistReady", "Playlist description: " + playlist.description);
        Log.i("onPlaylistReady", "Number of tracks: " + playlist.totalTracks);
        Log.i("onPlaylistReady", "Playlist duration: " + playlist.totalDuration);

        for (int i = 0; i < playlist.totalTracks; ++i ) {
            BrunoTrack track = playlist.tracks.get(i);
            Log.i("onPlaylistReady", "Track " + i + " name: " + track.name);
            Log.i("onPlaylistReady", "Track " + i + " album: " + track.album);
            Log.i("onPlaylistReady", "Track " + i + " duration: " + track.duration);

            for (int j = 0; j < track.artists.size(); ++j) {
                Log.i("onPlaylistReady", "Track " + i +  " Artist " + j + " name: "
                        + track.artists.get(j));
            }
        }
    }

    public void onPlaylistError(Exception underlyingException) {
        Log.e("onPlaylistError", underlyingException.getMessage());
    }

    public void onPlayerReady() {
        player.playMusic();
    }

    public void onPlayerError(Exception underlyingException) {
        Log.e("onPlayerError", underlyingException.getMessage());
    }

    protected void finalize() {
        player.disconnectFromSpotify();
    }
}
