package com.cs446.group7.bruno.music.player;

import android.util.Log;

import com.cs446.group7.bruno.music.BrunoTrack;
import com.cs446.group7.bruno.utils.Callback;

import java.util.ArrayList;

public class MockMusicPlayerImpl implements MusicPlayer {

    String playlistId = "";

    public void setPlaylist(String playlistId) {
        this.playlistId = playlistId;
    }

    public void play(Callback<Void, Exception> callback) {
        Log.i(this.getClass().getSimpleName(), "play(): Playing playlist " + this.playlistId);
        callback.onSuccess(null);
    }

    public void pause(Callback<Void, Exception> callback) {
        Log.i(this.getClass().getSimpleName(), "pause(): Paused playlist " + this.playlistId);
        callback.onSuccess(null);
    }

    public void resume(Callback<Void, Exception> callback) {
        Log.i(this.getClass().getSimpleName(), "resume(): Resuming playlist " + this.playlistId);
        callback.onSuccess(null);
    }

    public void getCurrentTrack(Callback<BrunoTrack, Exception> callback) {
        ArrayList<String> artists = new ArrayList<String>(2);
        artists.add("Bruno Mars");
        artists.add("Burno Mars");
        BrunoTrack track = new BrunoTrack("name", "album", 420, artists);
        Log.i(this.getClass().getSimpleName(), "getCurrentTrack(): Name: " + track.name);
        Log.i(this.getClass().getSimpleName(), "getCurrentTrack(): Album: " + track.album);
        Log.i(this.getClass().getSimpleName(), "getCurrentTrack(): Duration: " + track.duration);
        for (int i = 0; i < artists.size(); ++i) {
            Log.i(this.getClass().getSimpleName(), "getCurrentTrack(): Artist "
                    + i + ": " + artists.get(i));
        }
        callback.onSuccess(track);
    }

}
