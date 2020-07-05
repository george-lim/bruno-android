package com.cs446.group7.bruno.spotify;

// Any class which wants to work with the Spotify player should implement this interface
public interface OnPlayerCallback {
    void onPlayerReady();
    void onPlayerError(Exception underlyingException);
}
