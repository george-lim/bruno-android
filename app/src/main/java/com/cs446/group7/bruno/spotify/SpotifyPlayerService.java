package com.cs446.group7.bruno.spotify;

import android.content.Context;
import android.util.Log;

import com.cs446.group7.bruno.R;
import com.cs446.group7.bruno.music.BrunoPlaylist;
import com.cs446.group7.bruno.music.BrunoTrack;
import com.cs446.group7.bruno.music.player.MusicPlayer;
import com.cs446.group7.bruno.music.player.MusicPlayerException;
import com.cs446.group7.bruno.music.player.MusicPlayerSubscriber;
import com.cs446.group7.bruno.utils.Callback;
import com.cs446.group7.bruno.utils.ClosureQueue;
import com.cs446.group7.bruno.utils.NoFailCallback;
import com.spotify.android.appremote.api.ConnectionParams;
import com.spotify.android.appremote.api.Connector;
import com.spotify.android.appremote.api.PlayerApi;
import com.spotify.android.appremote.api.SpotifyAppRemote;
import com.spotify.android.appremote.api.error.CouldNotFindSpotifyApp;
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
    private BrunoPlaylist playlist;

    public SpotifyPlayerService() {
        spotifyServiceSubscribers = new ArrayList<>();
    }

    /**
     * Attempts to connect to Spotify via the user's Spotify app. If any error occurs, a {@link SpotifyPlayerException} is
     * generated in the callback.
     * @param callback callback for handling the result of the connection
     */
    public void connect(final Context context,
                        final Callback<Void, MusicPlayerException> callback) {

        // Don't connect if it's already connected
        if (isConnected()) {
            callback.onSuccess(null);
            return;
        }

        // Spotify is not installed on the device
        if (!SpotifyAppRemote.isSpotifyInstalled(context)) {
            SpotifyPlayerException exception = new SpotifyPlayerException(new CouldNotFindSpotifyApp());
            callback.onFailed(exception);
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
            // Spotify player is online - can play/stop music, etc
            @Override
            public void onConnected(SpotifyAppRemote spotifyAppRemote) {
                mSpotifyAppRemote = spotifyAppRemote;
                subscribeToPlayerState();
                callback.onSuccess(null);
            }

            @Override
            public void onFailure(Throwable throwable) {
                Log.e(TAG, ".connect onFailure method: " + throwable.toString());
                MusicPlayerException exception = new SpotifyPlayerException(throwable);
                callback.onFailed(exception);
            }
        });
    }

    // Check if we are connected to the Spotify app
    private boolean isConnected() {
        return mSpotifyAppRemote != null && mSpotifyAppRemote.isConnected();
    }
    
    public void addSubscriber(final MusicPlayerSubscriber subscriber) {
        if (spotifyServiceSubscribers.contains(subscriber)) return;
        spotifyServiceSubscribers.add(subscriber);
    }

    public void removeSubscriber(final MusicPlayerSubscriber subscriber) {
        spotifyServiceSubscribers.remove(subscriber);
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
        if (isConnected()) {
            SpotifyAppRemote.disconnect(mSpotifyAppRemote);
        }
    }

    // Plays the playlist which is set by setPlaylist()
    // Note that calling this method multiple times will play the custom playlist from the beginning each time
    public void play() {
        if (playlist == null) {
            Log.w(TAG, "Missing playlist when calling play()");
            return;
        }

        PlayerApi api = mSpotifyAppRemote.getPlayerApi();

        ClosureQueue<Void, Throwable> queue = new ClosureQueue<>();

        /*
            Set player shuffle to off if possible.
            NOTE: Free users cannot turn off shuffle. It is also possible for a premium user NOT to
                  be able to turn off shuffle if they previously had songs queued up.
         */
        queue.add((result, nextCallback) -> {
            api.getPlayerState()
                    .setResultCallback(playerState -> {
                        // If player cannot turn off shuffle, just succeed anyway and move on.
                        if (!playerState.playbackRestrictions.canToggleShuffle) {
                            nextCallback.onSuccess(null);
                            return;
                        }

                        api.setShuffle(false)
                                .setResultCallback(empty -> nextCallback.onSuccess(null))
                                .setErrorCallback(throwable -> nextCallback.onFailed(throwable));
                    })
                    .setErrorCallback(throwable -> nextCallback.onFailed(throwable));
        });

        // Now start playing playlist
        queue.add((result, nextCallback) -> {
            String playlistUrl = "spotify:playlist:" + playlist.id;

            api.play(playlistUrl)
                    .setResultCallback(empty -> nextCallback.onSuccess(null))
                    .setErrorCallback(throwable -> nextCallback.onFailed(throwable));
        });

        queue.run(new Callback<Void, Throwable>() {
            @Override
            public void onSuccess(Void result) {
                // NOOP
            }

            @Override
            public void onFailed(Throwable result) {
                Log.e(TAG, "Play playlist failed with error: " + result.toString());
            }
        });
    }

    // Sets the playlist for the music player
    public void setPlayerPlaylist(BrunoPlaylist playlist) { this.playlist = playlist; }

    // Stop the player by pausing it
    public void stop() {
        mSpotifyAppRemote.getPlayerApi()
                .pause()
                .setErrorCallback(throwable -> {
                    Log.e(TAG, "Stop playlist failed with error: " + throwable.toString());
                });
    }

    // Stop the player then disconnect
    public void stopAndDisconnect() {
        mSpotifyAppRemote.getPlayerApi()
                .pause()
                .setResultCallback(empty -> disconnect())
                .setErrorCallback(throwable -> {
                    Log.e(TAG, "Stop playlist failed with error: " + throwable.toString());
                    disconnect();
                });
    }

    public void getPlaybackPosition(NoFailCallback<Long> callback) {
        if (!isConnected()) return;
        mSpotifyAppRemote.getPlayerApi().getPlayerState().setResultCallback(playerState -> {
            callback.onSuccess(playerState.playbackPosition);
        });
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
