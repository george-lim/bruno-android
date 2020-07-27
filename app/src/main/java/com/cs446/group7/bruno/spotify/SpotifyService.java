package com.cs446.group7.bruno.spotify;

import android.content.Context;

// Initiates singletons for SpotifyPlayerService and SpotifyPlaylistService
public class SpotifyService {

    private SpotifyPlayerService playerService;
    private SpotifyPlaylistService playlistService;

    public SpotifyService(Context context) {
        playerService = new SpotifyPlayerService();
        playlistService = new SpotifyPlaylistService(context);
    }

    public SpotifyPlayerService getPlayerService() {
        return playerService;
    }

    public SpotifyPlaylistService getPlaylistService() {
        return playlistService;
    }
}
