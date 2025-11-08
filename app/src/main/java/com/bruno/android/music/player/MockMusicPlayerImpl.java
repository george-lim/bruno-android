package com.bruno.android.music.player;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.bruno.android.music.BrunoPlaylist;
import com.bruno.android.music.BrunoTrack;
import com.bruno.android.utils.Callback;

import java.util.ArrayList;
import java.util.List;

public class MockMusicPlayerImpl implements MusicPlayer {

    // MARK: - Private members

    private BrunoPlaylist playlist;
    private final List<MusicPlayerSubscriber> subscribers;
    private Thread playSongsThread;
    private long songStartTime;

    private final String TAG = getClass().getSimpleName();

    // MARK: - Lifecycle methods

    public MockMusicPlayerImpl() {
        playlist = null;
        subscribers = new ArrayList<>();
        playSongsThread = null;
    }

    // MARK: - Private methods

    // Simulates playing the playlist in the background, with proper track delay.
    private void playSongs() {
        try {
            for (int trackIndex = 0; true; ++trackIndex) {
                BrunoTrack track = playlist.getTrack(trackIndex);

                // Dispatch to UI thread
                new Handler(Looper.getMainLooper()).post(() -> {
                    // Notify all subscribers of new track
                    for (MusicPlayerSubscriber subscriber : subscribers) {
                        subscriber.onTrackChanged(track);
                    }
                });

                songStartTime = System.currentTimeMillis();
                // Sleep for song duration to simulate song playing
                Thread.sleep(track.getDuration());
            }
        }
        // Return from the method immediately
        catch (InterruptedException ignored) {
        }
    }

    // MARK: - MusicPlayer methods

    public void connect(final Context context,
                        final Callback<Void, MusicPlayerException> callback) {
        callback.onSuccess(null);
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

        playSongsThread = new Thread(this::playSongs);
        playSongsThread.start();
    }

    public void stop() {
        if (playSongsThread == null || !playSongsThread.isAlive()) {
            return;
        }

        try {
            playSongsThread.interrupt();
            playSongsThread.join();
        } catch (InterruptedException e) {
            // NOTE: .join requires a try-catch, even though control will never get here.
            Log.e(TAG, "Interrupted while stopping music player", e);
        }
    }

    public void stopAndDisconnect() {
        stop();
    }

    public void getPlaybackPosition(final Callback<Long, Throwable> callback) {
        callback.onSuccess(System.currentTimeMillis() - songStartTime);
    }
}
