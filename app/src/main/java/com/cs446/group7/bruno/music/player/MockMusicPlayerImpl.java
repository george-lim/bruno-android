package com.cs446.group7.bruno.music.player;

import android.util.Log;

import com.cs446.group7.bruno.music.BrunoTrack;
import com.cs446.group7.bruno.utils.Callback;

import java.util.ArrayList;

public class MockMusicPlayerImpl implements MusicPlayer {

    String playlistId = "";
    private final String TAG = getClass().getSimpleName();

    public void setPlaylist(String playlistId) {
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

    public void getCurrentTrack(Callback<BrunoTrack, Exception> callback) {
        ArrayList<String> artists = new ArrayList<String>(2);
        artists.add("Bruno Mars");
        artists.add("Burno Mars");
        BrunoTrack track = new BrunoTrack("name", "album", 420, artists);
        Log.i(TAG, "getCurrentTrack(): Name: " + track.name);
        Log.i(TAG, "getCurrentTrack(): Album: " + track.album);
        Log.i(TAG, "getCurrentTrack(): Duration: " + track.duration);
        for (int i = 0; i < artists.size(); ++i) {
            Log.i(TAG, "getCurrentTrack(): Artist "
                    + i + ": " + artists.get(i));
        }
        callback.onSuccess(track);
    }

}
