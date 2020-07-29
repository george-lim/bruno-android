package com.cs446.group7.bruno.spotify;

import com.cs446.group7.bruno.utils.Callback;
import com.spotify.sdk.android.auth.AuthorizationRequest;

public class SpotifyRequest {

    private AuthorizationRequest request;
    private Callback<String,Void> callback;

    public SpotifyRequest(final AuthorizationRequest request, final Callback<String, Void> callback) {
        this.request = request;
        this.callback = callback;
    }

    public AuthorizationRequest getAuthorizationRequest() { return request; }

    public Callback<String, Void> getCallback() { return callback; }

}
