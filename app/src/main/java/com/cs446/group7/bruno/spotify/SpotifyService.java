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

// I am designing this class in a way where it's instantiated once and acts as the main interface to
// Spotify. We can break this down later into separate components if it makes it easier to work with

public class SpotifyService {

    // Main interface to Spotify, initialized by connectToSpotify()
    private SpotifyAppRemote mSpotifyAppRemote;

    // Constantly updated from the Spotify player by subscribing to the player state
    // Not to be confused with BrunoTrack, which is a custom container class for our app
    // This Track is Spotify's Track object, which has much more metadata which we don't need
    private Track currentTrack;

    // connectToSpotify() does the main initialization
    public SpotifyService() { }

    // Exception caused by not having Spotify installed
    public static class SpotifyNotInstalledException extends RuntimeException {
        public SpotifyNotInstalledException() {
            super("SpotifyNotInstalledException - Spotify not installed on device");
        }
    }

    // Attempts to connect to Spotify by authenticating users
    public void connectToSpotify(OnPlayerCallback callback, Context appContext) {

        // Spotify is not installed on the device - communicating this via a custom exception
        if (!SpotifyAppRemote.isSpotifyInstalled(appContext)) {
            callback.onPlayerError(new SpotifyNotInstalledException());
        }

        // Configuration parameters read from resources
        final ConnectionParams connectionParams =
                new ConnectionParams.Builder(appContext.getResources().getString(R.string.spotify_client_id))
                    .setRedirectUri(appContext.getResources().getString(R.string.spotify_redirect_uri))
                    .showAuthView(true)
                    .build();

        // Attempt to connect to Spotify
        SpotifyAppRemote.connect(appContext, connectionParams,
                new Connector.ConnectionListener() {

                    // Success! Maintain control of the main interface AppRemote
                    // and listen for updates to the player. Let the caller know that the
                    // Spotify player is online - can now play/pause music, etc
                    public void onConnected(SpotifyAppRemote spotifyAppRemote) {
                        mSpotifyAppRemote = spotifyAppRemote;
                        subscribeToPlayerState();
                        callback.onPlayerReady();
                    }

                    public void onFailure(Throwable throwable) {
                        callback.onPlayerError(new Exception(throwable));
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

    // Reads the currently playing track from the player
    // and returns a BrunoTrack containing track metadata
    public BrunoTrack getCurrentTrack() {
        final List<Artist> trackArtists = currentTrack.artists;
        final ArrayList<String> artistNames = new ArrayList<String>();
        for (final Artist artist : trackArtists) {
            artistNames.add(artist.name);
        }
        final BrunoTrack output = new BrunoTrack(currentTrack.name, currentTrack.album.name,
                currentTrack.duration, artistNames);
        return output;
    }
}
