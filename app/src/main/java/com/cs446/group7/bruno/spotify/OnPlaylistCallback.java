package com.cs446.group7.bruno.spotify;

// Any class which needs to retrieve a playlist from Spotify API should implement this interface
public interface OnPlaylistCallback {
    void onPlaylistReady(BrunoPlaylist playlist);
    void onPlaylistError(Exception underlyingException);
}
