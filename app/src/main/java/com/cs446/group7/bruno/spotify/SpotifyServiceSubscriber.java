package com.cs446.group7.bruno.spotify;

public interface SpotifyServiceSubscriber {
    void onServiceReady();
    void onError(Exception exception);
}
