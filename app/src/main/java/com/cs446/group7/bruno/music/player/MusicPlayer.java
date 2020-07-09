package com.cs446.group7.bruno.music.player;

import com.cs446.group7.bruno.music.BrunoTrack;
import com.cs446.group7.bruno.utils.Callback;

// Any music player should implement this interface
public interface MusicPlayer {
    void setPlaylist(String playlistId);
    void play(Callback<Void, Exception> callback);
    void pause(Callback<Void, Exception> callback);
    void getCurrentTrack(Callback<BrunoTrack, Exception> callback);
}
