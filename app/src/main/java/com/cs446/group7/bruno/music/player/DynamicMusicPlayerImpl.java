package com.cs446.group7.bruno.music.player;

import android.content.Context;

import com.cs446.group7.bruno.MainActivity;
import com.cs446.group7.bruno.music.BrunoPlaylist;
import com.cs446.group7.bruno.storage.PreferencesStorage;
import com.cs446.group7.bruno.utils.Callback;

public class DynamicMusicPlayerImpl implements MusicPlayer {
    private MusicPlayer musicPlayer;
    private MusicPlayer mockMusicPlayer;

    public DynamicMusicPlayerImpl(final MusicPlayer musicPlayer,
                                  final MusicPlayer mockMusicPlayer) {
        this.musicPlayer = musicPlayer;
        this.mockMusicPlayer = mockMusicPlayer;
    }

    private MusicPlayer getMusicPlayer() {
        boolean isUsingMock = MainActivity.getPreferencesStorage().getBoolean(
                PreferencesStorage.KEYS.MOCK_MUSIC_PLAYER,
                true
        );

        return isUsingMock ? mockMusicPlayer : musicPlayer;
    }

    @Override
    public void connect(final Context context,
                        final Callback<Void, MusicPlayerException> callback) {
        getMusicPlayer().connect(context, callback);
    }

    @Override
    public void addSubscriber(final MusicPlayerSubscriber subscriber) {
        getMusicPlayer().addSubscriber(subscriber);
    }

    @Override
    public void removeSubscriber(final MusicPlayerSubscriber subscriber) {
        getMusicPlayer().removeSubscriber(subscriber);
    }

    @Override
    public void setPlayerPlaylist(final BrunoPlaylist playlist) {
        getMusicPlayer().setPlayerPlaylist(playlist);
    }

    @Override
    public void play() {
        getMusicPlayer().play();
    }

    @Override
    public void stop() {
        getMusicPlayer().stop();
    }

    @Override
    public void stopAndDisconnect() {
        getMusicPlayer().stopAndDisconnect();
    }

    @Override
    public void getPlaybackPosition(final Callback<Long, Throwable> callback) {
        getMusicPlayer().getPlaybackPosition(callback);
    }
}
