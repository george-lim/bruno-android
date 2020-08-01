package com.cs446.group7.bruno.spotify.auth;

import com.cs446.group7.bruno.utils.Callback;

public class MockSpotifyAuthServiceImpl implements SpotifyAuthService {
    public void requestUserAuth(final Callback<String, Void> clientCallback) {
        clientCallback.onSuccess("token");
    }
}
