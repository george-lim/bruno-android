package com.cs446.group7.bruno.spotify;

import android.content.Context;

import com.cs446.group7.bruno.music.BrunoPlaylist;
import com.cs446.group7.bruno.music.BrunoTrack;
import com.cs446.group7.bruno.utils.Callback;
import com.spotify.android.appremote.api.SpotifyAppRemote;

// Acts as the main interface to Spotify
// For now, it just forwards requests of the same method to the appropriate player/playlist service
public class SpotifyService {

    private SpotifyPlayerService playerService;
    private SpotifyPlaylistService playlistService;

    public SpotifyService(Context context) {
        playerService = new SpotifyPlayerService();
        playlistService = new SpotifyPlaylistService(context);
    }

    public void connect(final Context context, final Callback<Void, SpotifyServiceError> callback) {
        playerService.connect(context, callback);
    }

    public boolean isConnected() {
        return playerService.isConnected();
    }

    public void addSubscriber(final SpotifyServiceSubscriber subscriber) {
        playerService.addSubscriber(subscriber);
    }

    public void removeSubscriber(final SpotifyServiceSubscriber subscriber) {
        playerService.removeSubscriber(subscriber);
    }

    public void disconnect() {
        playerService.disconnect();
    }

    public void play(Callback<Void, Exception> callback) {
        playerService.play(callback);
    }

    public void setPlayerPlaylist(String playlistId) {
        playerService.setPlayerPlaylist(playlistId);
    }

    public void pause(Callback<Void, Exception> callback) {
        playerService.pause(callback);
    }

    public void resume(Callback<Void, Exception> callback) {
        playerService.resume(callback);
    }

    public BrunoTrack getCurrentTrack() {
        return playerService.getCurrentTrack();
    }

    // Only call which uses SpotifyPlaylistService
    public void getPlaylist(String playlistId, Callback<BrunoPlaylist, Exception> callback) {
        playlistService.getPlaylist(playlistId, callback);
    }

}

