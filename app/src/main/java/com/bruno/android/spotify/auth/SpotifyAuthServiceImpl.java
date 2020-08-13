package com.bruno.android.spotify.auth;

import android.content.Context;
import android.util.Log;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.bruno.android.MainActivity;
import com.bruno.android.R;
import com.bruno.android.utils.Callback;
import com.spotify.sdk.android.auth.AuthorizationRequest;
import com.spotify.sdk.android.auth.AuthorizationResponse;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class SpotifyAuthServiceImpl implements SpotifyAuthService {

    private final DefaultRetryPolicy retryPolicy;
    private final SpotifyRequestDelegate delegate;
    private final String redirectURI;
    private final String clientID;
    private final String TAG = this.getClass().getSimpleName();

    public SpotifyAuthServiceImpl(final Context context,
                                  final SpotifyRequestDelegate delegate,
                                  final DefaultRetryPolicy retryPolicy) {
        redirectURI = context.getResources().getString(R.string.spotify_redirect_uri);
        clientID = context.getResources().getString(R.string.spotify_client_id);
        this.delegate = delegate;
        this.retryPolicy = retryPolicy;
    }

    @Override
    public void requestUserAuth(final Callback<String, Void> clientCallback) {
        final AuthorizationRequest.Builder builder = new AuthorizationRequest.Builder(clientID,
                AuthorizationResponse.Type.TOKEN, redirectURI);
        builder.setShowDialog(true);
        builder.setScopes(new String[]{"app-remote-control", "playlist-read-private", "user-read-private"});
        final AuthorizationRequest authRequest = builder.build();
        final SpotifyRequest spotifyRequest = new SpotifyRequest(authRequest, clientCallback);
        delegate.handleSpotifyRequest(spotifyRequest);
    }

    @Override
    public void checkIfUserIsPremium(String token, Callback<Boolean, Exception> callback) {
        final StringRequest request = new StringRequest(Request.Method.GET,
                "https://api.spotify.com/v1/me",
                response -> {
                    try {
                        final JSONObject json = new JSONObject(response);
                        final String subscriptionLvl = json.getString("product");
                        callback.onSuccess(subscriptionLvl.equals("premium"));
                    } catch (JSONException e) {
                        Log.e(TAG, "checkIfUserIsPremium: JSON parsing failure: " + e.getMessage());
                        callback.onFailed(e);
                    }
                }, error -> {
            Log.e(TAG, "checkIfUserIsPremium: Error with sending the request: " + error.getMessage());
            callback.onFailed(new Exception(error));
        }) {
            @Override
            public Map<String, String> getHeaders() {
                final Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                headers.put("Authorization", "Bearer " + token);
                return headers;
            }
        };
        request.setTag(TAG);
        request.setRetryPolicy(retryPolicy);
        MainActivity.getVolleyRequestQueue().add(request);
    }


}
