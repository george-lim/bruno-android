package com.cs446.group7.bruno.spotify;

import android.content.Context;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.cs446.group7.bruno.MainActivity;
import com.cs446.group7.bruno.R;
import com.cs446.group7.bruno.music.BrunoPlaylistImpl;
import com.cs446.group7.bruno.music.BrunoPlaylist;
import com.cs446.group7.bruno.music.BrunoTrack;
import com.cs446.group7.bruno.music.playlist.PlaylistGenerator;
import com.cs446.group7.bruno.utils.Callback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// Communicates with the Spotify Web API through HTTP
// Uses Volley, an HTTP library: https://developer.android.com/training/volley
// Could share the request queue between this and RouteGenerator - can turn it into a singleton
class SpotifyPlaylistService implements PlaylistGenerator {

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
    private static final String DEFAULT_PLAYLIST_ID = "27q9PVUOHGeSJlz6jSgt2f";

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
                })
        {
            @Override
            public String getBodyContentType() {
                return "application/x-www-form-urlencoded";
            }

            @Override
            public byte[] getBody() throws AuthFailureError {
                String body = "";
                body += "grant_type=client_credentials";
                body += "&client_id=" + clientId;
                body += "&client_secret=" + clientSecret;
                final String requestBody = body.toString();
                try {
                    return requestBody == null ? null : requestBody.getBytes("utf-8");
                } catch (UnsupportedEncodingException e) {
                    Log.e(TAG, "getAuthorizationToken: Failed to encode request body: " + e.getMessage());
                    callback.onFailed(e);
                    return null;
                }
            }
        };

        authRequest.setTag(TAG);
        authRequest.setRetryPolicy(new DefaultRetryPolicy(
                this.REQUEST_TIMEOUT_MS,
                this.REQUEST_MAX_RETRIES,
                this.REQUEST_BACKOFF_MULT
        ));

        MainActivity.getVolleyRequestQueue().add(authRequest);
    }

    // With the authorization token, we can use the playlist API to retrieve a JSON representation
    // of the playlist. This gets parsed in BrunoPlaylist.getPlaylistFromJSON(), and returned to
    // the callback.
    private void getPlaylistResponse(final String authToken,
                                     final String playlistId,
                                     final Callback<BrunoPlaylist, Exception> callback) {
        final StringRequest stringRequest = new StringRequest(Request.Method.GET,
        playlistEndpoint + playlistId,
                response -> {
                    try {
                        final JSONObject responseJson = new JSONObject(response);
                        final BrunoPlaylist playlist = getPlaylistFromJSON(responseJson, playlistId);
                        callback.onSuccess(playlist);
                    } catch (JSONException e) {
                        Log.e(TAG, "getPlaylistResponse: JSON parsing failure: " + e.getMessage());
                        callback.onFailed(e);
                    }
                }, error -> {
                    Log.e(TAG, "getPlaylistResponse: Error with sending the request: " + error.getMessage());
                    callback.onFailed(new Exception(error));
                })
        {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                final Map<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                headers.put("Authorization", "Bearer " + authToken);
                return headers;
            }
        };

        stringRequest.setTag(TAG);
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                this.REQUEST_TIMEOUT_MS,
                this.REQUEST_MAX_RETRIES,
                this.REQUEST_BACKOFF_MULT
        ));

        MainActivity.getVolleyRequestQueue().add(stringRequest);
    }

    // Parses a BrunoPlaylist by reading a response JSON from Spotify's Playlist endpoint
    private BrunoPlaylist getPlaylistFromJSON(final JSONObject responseJson,
                                              final String playlistId) throws JSONException {
        final String outputPlaylistName = responseJson.getString("name");
        final JSONObject pagingObject = responseJson.getJSONObject("tracks");

        final int outputTotalTracks = pagingObject.getInt("total");
        final JSONArray responseTracks = pagingObject.getJSONArray("items");
        final List<BrunoTrack> outputTracks = new ArrayList<>();

        // Iterate through the tracks
        for (int i = 0; i < outputTotalTracks; ++i) {
            final JSONObject responseTrack = responseTracks.getJSONObject(i).getJSONObject("track");
            final String outputAlbum = responseTrack.getJSONObject("album").getString("name");

            // implicit int to long conversion - harmless
            final long outputDuration = responseTrack.getInt("duration_ms");
            String outputTrackName = responseTrack.getString("name");

            BrunoTrack currentTrack = new BrunoTrack(outputTrackName, outputAlbum, outputDuration);
            outputTracks.add(currentTrack);
        }

        return new BrunoPlaylistImpl(playlistId, outputPlaylistName, outputTracks);
    }
}
