package com.cs446.group7.bruno.spotify;

import androidx.annotation.NonNull;

public interface SpotifyRequestDelegate {
    void handleSpotifyRequest(@NonNull final SpotifyRequest request);
}
