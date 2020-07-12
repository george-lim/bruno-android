package com.cs446.group7.bruno.music.player;

import com.cs446.group7.bruno.music.BrunoTrack;
import com.cs446.group7.bruno.utils.Callback;

// Any music player should implement this interface
public interface MusicPlayer {
    // Sets the playlist which play() will start playing
    void setPlayerPlaylist(String playlistId);
    // Plays the playlist from the beginning
    void play(Callback<Void, Exception> callback);
    // Resumes the current playlist
    void resume(Callback<Void, Exception> callback);
    // Pauses the current playlist
    void pause(Callback<Void, Exception> callback);
    // Gets the currently playing track
    BrunoTrack getCurrentTrack();
}
