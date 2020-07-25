package com.cs446.group7.bruno.music.player;

import android.content.Context;

import com.cs446.group7.bruno.music.BrunoPlaylist;
import com.cs446.group7.bruno.utils.Callback;

// Any music player should implement this interface
public interface MusicPlayer {
    void connect(final Context context,
                 final Callback<Void, MusicPlayerError> callback);
    void disconnect();
    void addSubscriber(final MusicPlayerSubscriber subscriber);
    void removeSubscriber(final MusicPlayerSubscriber subscriber);
    // Sets the playlist which play() will start playing
    void setPlayerPlaylist(final BrunoPlaylist playlist);
    // Plays the playlist from the beginning
    void play();
    // Stops the current playlist
    void stop();
}
