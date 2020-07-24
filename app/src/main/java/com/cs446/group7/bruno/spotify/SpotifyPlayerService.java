package com.cs446.group7.bruno.spotify;

import android.content.Context;
import android.util.Log;

import com.cs446.group7.bruno.R;
import com.cs446.group7.bruno.music.BrunoTrack;
import com.cs446.group7.bruno.music.player.MusicPlayer;
import com.cs446.group7.bruno.music.player.MusicPlayerError;
import com.cs446.group7.bruno.music.player.MusicPlayerSubscriber;
import com.cs446.group7.bruno.utils.Callback;
import com.spotify.android.appremote.api.ConnectionParams;
import com.spotify.android.appremote.api.Connector;
import com.spotify.android.appremote.api.SpotifyAppRemote;
import com.spotify.android.appremote.api.error.AuthenticationFailedException;
import com.spotify.android.appremote.api.error.CouldNotFindSpotifyApp;
import com.spotify.android.appremote.api.error.LoggedOutException;
import com.spotify.android.appremote.api.error.NotLoggedInException;
import com.spotify.android.appremote.api.error.OfflineModeException;
import com.spotify.android.appremote.api.error.SpotifyConnectionTerminatedException;
import com.spotify.android.appremote.api.error.SpotifyDisconnectedException;
import com.spotify.android.appremote.api.error.SpotifyRemoteServiceException;
import com.spotify.android.appremote.api.error.UnsupportedFeatureVersionException;
import com.spotify.android.appremote.api.error.UserNotAuthorizedException;
import com.spotify.protocol.types.Artist;
import com.spotify.protocol.types.PlayerState;
import com.spotify.protocol.types.Track;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;

/**
 * Service responsible for connecting to Spotify, play music and notifying subscribers about state changes.
 */
class SpotifyPlayerService implements MusicPlayer {

    // Main interface to the Spotify app, initialized by connect()
    private SpotifyAppRemote mSpotifyAppRemote;
    private List<MusicPlayerSubscriber> spotifyServiceSubscribers;
    private final String TAG = getClass().getSimpleName();

    private PlayerState currentPlayerState;
    private String playlistId;

    public SpotifyPlayerService() {
        spotifyServiceSubscribers = new ArrayList<>();
    }

    /**
     * Attempts to connect to Spotify via the user's Spotify app. If any error occurs, a {@link SpotifyPlayerError} is
     * generated in the callback.
     * @param callback callback for handling the result of the connection
     */
    public void connect(final Context context,
                        final Callback<Void, MusicPlayerError> callback) {

        // Spotify is not installed on the device
        if (!SpotifyAppRemote.isSpotifyInstalled(context)) {
            callback.onFailed(SpotifyPlayerError.APP_NOT_FOUND);
            return;
        }

        // Configuration parameters read from resources
        final ConnectionParams connectionParams =
                new ConnectionParams.Builder(context.getResources().getString(R.string.spotify_client_id))
                    .setRedirectUri(context.getResources().getString(R.string.spotify_redirect_uri))
                    .showAuthView(true)
                    .build();

        // Attempt to connect to Spotify
        SpotifyAppRemote.connect(context, connectionParams, new Connector.ConnectionListener() {

            // Success! Maintain control of the main interface AppRemote
            // and listen for updates to the player. Let the caller know that the
            // Spotify player is online - can play/pause music, etc
            @Override
            public void onConnected(SpotifyAppRemote spotifyAppRemote) {
                mSpotifyAppRemote = spotifyAppRemote;
                subscribeToPlayerState();
                callback.onSuccess(null);
            }

            @Override
            public void onFailure(Throwable throwable) {
                Log.e(TAG, ".connect onFailure method: " + throwable.toString());
                SpotifyPlayerError spotifyPlayerError = getErrorFromThrowable(throwable);
                callback.onFailed(spotifyPlayerError);
            }
        });
    }

    // Check if we are connected to the Spotify app
    public boolean isConnected() {
        return mSpotifyAppRemote != null && mSpotifyAppRemote.isConnected();
    }

    // New subscribers are added after successfully connecting
    public void addSubscriber(final MusicPlayerSubscriber subscriber) {
        if (spotifyServiceSubscribers.contains(subscriber)) return;
        spotifyServiceSubscribers.add(subscriber);
    }

    public void removeSubscriber(final MusicPlayerSubscriber subscriber) {
        spotifyServiceSubscribers.remove(subscriber);
    }

    // Converts Spotify-specific exceptions to a SpotifyPlayerError
    private static SpotifyPlayerError getErrorFromThrowable (final Throwable throwable) {
        if (throwable instanceof AuthenticationFailedException) { return SpotifyPlayerError.AUTHENTICATION_FAILED; }
        if (throwable instanceof UserNotAuthorizedException) { return SpotifyPlayerError.AUTHORIZATION_FAILED; }
        if (throwable instanceof CouldNotFindSpotifyApp) { return SpotifyPlayerError.APP_NOT_FOUND; }
        if (throwable instanceof LoggedOutException) { return SpotifyPlayerError.LOGGED_OUT; }
        if (throwable instanceof NotLoggedInException) { return SpotifyPlayerError.NOT_LOGGED_IN; }
        if (throwable instanceof OfflineModeException) { return SpotifyPlayerError.OFFLINE_MODE; }
        if (throwable instanceof SpotifyConnectionTerminatedException) { return SpotifyPlayerError.CONNECTION_TERMINATED; }
        if (throwable instanceof SpotifyDisconnectedException) { return SpotifyPlayerError.DISCONNECTED; }
        if (throwable instanceof SpotifyRemoteServiceException) { return SpotifyPlayerError.REMOTE_SERVICE_ERROR; }
        if (throwable instanceof UnsupportedFeatureVersionException) { return SpotifyPlayerError.UNSUPPORTED_FEATURE_VERSION; }
        return SpotifyPlayerError.OTHER_ERROR;
    }

    // Listen for updates from the Spotify player
    // Can be quite noisy (gets called 4 times instead of once at the beginning)
    // Currently keeping track of the player's state, along with any track changes,
    // but could be modified for more complex logic
    private void subscribeToPlayerState() {
        mSpotifyAppRemote.getPlayerApi()
                .subscribeToPlayerState()
                .setEventCallback(playerState -> {
                    if (playerState == null || playerState.equals(currentPlayerState)) return;

                    Log.d(TAG, playerState.toString());
                    Track track = playerState.track;
                    if (track != null) {
                        if (currentPlayerState != null && track.equals(currentPlayerState.track)) {
                            // same track, perhaps just paused
                            Log.w(TAG, "Same track!");
                        }

                        for (MusicPlayerSubscriber subscriber : spotifyServiceSubscribers) {
                            subscriber.onTrackChanged(makeBrunoTrack(track));
                        }

                        Log.d(TAG, String.format("Curr Track: %s", track.toString()));
                    } else {
                        Log.w(TAG, "Track is null!");
                    }
                    currentPlayerState = playerState;
                })
                .setErrorCallback(throwable -> { // "Catch" for exceptions in setEventCallback
                    Log.e(TAG, "playerState setErrorCallback: " + throwable.toString());
                });
    }

    // Should be called when disconnecting from Spotify
    public void disconnect() {
        SpotifyAppRemote.disconnect(mSpotifyAppRemote);
    }

    // Plays the playlist which is set by setPlaylist()
    // Note that calling this method multiple times will play the custom playlist from the beginning each time
    public void play(Callback<Void, Exception> callback) {
        mSpotifyAppRemote.getPlayerApi().getPlayerState().setResultCallback(playerState -> {
            if (playerState.playbackRestrictions.canToggleShuffle) { // premium users
                mSpotifyAppRemote.getPlayerApi()
                        .setShuffle(false)
                        .setResultCallback(empty -> {
                            mSpotifyAppRemote.getPlayerApi().play("spotify:playlist:" + this.playlistId).setResultCallback(empty1 -> {
                                callback.onSuccess(null);
                            }).setErrorCallback(throwable -> {
                                Log.e(TAG, "play after shuffle failed: " + throwable.toString());
                                callback.onFailed(new Exception(throwable));
                            });
                        })
                        .setErrorCallback(throwable -> {
                            Log.e(TAG, "shuffle failed: " + throwable.toString());
                            callback.onFailed(new Exception(throwable));
                        });
            } else { // free users
                mSpotifyAppRemote.getPlayerApi().play("spotify:playlist:" + this.playlistId).setResultCallback(empty1 -> {
                    callback.onSuccess(null);
                }).setErrorCallback(throwable -> {
                    Log.e(TAG, "playing for free users failed: " + throwable.toString());
                    callback.onFailed(new Exception(throwable));
                });
            }
        }).setErrorCallback(throwable -> {
            Log.e(TAG, "failed to acquire player state in play: " + throwable.toString());
            callback.onFailed(new Exception(throwable));
        });
    }

    // Sets the playlist for the music player
    public void setPlayerPlaylist(String playlistId) { this.playlistId = playlistId; }

    // Pause the player
    public void pause(Callback<Void, Exception> callback) {
        mSpotifyAppRemote.getPlayerApi()
                .pause().setResultCallback(empty -> {
                    callback.onSuccess(null);
                })
                .setErrorCallback(throwable -> {
                    Log.e(TAG, "pause failed: " + throwable.toString());
                    callback.onFailed(new Exception(throwable));
                });
    }

    // Resume the player
    public void resume(Callback<Void, Exception> callback) {
        mSpotifyAppRemote.getPlayerApi()
                .resume().setResultCallback(empty -> {
                    Log.i(TAG, "Resumed!");
                    callback.onSuccess(null);
                })
                .setErrorCallback(throwable -> {
                    Log.e(TAG, "resume failed: " + throwable.toString());
                    callback.onFailed(new Exception(throwable));
                });
    }

    // Reads the currently playing track from the player
    // and returns a BrunoTrack containing track metadata
    public BrunoTrack getCurrentTrack() {
        if (currentPlayerState == null || currentPlayerState.track == null) return null;
        return makeBrunoTrack(currentPlayerState.track);
    }

    // Converts Spotify's Track object to a BrunoTrack object
    private static BrunoTrack makeBrunoTrack(@NonNull final Track track) {
        final List<Artist> trackArtists = track.artists;
        final ArrayList<String> artistNames = new ArrayList<>();
        for (final Artist artist : trackArtists) {
            artistNames.add(artist.name);
        }
        return new BrunoTrack(track.name, track.album.name, track.duration, artistNames);
    }
}
