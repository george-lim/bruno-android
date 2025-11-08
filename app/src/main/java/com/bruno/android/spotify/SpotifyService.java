package com.bruno.android.spotify;

import android.content.Context;

import com.android.volley.DefaultRetryPolicy;
import com.bruno.android.BuildConfig;
import com.bruno.android.spotify.auth.DynamicSpotifyAuthServiceImpl;
import com.bruno.android.spotify.auth.MockSpotifyAuthServiceImpl;
import com.bruno.android.spotify.auth.SpotifyAuthService;
import com.bruno.android.spotify.auth.SpotifyAuthServiceImpl;
import com.bruno.android.spotify.auth.SpotifyRequestDelegate;
import com.bruno.android.spotify.playlist.SpotifyPlaylistService;
import com.spotify.android.appremote.api.SpotifyAppRemote;

// Initiates singletons for SpotifyPlayerService and SpotifyPlaylistService
public class SpotifyService {
    private final SpotifyAuthService authService;
    private final SpotifyPlayerService playerService;
    private final SpotifyPlaylistService playlistService;

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

    public SpotifyService(SpotifyRequestDelegate delegate) {
        authService = BuildConfig.DEBUG
                ? new DynamicSpotifyAuthServiceImpl(
                new SpotifyAuthServiceImpl(delegate, retryPolicy),
                new MockSpotifyAuthServiceImpl()
        )
                : new SpotifyAuthServiceImpl(delegate, retryPolicy);
        playerService = new SpotifyPlayerService();
        playlistService = new SpotifyPlaylistService(retryPolicy);
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
