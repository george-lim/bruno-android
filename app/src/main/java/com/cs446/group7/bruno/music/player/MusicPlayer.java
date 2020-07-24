package com.cs446.group7.bruno.music.player;

import android.content.Context;

import com.cs446.group7.bruno.music.BrunoTrack;
import com.cs446.group7.bruno.utils.Callback;

// Any music player should implement this interface
public interface MusicPlayer {
    void connect(Context context,
                 Callback<Void, MusicPlayerError> callback);
    boolean isConnected();
    void disconnect();
    void addSubscriber(final MusicPlayerSubscriber subscriber);
    void removeSubscriber(final MusicPlayerSubscriber subscriber);
    // Sets the playlist which play() will start playing
    void setPlayerPlaylist(String playlistId);
    // Plays the playlist from the beginning
    void play(Callback<Void, Exception> callback);
    // Pauses the current playlist
    void pause(Callback<Void, Exception> callback);
}
