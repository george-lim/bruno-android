package com.bruno.android.spotify.auth;

import com.bruno.android.utils.Callback;

public interface SpotifyAuthService {
    // Retrieves access token
    void requestUserAuth(final Callback<String, Void> clientCallback);

    void checkIfUserIsPremium(final String token, final Callback<Boolean, Exception> callback);
}