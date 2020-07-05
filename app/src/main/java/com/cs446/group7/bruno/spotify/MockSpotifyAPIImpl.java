package com.cs446.group7.bruno.spotify;

import android.content.Context;

// To demonstrate how to get a playlist using SpotifyWebAPI and OnPlaylistCallback
public class MockSpotifyAPIImpl implements OnPlaylistCallback {

    public MockSpotifyAPIImpl(Context context) {
        SpotifyWebAPI api = new SpotifyWebAPI(context);
        api.getPlaylist(this);
    }

    public void onPlaylistReady(BrunoPlaylist playlist) {}

    public void onPlaylistError(Exception underlyingException) {}
}
