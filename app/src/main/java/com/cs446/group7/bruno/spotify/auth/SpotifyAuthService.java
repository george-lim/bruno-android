package com.cs446.group7.bruno.spotify.auth;

import com.cs446.group7.bruno.utils.Callback;

public interface SpotifyAuthService {
    // Retrieves access token
    void requestUserAuth(final Callback<String, Void> clientCallback);
    void checkIfUserIsPremium(final String token, final Callback<Boolean, Exception> callback);
}