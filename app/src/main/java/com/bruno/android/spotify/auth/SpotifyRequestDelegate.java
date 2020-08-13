package com.bruno.android.spotify.auth;

import androidx.annotation.NonNull;

public interface SpotifyRequestDelegate {
    void handleSpotifyRequest(@NonNull final SpotifyRequest request);
}
