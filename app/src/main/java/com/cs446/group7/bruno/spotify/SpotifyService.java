package com.cs446.group7.bruno.spotify;

import com.cs446.group7.bruno.R;

import android.content.Context;
import android.util.Log;

import com.spotify.android.appremote.api.ConnectionParams;
import com.spotify.android.appremote.api.Connector;
import com.spotify.android.appremote.api.SpotifyAppRemote;

import com.spotify.protocol.types.Artist;
import com.spotify.protocol.types.Track;

import java.util.ArrayList;
import java.util.List;

// I am designing this class in a way where it's instantiated once and acts as the main interface to Spotify
// We can break this down later into separate components if it makes it easier or more logical to work with

public class SpotifyService {

    // Main interface to Spotify, initialized by connectToSpotify
    private SpotifyAppRemote mSpotifyAppRemote;

    // Constantly updated by subscribing to the player state
    // Not to be confused with BrunoTrack, which is a custom container class for our app
    // This Track is Spotify's Track object, which has much more metadata which we don't need
    private Track currentTrack;

    // Connect to Spotify during construction, could be a two-step process if necessary
    public SpotifyService(Context appContext) {
        connectToSpotify(appContext);
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

    // Called by constructor, attempts to connect to Spotify by authenticating users
    // Could be made a public method if we want to call it outside the constructor
    private void connectToSpotify(Context appContext) {

        // Spotify is not installed on the device - communicating this via a custom exception
        if (!SpotifyAppRemote.isSpotifyInstalled(appContext)) {
            throw new SpotifyNotInstalledException();
        }

        // Configuration parameters configured in the BuildConfig
        ConnectionParams connectionParams =
                new ConnectionParams.Builder(appContext.getResources().getString(R.string.spotify_client_id))
                        .setRedirectUri(appContext.getResources().getString(R.string.spotify_redirect_uri))
                        .showAuthView(true)
                        .build();

        // Attempt to connect to Spotify
        SpotifyAppRemote.connect(appContext, connectionParams,
                new Connector.ConnectionListener() {

                    // Success! Maintain control of the main interface AppRemote and listen for updates to the player
                    public void onConnected(SpotifyAppRemote spotifyAppRemote) {
                        mSpotifyAppRemote = spotifyAppRemote;
                        subscribeToPlayerState();
                        playMusic();

                    }

                    // Custom exception for Spotify connection failure
                    public void onFailure(Throwable throwable) {
                        throw new SpotifyStartupException();
                    }
                });
    }

    // Listen for updates from the Spotify player
    // Quite noisy (gets called 4 times instead of once during an update)
    // Currently keeping track of the current track here, but could be modified for more complex logic
    private void subscribeToPlayerState() {
        mSpotifyAppRemote.getPlayerApi()
                .subscribeToPlayerState()
                .setEventCallback(playerState -> {
                    Track track = playerState.track;
                    if (track != null) {
                        Log.d("SpotifyService", track.toString());
                        currentTrack = track;
                    }
                });
    }

    // Should be called when disconnecting from Spotify
    public void disconnectFromSpotify() {
        SpotifyAppRemote.disconnect(mSpotifyAppRemote);
    }

    public void pauseMusic() {
        mSpotifyAppRemote.getPlayerApi().pause();
    }

    public void resumeMusic() {
        mSpotifyAppRemote.getPlayerApi().resume();
    }

    // Note that calling this method multiple times will play the custom playlist from the beginning
    public void playMusic() {
        mSpotifyAppRemote.getPlayerApi().play("spotify:playlist:7fPwZk4KFD2yfU7J5O1JVz");
    }

    // Reads the currently playing track and returns a BrunoTrack containing track metadata
    public BrunoTrack getCurrentTrack() {
        List<Artist> trackArtists = currentTrack.artists;
        ArrayList<String> artistNames = new ArrayList();
        for (Artist artist : trackArtists) {
            artistNames.add(artist.name);
        }
        BrunoTrack output = new BrunoTrack(currentTrack.album.name, currentTrack.artist.name,
                artistNames, currentTrack.duration, currentTrack.name);
        return output;
    }
}
