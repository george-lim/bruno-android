package com.cs446.group7.bruno.spotify;

// Heavily "inspired" by routing/OnRouteResponseCallback
public interface OnPlaylistCallback {
    void onPlaylistReady(BrunoPlaylist playlist);
    void onPlaylistError(Exception underlyingException);
}
