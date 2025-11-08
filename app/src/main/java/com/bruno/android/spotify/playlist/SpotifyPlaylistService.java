package com.bruno.android.spotify.playlist;

import android.util.Log;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.bruno.android.BuildConfig;
import com.bruno.android.MainActivity;
import com.bruno.android.music.BrunoPlaylist;
import com.bruno.android.music.BrunoPlaylistImpl;
import com.bruno.android.music.BrunoTrack;
import com.bruno.android.music.playlist.PlaylistGenerator;
import com.bruno.android.music.playlist.PlaylistMetadata;
import com.bruno.android.utils.Callback;

import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

// Communicates with the Spotify Web API through HTTP
// Uses Volley, an HTTP library: https://developer.android.com/training/volley
public class SpotifyPlaylistService implements PlaylistGenerator, SpotifyPlaylistAPI {

    private final DefaultRetryPolicy retryPolicy;
    private final String TAG = this.getClass().getSimpleName();

    private static final String[] PLAYLIST_IDS = {
            "0Ljf1rAruX3SVPkoSN0rlG",
            "1wqLZv3iGUj70RiGKA2yXC",
            "0DCxpWUBg9ArgKDe34l0iA",
            "00jy0g2ZnCxiCfhEiwCzY6"
    };

    // Needs context for secret variables
    public SpotifyPlaylistService(final DefaultRetryPolicy retryPolicy) {
        this.retryPolicy = retryPolicy;
    }

    // Call this to get the BrunoPlaylist - it goes through public authentication and then the playlist
    // endpoint to provide the BrunoPlaylist requested by callback
    public void discoverPlaylist(final Callback<BrunoPlaylist, Exception> callback) {
        getPublicAuthorizationToken(new Callback<>() {
            @Override
            public void onSuccess(String authToken) {
                getPlaylist(authToken, getRandomPlaylistId(), callback);
            }

            @Override
            public void onFailed(Exception result) {
                callback.onFailed(result);
            }
        });
    }

    // Retrieves a list of the user's playlists which they can select from for their fallback playlist
    // Requires an access token which is capable of retrieving private playlist information
    public void getUserPlaylistLibrary(final String accessToken, final Callback<List<PlaylistMetadata>, Exception> callback) {
        final StringRequest stringRequest = new StringRequest(Request.Method.GET,
                "https://api.spotify.com/v1/me/playlists",
                response -> {
                    try {
                        final JSONObject pagingObject = new JSONObject(response);
                        new PlaylistPagingObjectParser(accessToken, retryPolicy).parsePagingObject(pagingObject, callback);
                    } catch (JSONException e) {
                        Log.e(TAG, "getUserPlaylists: JSON parsing failure: " + e.getMessage());
                        callback.onFailed(e);
                    }
                }, error -> {
            Log.e(TAG, "getUserPlaylists: Error with sending the request: " + error.getMessage());
            callback.onFailed(new Exception(error));
        }) {
            @Override
            public Map<String, String> getHeaders() {
                final Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                headers.put("Authorization", "Bearer " + accessToken);
                return headers;
            }
        };
        addRetryAndSendRequest(stringRequest);
    }

    // In order to use the Spotify API, an authorization token needs to be retrieved from Spotify using our
    // application credentials. Using the client id and client secret, we can retrieve this token first before using
    // the playlist endpoint.
    private void getPublicAuthorizationToken(final Callback<String, Exception> callback) {
        final StringRequest authRequest;
        String authorizationEndpoint = "https://accounts.spotify.com/api/token";
        authRequest = new StringRequest(Request.Method.POST, authorizationEndpoint,
                response -> {
                    try {
                        final JSONObject responseJson = new JSONObject(response);
                        callback.onSuccess(responseJson.getString("access_token"));
                    } catch (JSONException e) {
                        Log.e(TAG, "getPublicAuthorizationToken: JSON parsing failure: " + e.getMessage());
                        callback.onFailed(e);
                    }
                }, error -> {
            Log.e(TAG, "getPublicAuthorizationToken: Error with sending the request: " + error.getMessage());
            callback.onFailed(new Exception(error));
        }) {
            @Override
            public String getBodyContentType() {
                return "application/x-www-form-urlencoded";
            }

            @Override
            public byte[] getBody() {
                String body = "";
                body += "grant_type=client_credentials";
                body += "&client_id=" + BuildConfig.SPOTIFY_CLIENT_ID;
                body += "&client_secret=" + BuildConfig.SPOTIFY_CLIENT_SECRET;
                return body.getBytes(StandardCharsets.UTF_8);
            }
        };
        addRetryAndSendRequest(authRequest);
    }

    // Returns a playlist using Spotify's playlist endpoint using the token and playlist id. The token should have
    // sufficient credentials to access the playlist. (e.g. a public access token is not capable of retrieving the
    // user's private playlists)
    public void getPlaylist(final String token,
                            final String playlistId,
                            final Callback<BrunoPlaylist, Exception> callback) {
        String playlistEndpoint = "https://api.spotify.com/v1/playlists/";
        final StringRequest stringRequest = new StringRequest(Request.Method.GET,
                playlistEndpoint + playlistId,
                response -> {
                    try {
                        final JSONObject responseJson = new JSONObject(response);
                        final String outputPlaylistName = responseJson.getString("name");
                        final JSONObject pagingObject = responseJson.getJSONObject("tracks");
                        new TrackPagingObjectParser(token, retryPolicy)
                                .parsePagingObject(pagingObject, new Callback<>() {
                                    @Override
                                    public void onSuccess(List<BrunoTrack> result) {
                                        final BrunoPlaylist outputPlaylist = new BrunoPlaylistImpl(playlistId,
                                                outputPlaylistName, result);
                                        callback.onSuccess(outputPlaylist);
                                    }

                                    @Override
                                    public void onFailed(Exception result) {
                                        callback.onFailed(result);
                                    }
                                });
                    } catch (JSONException e) {
                        Log.e(TAG, "getPlaylistResponse: JSON parsing failure: " + e.getMessage());
                        callback.onFailed(e);
                    }
                }, error -> {
            Log.e(TAG, "getPlaylistResponse: Error with sending the request: " + error.getMessage());
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
        addRetryAndSendRequest(stringRequest);
    }

    private void addRetryAndSendRequest(StringRequest request) {
        request.setTag(TAG);
        request.setRetryPolicy(retryPolicy);
        MainActivity.getVolleyRequestQueue().add(request);
    }

    private static String getRandomPlaylistId() {
        return PLAYLIST_IDS[new Random().nextInt(PLAYLIST_IDS.length)];
    }
}
