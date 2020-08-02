package com.cs446.group7.bruno.spotify.auth;

import com.cs446.group7.bruno.utils.Callback;

public class MockSpotifyAuthServiceImpl implements SpotifyAuthService {
    @Override
    public void requestUserAuth(final Callback<String, Void> clientCallback) {
        clientCallback.onSuccess("token");
    }

    @Override
    public void checkIfUserIsPremium(String token, Callback<Boolean, Void> callback) {
        callback.onSuccess(true);
    }
}
