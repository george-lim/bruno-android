package com.cs446.group7.bruno.music.player;

import android.content.Context;
import android.util.Log;

import com.cs446.group7.bruno.music.BrunoTrack;
import com.cs446.group7.bruno.utils.Callback;

import java.util.ArrayList;

public class MockMusicPlayerImpl implements MusicPlayer {

    String playlistId = "";
    private final String TAG = getClass().getSimpleName();
    boolean connected = false;

    public void connect(Context context,
                 Callback<Void, MusicPlayerError> callback) {
        Log.i(TAG, "connect(): Connected");
        connected = true;
        callback.onSuccess(null);
    }

    public boolean isConnected() {
        return connected;
    }

    public void disconnect() {
        connected = false;
        Log.i(TAG, "disconnected(): Disconnected");
    }

    // Simulating player state change on subscription for demoing purposes
    // Normally, Spotify itself will have a player state change and notify subscribers about the
    // current track
    public void addSubscriber(final MusicPlayerSubscriber subscriber) {
        subscriber.onTrackChanged(getCurrentTrack());
    }

    public void removeSubscriber(final MusicPlayerSubscriber subscriber) { }

    public void setPlayerPlaylist(String playlistId) {
        this.playlistId = playlistId;
    }

    public void play(Callback<Void, Exception> callback) {
        Log.i(TAG, "play(): Playing playlist " + this.playlistId);
        callback.onSuccess(null);
    }

    public void pause(Callback<Void, Exception> callback) {
        Log.i(TAG, "pause(): Paused playlist " + this.playlistId);
        callback.onSuccess(null);
    }

    public void resume(Callback<Void, Exception> callback) {
        Log.i(TAG, "resume(): Resuming playlist " + this.playlistId);
        callback.onSuccess(null);
    }

    // Making this match the first track of MockPlaylistImpl
    public BrunoTrack getCurrentTrack() {

        long threeMinutes = 180000; // Milliseconds
        ArrayList<String> artists = new ArrayList<String>(2);
        artists.add("Jimin");
        artists.add("Taylor Swift");
        BrunoTrack track = new BrunoTrack("name0", "album0", threeMinutes, artists);
        Log.i(TAG, "getCurrentTrack(): Name: " + track.name);
        Log.i(TAG, "getCurrentTrack(): Album: " + track.album);
        Log.i(TAG, "getCurrentTrack(): Duration: " + track.duration);
        for (int i = 0; i < artists.size(); ++i) {
            Log.i(TAG, "getCurrentTrack(): Artist "
                    + i + ": " + artists.get(i));
        }
        return track;
    }

}
