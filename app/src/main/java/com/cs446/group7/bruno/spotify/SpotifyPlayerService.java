package com.cs446.group7.bruno.spotify;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.cs446.group7.bruno.R;
import com.cs446.group7.bruno.music.BrunoPlaylist;
import com.cs446.group7.bruno.music.BrunoTrack;
import com.cs446.group7.bruno.music.player.MusicPlayer;
import com.cs446.group7.bruno.music.player.MusicPlayerException;
import com.cs446.group7.bruno.music.player.MusicPlayerSubscriber;
import com.cs446.group7.bruno.utils.Callback;
import com.cs446.group7.bruno.utils.ClosureQueue;
import com.spotify.android.appremote.api.ConnectionParams;
import com.spotify.android.appremote.api.Connector;
import com.spotify.android.appremote.api.PlayerApi;
import com.spotify.android.appremote.api.SpotifyAppRemote;
import com.spotify.android.appremote.api.error.CouldNotFindSpotifyApp;
import com.spotify.android.appremote.api.error.SpotifyDisconnectedException;
import com.spotify.protocol.types.PlayerState;
import com.spotify.protocol.types.Repeat;
import com.spotify.protocol.types.Track;

import java.util.ArrayList;
import java.util.List;

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

    // Indicates if the player has been stopped and subsequently triggered fallback playlist behavior
    private boolean isFallbackTriggered = false;
    // Indicates if the player is done initializing and is playing the first song of the playlist
    private boolean isPlayerStarted = false;
    // Indicates if the player has received information about the first song of the playlist
    private boolean hasReachedFirstSong = false;

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
        if (!SpotifyService.isSpotifyInstalled(context)) {
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

                    Log.d(TAG, "Received new PlayerState: " + playerState.toString());

                    // Detect and handle fallback behaviour.
                    // Since we only have one fallback playlist, we only trigger fallback once.
                    // Note that the track could be null when the player has stopped.
                    if (!isFallbackTriggered && didPlayerStopPlaying(playerState)) {
                        Log.w(TAG, "Fallback playlist triggered.");
                        isFallbackTriggered = true;
                        if (spotifyServiceSubscribers.isEmpty()) {
                            Log.w(TAG, "No subscribers to alert about the fallback playlist.");
                        }
                        for (MusicPlayerSubscriber subscriber : spotifyServiceSubscribers) {
                            subscriber.onFallback();
                        }
                        // Early return since triggering onTrackChanged is not useful when switching playlists
                        return;
                    }

                    Track track = playerState.track;

                    if (track != null) {
                        Log.d(TAG, String.format("Received Track: %s", track.toString()));
                        // Check to see if the track received corresponds to the beginning of the playlist
                        if (!hasReachedFirstSong && convertToBrunoTrack(track).equals(playlist.getTrack(0))) {
                            Log.d(TAG, "Received the first song related to the playlist.");
                            hasReachedFirstSong = true;
                        }

                        boolean isDifferentTrack =
                                !(currentPlayerState != null && track.equals(currentPlayerState.track));

                        // Only alert subscribers about new track changes once we reach the first song of the playlist
                        if ((hasReachedFirstSong || isFallbackTriggered) && isDifferentTrack) {
                            Log.d(TAG, "Alerting subscribers about a new track change.");
                            for (MusicPlayerSubscriber subscriber : spotifyServiceSubscribers) {
                                subscriber.onTrackChanged(convertToBrunoTrack(track));
                            }
                        }

                    } else {
                        Log.w(TAG, "Received a null track!");
                    }
                    currentPlayerState = playerState;
                })
                .setErrorCallback(throwable -> { // "Catch" for exceptions in setEventCallback
                    Log.e(TAG, "playerState setErrorCallback: " + throwable.toString());
                });
    }

    // Should be called when disconnecting from Spotify
    private void disconnect() {
        if (isConnected()) {
            SpotifyAppRemote.disconnect(mSpotifyAppRemote);
        }
    }

    // Checks if we need to switch to a fallback playlist by examining the current player state
    private boolean didPlayerStopPlaying(PlayerState currentState) {
        // Ignore player state changes until the player begins playing the first song of the playlist
        if (!isPlayerStarted) {
            // Need to make sure that the track is not null and is equal to the first song of the playlist
            boolean isBeginningOfPlaylist = currentState.track != null
                    && convertToBrunoTrack(currentState.track).equals(playlist.getTrack(0));
            // Then check if the first song is currently playing and at a non-zero playback position
            isPlayerStarted =
                    isBeginningOfPlaylist && currentState.playbackSpeed != 0.0 && currentState.playbackPosition > 0;
            if (isPlayerStarted) {
                Log.d(TAG, "The player has started playing.");
            }
            return false;
        }
        // Since the player has started playing from this point, we don't have to worry about the track being null

        // This case happens when Spotify appears to be playing a song (i.e. not paused), but the player is "stuck" and
        // not playing music
        boolean stoppedInSongMiddle = !currentState.isPaused && currentState.playbackSpeed == 0.0;

        // This case happens when Spotify returns to the beginning of the song and is paused.
        // Note: Spotify usually returns to the beginning of the playlist but it is possible to return to the
        // beginning of the current song within the playlist, so this case assumes that it could be any song
        boolean stoppedInSongBeginning =
                currentState.isPaused && currentState.playbackSpeed == 0.0
                        && currentState.playbackPosition == 0;
        if (stoppedInSongBeginning) { Log.d(TAG, "The song has stopped in the beginning."); }
        if (stoppedInSongMiddle) { Log.d(TAG, "The song has stopped in the middle."); }
        return stoppedInSongBeginning || stoppedInSongMiddle;
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
        // Reset the check for the first track which matches the playlist
        hasReachedFirstSong = false;

        /*
            Set player shuffle to off if possible.
            NOTE: Free users cannot turn off shuffle. It is also possible for a premium user NOT to
                  be able to turn off shuffle if they previously had songs queued up.
         */
        queue.add((result, nextCallback) -> api.getPlayerState()
                .setResultCallback(playerState -> {
                    // If player cannot turn off shuffle, just succeed anyway and move on.
                    if (!playerState.playbackRestrictions.canToggleShuffle) {
                        nextCallback.onSuccess(null);
                        return;
                    }

                    api.setShuffle(false)
                            .setResultCallback(empty -> nextCallback.onSuccess(null))
                            .setErrorCallback(nextCallback::onFailed);
                })
                .setErrorCallback(nextCallback::onFailed));

        /*
            Set player repeat to on if possible.
            NOTE: Free users cannot turn on repeat.
         */
        queue.add((result, nextCallback) -> api.getPlayerState()
                .setResultCallback(playerState -> {
                    // If player cannot turn on repeat, just succeed anyway and move on.
                    if (!playerState.playbackRestrictions.canRepeatContext) {
                        nextCallback.onSuccess(null);
                        return;
                    }

                    api.setRepeat(Repeat.ALL)
                            .setResultCallback(empty -> nextCallback.onSuccess(null))
                            .setErrorCallback(nextCallback::onFailed);
                })
                .setErrorCallback(nextCallback::onFailed));

        // Now start playing playlist
        queue.add((result, nextCallback) -> {
            String playlistUrl = "spotify:playlist:" + playlist.getId();

            api.play(playlistUrl)
                    .setResultCallback(empty -> nextCallback.onSuccess(null))
                    .setErrorCallback(nextCallback::onFailed);
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
                .setErrorCallback(throwable -> Log.e(TAG, "Stop playlist failed with error: " + throwable.toString()));
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

    public void getPlaybackPosition(final Callback<Long, Throwable> callback) {
        if (!isConnected()) {
            callback.onFailed(new SpotifyDisconnectedException());
            return;
        }

        mSpotifyAppRemote
                .getPlayerApi()
                .getPlayerState()
                .setResultCallback(playerState -> callback.onSuccess(playerState.playbackPosition))
                .setErrorCallback(callback::onFailed);
    }

    // Converts Spotify's Track object to a BrunoTrack object
    private static BrunoTrack convertToBrunoTrack(@NonNull final Track track) {
        StringBuilder artists = new StringBuilder();

        for (int i = 0; i < track.artists.size(); ++i) {
            artists.append(track.artists.get(i).name);

            if (i + 1 < track.artists.size()) {
                artists.append(", ");
            }
        }

        return new BrunoTrack(track.name, artists.toString(), track.duration);
    }
}
