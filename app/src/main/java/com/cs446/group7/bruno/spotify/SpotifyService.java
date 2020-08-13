package com.cs446.group7.bruno.spotify;

import android.content.Context;

import com.android.volley.DefaultRetryPolicy;
import com.cs446.group7.bruno.BuildConfig;
import com.cs446.group7.bruno.spotify.auth.DynamicSpotifyAuthServiceImpl;
import com.cs446.group7.bruno.spotify.auth.MockSpotifyAuthServiceImpl;
import com.cs446.group7.bruno.spotify.auth.SpotifyAuthService;
import com.cs446.group7.bruno.spotify.auth.SpotifyAuthServiceImpl;
import com.cs446.group7.bruno.spotify.auth.SpotifyRequestDelegate;
import com.cs446.group7.bruno.spotify.playlist.SpotifyPlaylistService;
import com.spotify.android.appremote.api.SpotifyAppRemote;

// Initiates singletons for SpotifyPlayerService and SpotifyPlaylistService
public class SpotifyService {
    private SpotifyAuthService authService;
    private SpotifyPlayerService playerService;
    private SpotifyPlaylistService playlistService;

    // Default is 2500 MS
    private static final int REQUEST_TIMEOUT_MS = DefaultRetryPolicy.DEFAULT_TIMEOUT_MS;
    // Default is 1 retry, but we use 5 instead
    private static final int REQUEST_MAX_RETRIES = 5;
    // Default is 1f (i.e. first request waits 2500MS, the next request waits 5000MS, etc...)
    private static final float REQUEST_BACKOFF_MULT = DefaultRetryPolicy.DEFAULT_BACKOFF_MULT;
    private static final DefaultRetryPolicy retryPolicy = new DefaultRetryPolicy(
            REQUEST_TIMEOUT_MS,
            REQUEST_MAX_RETRIES,
            REQUEST_BACKOFF_MULT
    );

    public SpotifyService(Context context, SpotifyRequestDelegate delegate) {
        authService = BuildConfig.DEBUG
                ? new DynamicSpotifyAuthServiceImpl(
                        new SpotifyAuthServiceImpl(context, delegate, retryPolicy),
                        new MockSpotifyAuthServiceImpl()
                )
                : new SpotifyAuthServiceImpl(context, delegate, retryPolicy);
        playerService = new SpotifyPlayerService();
        playlistService = new SpotifyPlaylistService(context, retryPolicy);
    }

    public SpotifyAuthService getAuthService() {
        return authService;
    }

    public SpotifyPlayerService getPlayerService() {
        return playerService;
    }

    public SpotifyPlaylistService getPlaylistService() {
        return playlistService;
    }

    public static boolean isSpotifyInstalled(Context context) {
        return BuildConfig.DEBUG || SpotifyAppRemote.isSpotifyInstalled(context);
    }
}
