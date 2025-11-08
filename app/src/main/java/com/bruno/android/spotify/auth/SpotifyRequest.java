package com.bruno.android.spotify.auth;

import com.bruno.android.utils.Callback;
import com.spotify.sdk.android.auth.AuthorizationRequest;

public class SpotifyRequest {

    private final AuthorizationRequest request;
    private final Callback<String, Void> callback;

    public SpotifyRequest(final AuthorizationRequest request, final Callback<String, Void> callback) {
        this.request = request;
        this.callback = callback;
    }

    public AuthorizationRequest getAuthorizationRequest() {
        return request;
    }

    public Callback<String, Void> getCallback() {
        return callback;
    }

}
