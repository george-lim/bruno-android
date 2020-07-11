package com.cs446.group7.bruno.spotify;

import android.content.Context;
import android.util.Log;

import com.cs446.group7.bruno.R;
import com.cs446.group7.bruno.music.BrunoTrack;
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
public class SpotifyService {

    // Main interface to Spotify, initialized by connectToSpotify()
    private SpotifyAppRemote mSpotifyAppRemote;
    private Context context;
    private List<SpotifyServiceSubscriber> spotifyServiceSubscribers;
    private final String TAG = getClass().getSimpleName();

    private PlayerState currentPlayerState;

    public SpotifyService(final Context context) {
        this.context = context;
        spotifyServiceSubscribers = new ArrayList<>();
    }

    /**
     * Attempts to connect to Spotify via the user's Spotify app. If any error occurs, a {@link SpotifyServiceError} is
     * generated in the callback.
     * @param callback callback for handling the result of the connection
     */
    public void connect(final Callback<Void, SpotifyServiceError> callback) {

        // Spotify is not installed on the device
        if (!SpotifyAppRemote.isSpotifyInstalled(context)) {
            callback.onFailed(SpotifyServiceError.APP_NOT_FOUND);
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
                SpotifyServiceError spotifyServiceError = getErrorFromThrowable(throwable);
                callback.onFailed(spotifyServiceError);
                for (SpotifyServiceSubscriber subscriber : spotifyServiceSubscribers) {
                    subscriber.onError(spotifyServiceError);
                }
            }
        });
    }

    public boolean isConnected() {
        return mSpotifyAppRemote != null && mSpotifyAppRemote.isConnected();
    }

    public void addSubscriber(final SpotifyServiceSubscriber subscriber) {
        if (spotifyServiceSubscribers.contains(subscriber)) return;
        spotifyServiceSubscribers.add(subscriber);
    }

    public void removeSubscriber(final SpotifyServiceSubscriber subscriber) {
        spotifyServiceSubscribers.remove(subscriber);
    }

    private static SpotifyServiceError getErrorFromThrowable (final Throwable throwable) {
        if (throwable instanceof AuthenticationFailedException) { return SpotifyServiceError.AUTHENTICATION_FAILED; }
        if (throwable instanceof UserNotAuthorizedException) { return SpotifyServiceError.AUTHORIZATION_FAILED; }
        if (throwable instanceof CouldNotFindSpotifyApp) { return SpotifyServiceError.APP_NOT_FOUND; }
        if (throwable instanceof LoggedOutException) { return SpotifyServiceError.LOGGED_OUT; }
        if (throwable instanceof NotLoggedInException) { return SpotifyServiceError.NOT_LOGGED_IN; }
        if (throwable instanceof OfflineModeException) { return SpotifyServiceError.OFFLINE_MODE; }
        if (throwable instanceof SpotifyConnectionTerminatedException) { return SpotifyServiceError.CONNECTION_TERMINATED; }
        if (throwable instanceof SpotifyDisconnectedException) { return SpotifyServiceError.DISCONNECTED; }
        if (throwable instanceof SpotifyRemoteServiceException) { return SpotifyServiceError.REMOTE_SERVICE_ERROR; }
        if (throwable instanceof UnsupportedFeatureVersionException) { return SpotifyServiceError.UNSUPPORTED_FEATURE_VERSION; }
        return SpotifyServiceError.OTHER_ERROR;
    }

    // Listen for updates from the Spotify player
    // Quite noisy (gets called 4 times instead of once during an update)
    // Currently keeping track of the current track here, but could be modified for more complex logic
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
                            Log.d(TAG, "Same track!");
                        } else {
                            // track changed, let client know
                            for (SpotifyServiceSubscriber subscriber : spotifyServiceSubscribers) {
                                subscriber.onTrackChanged(makeBrunoTrack(track));
                            }
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

    public void pauseMusic() {
        mSpotifyAppRemote.getPlayerApi().pause();
    }

    public void resumeMusic() {
        mSpotifyAppRemote.getPlayerApi().resume();
    }

    // Note that calling this method multiple times will play the custom playlist from the beginning
    public void playMusic(final String playlistId) {
        mSpotifyAppRemote.getPlayerApi()
                .setShuffle(false)
                .setResultCallback(empty -> {
                    mSpotifyAppRemote.getPlayerApi().play("spotify:playlist:" + playlistId).setResultCallback(empty1 -> Log.i(TAG, "playing!"));
                })
                .setErrorCallback(throwable -> Log.e(TAG, "playMusic Failed: " + throwable.toString()));
    }

    // Reads the currently playing track from the player
    // and returns a BrunoTrack containing track metadata
    public BrunoTrack getCurrentTrack() {
        if (currentPlayerState == null || currentPlayerState.track == null) return null;
        return makeBrunoTrack(currentPlayerState.track);
    }

    private static BrunoTrack makeBrunoTrack(@NonNull final Track track) {
        final List<Artist> trackArtists = track.artists;
        final ArrayList<String> artistNames = new ArrayList<>();
        for (final Artist artist : trackArtists) {
            artistNames.add(artist.name);
        }
        return new BrunoTrack(track.name, track.album.name, track.duration, artistNames);
    }
}
