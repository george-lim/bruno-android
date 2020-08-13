package com.bruno.android.spotify.auth;

import com.bruno.android.utils.Callback;

public class MockSpotifyAuthServiceImpl implements SpotifyAuthService {
    @Override
    public void requestUserAuth(final Callback<String, Void> clientCallback) {
        clientCallback.onSuccess("token");
    }

    @Override
    public void checkIfUserIsPremium(String token, Callback<Boolean, Exception> callback) {
        callback.onSuccess(true);
    }
}
