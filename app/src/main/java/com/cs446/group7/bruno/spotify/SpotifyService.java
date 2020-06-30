package com.cs446.group7.bruno.spotify;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;

import android.util.Log;

import com.cs446.group7.bruno.BuildConfig;
import com.cs446.group7.bruno.sensor.Pedometer;
import com.cs446.group7.bruno.sensor.PedometerSubscriber;
import com.cs446.group7.bruno.sensor.SensorService;
import com.spotify.android.appremote.api.ConnectionParams;
import com.spotify.android.appremote.api.Connector;
import com.spotify.android.appremote.api.SpotifyAppRemote;

import com.spotify.protocol.client.Subscription;
import com.spotify.protocol.types.Album;
import com.spotify.protocol.types.PlayerState;
import com.spotify.protocol.types.Track;

// I am designing this class in a way where it's instantiated once and acts as the main interface to Spotify
// We can break this down later into separate components if it makes it easier or more logical to work with

public class SpotifyService {

    private SpotifyAppRemote mSpotifyAppRemote;
    private boolean playingMusic;

    public SpotifyService() {
        playingMusic = false;

        // Creates a hard-coded playlist

    }

    // Exception caused by Spotify failing to start up
    public static class SpotifyStartupException extends RuntimeException {
        public SpotifyStartupException() {
            super("SpotifyStartupException - Failed to connect to Spotify");
        }
    }

    // Exception caused by not having Spotify installed
    public static class SpotifyNotInstalledException extends RuntimeException {
        public SpotifyNotInstalledException() {
            super("SpotifyNotInstalledException - Spotify not installed on device");
        }
    }


    public void connectToSpotify(Context appContext) {

        // Spotify is not installed on the device - communicating this via a custom exception
        if (!SpotifyAppRemote.isSpotifyInstalled(appContext)) {
            throw new SpotifyNotInstalledException();
        }

        ConnectionParams connectionParams =
                new ConnectionParams.Builder(BuildConfig.SPOTIFY_CLIENT_ID)
                        .setRedirectUri(BuildConfig.SPOTIFY_REDIRECT_URI)
                        .showAuthView(true)
                        .build();

        SpotifyAppRemote.connect(appContext, connectionParams,
                new Connector.ConnectionListener() {

                    public void onConnected(SpotifyAppRemote spotifyAppRemote) {
                        mSpotifyAppRemote = spotifyAppRemote;
                        playMusic();
                    }

                    // Spotify failed to connect
                    public void onFailure(Throwable throwable) {
                        throw new SpotifyStartupException();
                    }
                });
    }

    public void disconnectFromSpotify() {
        SpotifyAppRemote.disconnect(mSpotifyAppRemote);
    }

    public void pauseMusic() {
        mSpotifyAppRemote.getPlayerApi().pause();
    }

    public void resumeMusic() {
        mSpotifyAppRemote.getPlayerApi().resume();
    }

    public void playMusic() {
        mSpotifyAppRemote.getPlayerApi().play("spotify:playlist:7fPwZk4KFD2yfU7J5O1JVz");
        playingMusic = true;
    }

}
