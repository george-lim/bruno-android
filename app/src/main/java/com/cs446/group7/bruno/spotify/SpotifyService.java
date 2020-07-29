package com.cs446.group7.bruno.spotify;

import android.content.Context;

// Initiates singletons for SpotifyPlayerService and SpotifyPlaylistService
public class SpotifyService {

    private SpotifyAuthService authService;
    private SpotifyPlayerService playerService;
    private SpotifyPlaylistService playlistService;


    public SpotifyService(Context context, SpotifyRequestDelegate delegate) {
        authService = new SpotifyAuthService(context, delegate);
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
}
