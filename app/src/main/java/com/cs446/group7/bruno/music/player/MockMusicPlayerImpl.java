package com.cs446.group7.bruno.music.player;

import android.content.Context;
import android.util.Log;

import com.cs446.group7.bruno.music.BrunoPlaylist;
import com.cs446.group7.bruno.music.BrunoTrack;
import com.cs446.group7.bruno.utils.Callback;

import java.util.ArrayList;

public class MockMusicPlayerImpl implements MusicPlayer {

    BrunoPlaylist playlist;
    private final String TAG = getClass().getSimpleName();
    boolean connected = false;

    public void connect(final Context context,
                        final Callback<Void, MusicPlayerError> callback) {
        Log.i(TAG, "connect(): Connected");
        connected = true;
        callback.onSuccess(null);
    }

    public void disconnect() {
        connected = false;
        Log.i(TAG, "disconnected(): Disconnected");
    }

    // Simulating player state change on subscription for demoing purposes
    // Normally, Spotify itself will have a player state change and notify subscribers about the
    // current track
    public void addSubscriber(final MusicPlayerSubscriber subscriber) {
    }

    public void removeSubscriber(final MusicPlayerSubscriber subscriber) { }

    public void setPlayerPlaylist(BrunoPlaylist playlist) {
        this.playlist = playlist;
    }

    public void play(Callback<Void, Exception> callback) {
        Log.i(TAG, "play(): Playing playlist " + this.playlist.name);
        callback.onSuccess(null);
    }

    public void stop(Callback<Void, Exception> callback) {
        Log.i(TAG, "stop(): Stopped playlist " + this.playlist.name);
        callback.onSuccess(null);
    }
}
