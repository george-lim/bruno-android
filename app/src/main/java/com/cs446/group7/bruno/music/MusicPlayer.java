package com.cs446.group7.bruno.music;

import android.content.Context;

public abstract class MusicPlayer {
    abstract public void connect(OnPlayerCallback callback, Context appContext);
    abstract public void disconnect();
    abstract public void playMusic(String playlistId);
    abstract public void pauseMusic();
    abstract public void resumeMusic();
    abstract public BrunoTrack getCurrentTrack();
}
