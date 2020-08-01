package com.cs446.group7.bruno.spotify;

import android.content.Context;

import com.cs446.group7.bruno.spotify.auth.SpotifyAuthService;
import com.cs446.group7.bruno.spotify.auth.SpotifyAuthServiceImpl;
import com.cs446.group7.bruno.spotify.auth.SpotifyRequestDelegate;
import com.spotify.android.appremote.api.SpotifyAppRemote;

// Initiates singletons for SpotifyPlayerService and SpotifyPlaylistService
public class SpotifyService {

    private SpotifyAuthService authService;
    private SpotifyPlayerService playerService;
    private SpotifyPlaylistService playlistService;


    public SpotifyService(Context context, SpotifyRequestDelegate delegate) {
        authService = new SpotifyAuthServiceImpl(context, delegate);
        playerService = new SpotifyPlayerService();
        playlistService = new SpotifyPlaylistService(context);
    }

    public SpotifyAuthService getAuthService() { return authService; }

    public SpotifyPlayerService getPlayerService() {
        return playerService;
    }

    public SpotifyPlaylistService getPlaylistService() {
        return playlistService;
    }

    public static boolean isSpotifyInstalled(Context context) { return SpotifyAppRemote.isSpotifyInstalled(context); }
}
