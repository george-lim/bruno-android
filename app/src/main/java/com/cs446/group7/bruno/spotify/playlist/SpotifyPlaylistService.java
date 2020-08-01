package com.cs446.group7.bruno.spotify.playlist;

import android.content.Context;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.cs446.group7.bruno.MainActivity;
import com.cs446.group7.bruno.R;
import com.cs446.group7.bruno.music.BrunoPlaylist;
import com.cs446.group7.bruno.music.BrunoTrack;
import com.cs446.group7.bruno.music.playlist.PlaylistGenerator;
import com.cs446.group7.bruno.music.playlist.PlaylistInfo;
import com.cs446.group7.bruno.utils.Callback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// Communicates with the Spotify Web API through HTTP
// Uses Volley, an HTTP library: https://developer.android.com/training/volley
// Could share the request queue between this and RouteGenerator - can turn it into a singleton
public class SpotifyPlaylistService implements PlaylistGenerator {

    private final String playlistEndpoint = "https://api.spotify.com/v1/playlists/";
    private final String authorizationEndpoint = "https://accounts.spotify.com/api/token";
    private final String clientId;
    private final String clientSecret;
    private final String TAG = this.getClass().getSimpleName();
    // Default is 2500 MS
    private static final int REQUEST_TIMEOUT_MS = DefaultRetryPolicy.DEFAULT_TIMEOUT_MS;
    // Default is 1 retry, but we use 5 instead
    private static final int REQUEST_MAX_RETRIES = 5;
    // Default is 1f (i.e. first request waits 2500MS, the next request waits 5000MS, etc...)
    private static final float REQUEST_BACKOFF_MULT = DefaultRetryPolicy.DEFAULT_BACKOFF_MULT;
    // Could be expanded to use different playlists
    private static final String DEFAULT_PLAYLIST_ID = "6QAKnenuZoowNqxRzZbeRg";

    // Needs context for secret variables
    public SpotifyPlaylistService(final Context context) {
        clientId = context.getResources().getString(R.string.spotify_client_id);
        clientSecret = context.getResources().getString(R.string.spotify_client_secret);
    }

    // Call this to get the BrunoPlaylist - it goes through authentication and then the playlist
    // endpoint to provide the BrunoPlaylist requested by callback
    // All failures are sent back through callback.onFailed
    // Needs internet access to succeed, since it uses API calls
    public void getPlaylist(final Callback<BrunoPlaylist, Exception> callback) {
        getAuthorizationToken(new Callback<String, Exception>() {
            @Override
            public void onSuccess(String authToken) {
                getPlaylistResponse(authToken, DEFAULT_PLAYLIST_ID, callback);
            }

            @Override
            public void onFailed(Exception result) {
                callback.onFailed(result);
            }
        });
    }

    // Retrieves a list of the user's playlists which they can select from for their fallback playlist
    // NOTE: The tracks within the playlists will not be retrieved, since it is not used during playlist selection
    public void getUserPlaylists(final String accessToken, final Callback<List<PlaylistInfo>, Exception> callback) {
        final StringRequest stringRequest = new StringRequest(Request.Method.GET,
                "https://api.spotify.com/v1/me/playlists",
                response -> {
                    try {
                        final JSONObject pagingObject = new JSONObject(response);
                        parsePagingObject(accessToken, new ArrayList<>(), pagingObject, new PlaylistParser(), callback);
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

    // In order to use the Spotify API, an authorization token needs to be retrieved from Spotify
    // Using the client id and client secret, we can retrieve this token first before using the playlist endpoint
    private void getAuthorizationToken(final Callback<String, Exception> callback) {
        final StringRequest authRequest = new StringRequest(Request.Method.POST, authorizationEndpoint,
                response -> {
                    try {
                        final JSONObject responseJson = new JSONObject(response);
                        callback.onSuccess(responseJson.getString("access_token"));
                    } catch (JSONException e) {
                        Log.e(TAG, "getAuthorizationToken: JSON parsing failure: " + e.getMessage());
                        callback.onFailed(e);
                    }
                }, error -> {
            Log.e(TAG, "getAuthorizationToken: Error with sending the request: " + error.getMessage());
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
                body += "&client_id=" + clientId;
                body += "&client_secret=" + clientSecret;
                final String requestBody = body;
                try {
                    return requestBody == null ? null : requestBody.getBytes("utf-8");
                } catch (UnsupportedEncodingException e) {
                    Log.e(TAG, "getAuthorizationToken: Failed to encode request body: " + e.getMessage());
                    callback.onFailed(e);
                    return null;
                }
            }
        };
        addRetryAndSendRequest(authRequest);
    }

    // With the authorization token, we can use the playlist API to retrieve a JSON representation
    // of the playlist. This gets parsed in BrunoPlaylist.getPlaylistFromJSON(), and returned to
    // the callback.
    private void getPlaylistResponse(final String token,
                                     final String playlistId,
                                     final Callback<BrunoPlaylist, Exception> callback) {
        final StringRequest stringRequest = new StringRequest(Request.Method.GET,
                playlistEndpoint + playlistId,
                response -> {
                    try {
                        final JSONObject responseJson = new JSONObject(response);
                        final String outputPlaylistName = responseJson.getString("name");
                        final String outputDescription = responseJson.getString("description");
                        final JSONObject pagingObject = responseJson.getJSONObject("tracks");
                        parsePagingObject(token, new ArrayList<>(), pagingObject, new TrackParser(),
                                new Callback<List<BrunoTrack>, Exception>() {
                                    @Override
                                    public void onSuccess(List<BrunoTrack> result) {
                                        final BrunoPlaylist outputPlaylist = new BrunoPlaylist(playlistId, outputPlaylistName,
                                                outputDescription, result);
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
                final Map<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                headers.put("Authorization", "Bearer " + token);
                return headers;
            }
        };
        addRetryAndSendRequest(stringRequest);
    }

    private void addRetryAndSendRequest(StringRequest request) {
        request.setTag(TAG);
        request.setRetryPolicy(new DefaultRetryPolicy(
                this.REQUEST_TIMEOUT_MS,
                this.REQUEST_MAX_RETRIES,
                this.REQUEST_BACKOFF_MULT
        ));

        MainActivity.getVolleyRequestQueue().add(request);
    }

    private <T> void parsePagingObject(String token, List<T> output, JSONObject pagingObj, SpotifyParser parser,
                                       final Callback<List<T>, Exception> callback) {
        try {
            List<T> pagingResult = parser.parsePagingItems(pagingObj.getJSONArray("items"));
            output.addAll(pagingResult);

            String nextUrl = pagingObj.getString("next");
            if (nextUrl != "null") {
                getPagingObject(token, nextUrl, new Callback<JSONObject, Exception>() {
                    @Override
                    public void onSuccess(JSONObject result) {
                        parsePagingObject(token, output, result, parser, callback);
                    }

                    @Override
                    public void onFailed(Exception result) {
                        callback.onFailed(result);
                    }
                });
            } else {
                callback.onSuccess(output);
            }
        } catch (JSONException e) {
            callback.onFailed(e);
        }
    }

    private void getPagingObject(final String token, final String url, final Callback<JSONObject, Exception> callback) {
        final StringRequest stringRequest = new StringRequest(Request.Method.GET,
                url,
                response -> {
                    try {
                        final JSONObject pagingJson = new JSONObject(response);
                        callback.onSuccess(pagingJson);
                    } catch (JSONException e) {
                        Log.e(TAG, "getPagingObject: JSON parsing failure: " + e.getMessage());
                        callback.onFailed(e);
                    }
                }, error -> {
            Log.e(TAG, "getPagingObject: Error with sending the request: " + error.getMessage());
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
}
