package com.cs446.group7.bruno.spotify;

import android.content.Context;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.cs446.group7.bruno.R;
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

    private final RequestQueue requestQueue;
    private final String playlistEndpoint = "https://api.spotify.com/v1/playlists/";
    private final String authorizationEndpoint = "https://accounts.spotify.com/api/token";
    private final String clientId;
    private final String clientSecret;
    public final String TAG = this.getClass().getSimpleName();

    // Needs context for secret variables
    public SpotifyPlaylistService(Context context) {
        requestQueue = Volley.newRequestQueue(context);
        clientId = context.getResources().getString(R.string.spotify_client_id);
        clientSecret = context.getResources().getString(R.string.spotify_client_secret);
    }

    // Call this to get the BrunoPlaylist - it goes through authentication and then the playlist
    // endpoint to provide the BrunoPlaylist requested by callback
    // All failures are sent back through callback.onFailed
    // Needs internet access to succeed, since it uses API calls
    public void getPlaylist(String playlistId, Callback<BrunoPlaylist, Exception> callback) {
        getAuthorizationToken(new Callback<String, Exception>() {
            @Override
            public void onSuccess(String authToken) {
                getPlaylistResponse(authToken, playlistId, callback);
            }

            @Override
            public void onFailed(Exception result) {
                callback.onFailed(result);
            }
        });
    }

    // In order to use the Spotify API, an authorization token needs to be retrieved from Spotify
    // Using the client id and client secret, we can retrieve this token first before using the playlist endpoint
    private void getAuthorizationToken(Callback<String, Exception> callback) {
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
        requestQueue.add(authRequest);
    }

    // With the authorization token, we can use the playlist API to retrieve a JSON representation
    // of the playlist. This gets parsed in BrunoPlaylist.getPlaylistFromJSON(), and returned to
    // the callback.
    private void getPlaylistResponse(String authToken, String playlistId, Callback<BrunoPlaylist, Exception> callback) {
        final StringRequest stringRequest = new StringRequest(Request.Method.GET,
        playlistEndpoint + playlistId,
                response -> {
                    try {
                        final JSONObject responseJson = new JSONObject(response);
                        final BrunoPlaylist playlist = getPlaylistFromJSON(responseJson);
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
        requestQueue.add(stringRequest);
    }

    // Parses a BrunoPlaylist by reading a response JSON from Spotify's Playlist endpoint
    private BrunoPlaylist getPlaylistFromJSON(JSONObject responseJson) throws JSONException {
        final String outputPlaylistName = responseJson.getString("name");
        final String outputDescription = responseJson.getString("description");
        final JSONObject pagingObject = responseJson.getJSONObject("tracks");

        final int outputTotalTracks = pagingObject.getInt("total");
        final JSONArray responseTracks = pagingObject.getJSONArray("items");
        long outputPlaylistDuration = 0;
        final List<BrunoTrack> outputTracks = new ArrayList<BrunoTrack>();

        // Iterate through the tracks
        for (int i = 0; i < outputTotalTracks; ++i) {
            final JSONObject responseTrack = responseTracks.getJSONObject(i).getJSONObject("track");
            final String outputAlbum = responseTrack.getJSONObject("album").getString("name");

            final ArrayList<String> outputArtists = new ArrayList<String>();
            final JSONArray responseArtists = responseTrack.getJSONArray("artists");

            // Iterate through the artists of each track
            for (int j = 0; j < responseArtists.length(); ++j) {
                outputArtists.add(responseArtists.getJSONObject(j).getString("name"));
            }

            // implicit int to long conversion - harmless
            final long outputDuration = responseTrack.getInt("duration_ms");
            outputPlaylistDuration += outputDuration;
            String outputTrackName = responseTrack.getString("name");

            BrunoTrack currentTrack = new BrunoTrack(outputTrackName, outputAlbum,
                    outputDuration, outputArtists);
            outputTracks.add(currentTrack);
        }

        final BrunoPlaylist outputPlaylist = new BrunoPlaylist(outputPlaylistName, outputDescription,
                outputTotalTracks, outputPlaylistDuration, outputTracks);
        return outputPlaylist;
    }
}