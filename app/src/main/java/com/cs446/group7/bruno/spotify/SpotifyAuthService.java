package com.cs446.group7.bruno.spotify;

import android.content.Context;

import com.cs446.group7.bruno.R;
import com.cs446.group7.bruno.utils.Callback;
import com.spotify.sdk.android.auth.AuthorizationRequest;
import com.spotify.sdk.android.auth.AuthorizationResponse;

public class SpotifyAuthService {

    private SpotifyRequestDelegate delegate;
    private final String redirectURI;
    private final String clientID;

    public SpotifyAuthService(Context context, SpotifyRequestDelegate delegate) {
        redirectURI = context.getResources().getString(R.string.spotify_redirect_uri);
        clientID = context.getResources().getString(R.string.spotify_client_id);
        this.delegate = delegate;
    }

    public void requestSpotifyAuth(Context context, final Callback<String, Void> clientCallback) {

        AuthorizationRequest.Builder builder = new AuthorizationRequest.Builder(
                clientID, AuthorizationResponse.Type.TOKEN, redirectURI);
        builder.setShowDialog(true);
        builder.setScopes(new String[]{"app-remote-control", "playlist-read-private"});
        AuthorizationRequest authRequest = builder.build();
        SpotifyRequest spotifyRequest = new SpotifyRequest(authRequest, clientCallback);

        delegate.handleSpotifyRequest(spotifyRequest);
    }


}
