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

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

// Communicates with the Spotify Web API through HTTP
// Uses Volley, an HTTP library: https://developer.android.com/training/volley
// Could share the request queue between this and RouteGenerator - can turn it into a singleton
public class SpotifyWebAPI {

    RequestQueue requestQueue;
    // Hard coded to a specific playlist - same as the one in SpotifyService.playMusic()
    String playlistEndpoint = "https://api.spotify.com/v1/playlists/7fPwZk4KFD2yfU7J5O1JVz";
    String authorizationEndpoint = "https://accounts.spotify.com/api/token";
    String clientId;
    String clientSecret;

    public SpotifyWebAPI(Context context) {
        requestQueue = Volley.newRequestQueue(context);
        clientId = context.getResources().getString(R.string.spotify_client_id);
        clientSecret = context.getResources().getString(R.string.spotify_client_secret);
    }

    // Call this to get the BrunoPlaylist - it goes through the sequence necessary to provide
    // the BrunoPlaylist requested by callback
    // All failures are sent back through callback.onPlaylistError()
    public void getPlaylist(OnPlaylistCallback callback) {
        getAuthorizationToken(callback);
    }

    // In order to use the Spotify API, an authorization token needs to be retrieved from Spotify
    // Using the client id and client secret, we can retrieve this token first before using the API
    // Calls getPlaylistResponse upon success
    private void getAuthorizationToken(OnPlaylistCallback callback) {
        StringRequest authRequest = new StringRequest(Request.Method.POST, authorizationEndpoint,
                new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject responseJson = new JSONObject(response);
                    getPlaylistResponse(callback, responseJson.getString("access_token"));
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
        requestQueue.add(authRequest);
    }

    // With the authorization token, we can use the playlist API to retrieve a JSON representation
    // of the playlist. This gets parsed in BrunoPlaylist.getPlaylistFromJson, and returned to
    // the callback.
    private void getPlaylistResponse(OnPlaylistCallback callback, String authToken) {
        StringRequest stringRequest = new StringRequest(Request.Method.GET, playlistEndpoint,
            new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        JSONObject responseJson = new JSONObject(response);
                        BrunoPlaylist playlist = BrunoPlaylist.getPlaylistFromJSON(responseJson);
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
                Map<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                headers.put("Authorization", "Bearer " + authToken);
                return headers;
            }
        };

        requestQueue.add(stringRequest);
    }

}
