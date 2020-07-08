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
import com.cs446.group7.bruno.music.OnPlaylistCallback;
import com.cs446.group7.bruno.music.PlaylistGenerator;

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
public class SpotifyPlaylistService extends PlaylistGenerator {

    final RequestQueue requestQueue;
    // Hard coded to a specific playlist - same as the one in SpotifyService.playMusic()
    final String playlistEndpoint = "https://api.spotify.com/v1/playlists/";
    final String authorizationEndpoint = "https://accounts.spotify.com/api/token";
    final String clientId;
    final String clientSecret;
    final public String TAG = this.getClass().getSimpleName();

    public SpotifyPlaylistService(Context context) {
        requestQueue = Volley.newRequestQueue(context);
        clientId = context.getResources().getString(R.string.spotify_client_id);
        clientSecret = context.getResources().getString(R.string.spotify_client_secret);
    }

    // Call this to get the BrunoPlaylist - it goes through the sequence necessary to provide
    // the BrunoPlaylist requested by callback
    // All failures are sent back through callback.onPlaylistError()
    public void getPlaylist(OnPlaylistCallback callback, String playlistId) {
        getAuthorizationToken(callback, playlistId);
    }

    // In order to use the Spotify API, an authorization token needs to be retrieved from Spotify
    // Using the client id and client secret, we can retrieve this token first before using the API
    // Calls getPlaylistResponse upon success
    private void getAuthorizationToken(OnPlaylistCallback callback, String playlistId) {
        final StringRequest authRequest = new StringRequest(Request.Method.POST, authorizationEndpoint,
                new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    final JSONObject responseJson = new JSONObject(response);
                    getPlaylistResponse(callback,
                            responseJson.getString("access_token"), playlistId);
                } catch (JSONException e) {
                    callback.onPlaylistError(e);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("AUTH ERROR: ", error.toString());
            }
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
                    callback.onPlaylistError(e);
                    return null;
                }
            }
        };
        authRequest.setTag(TAG);
        requestQueue.add(authRequest);
    }

    // With the authorization token, we can use the playlist API to retrieve a JSON representation
    // of the playlist. This gets parsed in BrunoPlaylist.getPlaylistFromJson, and returned to
    // the callback.
    private void getPlaylistResponse(OnPlaylistCallback callback, String authToken, String playlistId) {
        final StringRequest stringRequest = new StringRequest(Request.Method.GET,
        playlistEndpoint + playlistId,
            new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        final JSONObject responseJson = new JSONObject(response);
                        final BrunoPlaylist playlist = getPlaylistFromJSON(responseJson);
                        callback.onPlaylistReady(playlist);
                    } catch (JSONException e) {
                        callback.onPlaylistError(e);
                    }
                }
            }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("BAD",error.toString());
            }
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
