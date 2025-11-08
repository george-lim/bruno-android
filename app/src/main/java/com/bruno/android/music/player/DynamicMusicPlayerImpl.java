package com.bruno.android.music.player;

import android.content.Context;

import com.bruno.android.MainActivity;
import com.bruno.android.music.BrunoPlaylist;
import com.bruno.android.storage.PreferencesStorage;
import com.bruno.android.utils.Callback;

public class DynamicMusicPlayerImpl implements MusicPlayer {
    private final MusicPlayer musicPlayer;
    private final MusicPlayer mockMusicPlayer;

    public DynamicMusicPlayerImpl(final MusicPlayer musicPlayer,
                                  final MusicPlayer mockMusicPlayer) {
        this.musicPlayer = musicPlayer;
        this.mockMusicPlayer = mockMusicPlayer;
    }

    private MusicPlayer getMusicPlayer() {
        boolean isUsingMock = MainActivity.getPreferencesStorage().getBoolean(
                PreferencesStorage.KEYS.MOCK_MUSIC_PLAYER,
                false
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
