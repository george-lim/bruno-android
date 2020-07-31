package com.cs446.group7.bruno.music.player;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.cs446.group7.bruno.music.BrunoPlaylist;
import com.cs446.group7.bruno.music.BrunoTrack;
import com.cs446.group7.bruno.utils.Callback;

import java.util.ArrayList;
import java.util.List;

public class MockMusicPlayerImpl implements MusicPlayer {

    // MARK: - Private members

    private BrunoPlaylist playlist;
    private List<MusicPlayerSubscriber> subscribers;
    private Thread playSongsThread;
    private long songStartTime;

    // MARK: - Lifecycle methods

    public MockMusicPlayerImpl() {
        playlist = null;
        subscribers = new ArrayList<>();
        playSongsThread = new Thread(() -> playSongs());
    }

    // MARK: - Private methods

    // Simulates playing the playlist in the background, with proper track delay.
    private void playSongs() {
        try {
            // Play each song
            for (BrunoTrack track : playlist.tracks) {
                // Dispatch to UI thread
                new Handler(Looper.getMainLooper()).post(() -> {
                    // Notify all subscribers of new track
                    for (MusicPlayerSubscriber subscriber : subscribers) {
                        subscriber.onTrackChanged(track);
                    }
                });

                songStartTime = System.currentTimeMillis();
                // Sleep for song duration to simulate song playing
                Thread.sleep(track.duration);
            }
        }
        // Return from the method immediately. Safely terminates thread.
        catch (InterruptedException e) {
            return;
        }
    }

    // MARK: - MusicPlayer methods

    public void connect(final Context context,
                        final Callback<Void, MusicPlayerException> callback) {
        callback.onSuccess(null);
    }

    public void disconnect() {
        // NOOP
    }

    public void addSubscriber(final MusicPlayerSubscriber subscriber) {
        subscribers.add(subscriber);
    }

    public void removeSubscriber(final MusicPlayerSubscriber subscriber) {
        subscribers.remove(subscriber);
    }

    public void setPlayerPlaylist(final BrunoPlaylist playlist) {
        this.playlist = playlist;
    }

    public void play() {
        if (playlist == null) {
            Log.w(getClass().getSimpleName(), "Missing playlist when calling play()");
            return;
        }

        playSongsThread.start();
    }

    public void stop() {
        if (!playSongsThread.isAlive()) {
            return;
        }

        playSongsThread.interrupt();
    }

    public void stopAndDisconnect() {
        stop();
        disconnect();
    }

    public void getPlaybackPosition(final Callback<Long, Throwable> callback) {
        callback.onSuccess(System.currentTimeMillis() - songStartTime);
    }
}
