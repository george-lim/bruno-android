package com.cs446.group7.bruno.viewmodels;

import android.content.Context;
import android.content.res.Resources;
import android.location.Location;
import android.util.Log;

import androidx.annotation.NonNull;

import com.cs446.group7.bruno.BuildConfig;
import com.cs446.group7.bruno.MainActivity;
import com.cs446.group7.bruno.R;
import com.cs446.group7.bruno.location.BrunoBot;
import com.cs446.group7.bruno.location.Coordinate;
import com.cs446.group7.bruno.location.LocationService;
import com.cs446.group7.bruno.location.LocationServiceSubscriber;
import com.cs446.group7.bruno.models.RouteModel;
import com.cs446.group7.bruno.music.BrunoPlaylist;
import com.cs446.group7.bruno.music.BrunoTrack;
import com.cs446.group7.bruno.music.player.MockMusicPlayerImpl;
import com.cs446.group7.bruno.music.player.MusicPlayer;
import com.cs446.group7.bruno.music.player.MusicPlayerException;
import com.cs446.group7.bruno.music.player.MusicPlayerSubscriber;
import com.cs446.group7.bruno.sensor.PedometerSubscriber;
import com.cs446.group7.bruno.storage.FileStorage;
import com.cs446.group7.bruno.storage.PreferencesStorage;
import com.cs446.group7.bruno.utils.Callback;
import com.cs446.group7.bruno.utils.NoFailCallback;

public class OnRouteViewModel implements LocationServiceSubscriber, MusicPlayerSubscriber, PedometerSubscriber {

    // MARK: - Constants

    private static final int CAMERA_TILT = 60;
    private static final int CAMERA_ZOOM = 18;

    // MARK: - Private members

    private Resources resources;
    private RouteModel model;
    private OnRouteViewModelDelegate delegate;
    private Context context;

    private LocationService locationService;
    private MusicPlayer musicPlayer;
    private boolean hasCompletedRoute;

    private String TAG = getClass().getSimpleName();

    // MARK: - Lifecycle methods

    public OnRouteViewModel(final Context context,
                            final RouteModel model,
                            final OnRouteViewModelDelegate delegate) {
        this.resources = context.getResources();
        this.model = model;
        this.delegate = delegate;
        this.context = context;

        musicPlayer = getMusicPlayer();
        musicPlayer.setPlayerPlaylist(model.getPlaylist());
        musicPlayer.addSubscriber(this);

        hasCompletedRoute = false;

        locationService = getLocationService();
        locationService.addSubscriber(this);
        locationService.startLocationUpdates();

        MainActivity.getSensorService().addPedometerSubscriber(this);

        setupUI();

        // Connect player, and play playlist after connection succeeds
        connectPlayer(context, result -> {
            model.startRouteNavigation();
            musicPlayer.play();
        });
    }

    public void onDestroyView() {
        MainActivity.getLocationService().removeSubscriber(this);
        MainActivity.getSensorService().removePedometerSubscriber(this);
        musicPlayer.removeSubscriber(this);
    }

    // MARK: - Private methods

    private LocationService getLocationService() {
        return BuildConfig.DEBUG
                ? new BrunoBot(model)
                : MainActivity.getLocationService();
    }

    private MusicPlayer getMusicPlayer() {
        return BuildConfig.DEBUG
                ? new MockMusicPlayerImpl()
                : MainActivity.getSpotifyService().getPlayerService();
    }

    private void setupUI() {
        int userAvatarDrawableResourceId = MainActivity.getPreferencesStorage()
                .getInt(PreferencesStorage.USER_AVATAR, PreferencesStorage.DEFAULT_AVATAR);
        int brunoAvatarDrawableResourceId = R.drawable.ic_bruno_avatar;

        delegate.setupUI(userAvatarDrawableResourceId, brunoAvatarDrawableResourceId);

        BrunoTrack currentTrack = model.getCurrentTrack();

        if (currentTrack != null) {
            delegate.updateCurrentSongUI(currentTrack.getName(), currentTrack.getArtists());
        }

        drawRoute();
        refreshUI();
    }

    private void drawRoute() {
        float routeWidth = 14;
        delegate.drawRoute(model.getTrackSegments(), routeWidth);
    }

    private void refreshUI() {
        delegate.animateCamera(
                model.getCurrentCoordinate().getLatLng(),
                model.getCurrentLocation().getBearing(),
                CAMERA_TILT,
                CAMERA_ZOOM
        );

        musicPlayer.getPlaybackPosition(new Callback<Long, Throwable>() {
            @Override
            public void onSuccess(Long playbackPosition) {
                updateBrunoCoordinate(playbackPosition);
                updateDistanceBetweenUserAndPlaylist(playbackPosition);
            }

            @Override
            public void onFailed(Throwable result) {
                updateBrunoCoordinate(0);
                updateDistanceBetweenUserAndPlaylist(0);
            }
        });

        delegate.updateDistanceToCheckpoint((int) model.getDistanceToCheckpoint() + " m");

        if (model.hasCompletedAllCheckpoints()) {
            onRouteCompleted();
        }
        else {
            delegate.updateCheckpointMarker(
                    model.getCheckpoint().getLatLng(),
                    model.getCheckpointRadius()
            );
        }
    }

    private void showPlayerConnectProgressDialog() {
        delegate.showProgressDialog(
                resources.getString(R.string.run_player_connect_title),
                resources.getString(R.string.run_player_connect_message),
                false,
                false
        );
    }

    private void dismissPlayerConnectProgressDialog() {
        delegate.dismissProgressDialog();
    }

    private void showPlayerConnectFailureDialog(final String errorMessage) {
        delegate.showAlertDialog(
                resources.getString(R.string.player_error),
                errorMessage,
                resources.getString(R.string.ok_button),
                (dialogInterface, i) -> delegate.navigateToPreviousScreen(),
                false
        );
    }

    private void connectPlayer(final Context context, final NoFailCallback<Void> callback) {
        showPlayerConnectProgressDialog();

        musicPlayer.connect(context, new Callback<Void, MusicPlayerException>() {
            @Override
            public void onSuccess(Void result) {
                dismissPlayerConnectProgressDialog();
                callback.onSuccess(null);
            }

            @Override
            public void onFailed(MusicPlayerException result) {
                String errorMessage = result.getLocalizedMessage();
                Log.e(TAG, "onFailed connect: " + errorMessage);

                dismissPlayerConnectProgressDialog();
                showPlayerConnectFailureDialog(errorMessage);
            }
        });
    }

    private void updateBrunoCoordinate(long playbackPosition) {
        final Coordinate brunoCoordinate = model.getPlaylistRouteCoordinate(playbackPosition);
        delegate.updateBrunoMarker(brunoCoordinate.getLatLng());
    }

    private void updateDistanceBetweenUserAndPlaylist(long playbackPosition) {
        int userPlaylistDistance = (int)model.getDistanceBetweenUserAndPlaylist(playbackPosition);

        if (userPlaylistDistance < 0) {
            delegate.updateDistanceBetweenUserAndPlaylist(
                    -userPlaylistDistance + " m",
                    resources.getDrawable(R.drawable.ic_angle_double_down, null),
                    resources.getColor(R.color.colorPrimary, null));
        } else {
            delegate.updateDistanceBetweenUserAndPlaylist(
                    userPlaylistDistance + " m",
                    resources.getDrawable(R.drawable.ic_angle_double_up, null),
                    resources.getColor(R.color.colorSecondary, null));
        }
    }

    private void handlePlaylistChanged(final BrunoPlaylist playlist, long playbackPosition) {
        musicPlayer.stop();
        musicPlayer.setPlayerPlaylist(playlist);

        model.mergePlaylist(playlist, playbackPosition);
        delegate.clearMap();

        drawRoute();
        refreshUI();

        musicPlayer.play();
    }

    private void handleFallbackFailed() {
        Log.d(TAG, "onFallback: null fallback playlist");
        delegate.showAlertDialog(
                context.getResources().getString(R.string.fallback_fail_title),
                context.getResources().getString(R.string.fallback_fail_description),
                context.getResources().getString(R.string.ok_button),
                (dialogInterface, i) -> {
                    model.stopRouteNavigation();
                    musicPlayer.stopAndDisconnect();
                    delegate.navigateToPreviousScreen();
                },
                false);
    }

    /**
     * Logic when the route is completed goes here.
     */
    private void onRouteCompleted() {
        if (hasCompletedRoute) {
            return;
        }

        hasCompletedRoute = true;
        model.completeRouteNavigation();

        // TODO: Implement a route completion screen.
        delegate.showAlertDialog(
                resources.getString(R.string.run_completion_title),
                resources.getString(R.string.run_completion_message),
                resources.getString(R.string.ok_button),
                (dialogInterface, i) -> {
                    musicPlayer.stopAndDisconnect();
                    model.reset();
                    delegate.navigateToPreviousScreen();
                },
                false
        );
    }

    // MARK: - User action handlers

    public void handleExitRoute() {
        delegate.showAlertDialog(
                resources.getString(R.string.run_exit_title),
                resources.getString(R.string.run_exit_message),
                resources.getString(R.string.yes_button),
                (dialogInterface, i) -> {
                    model.stopRouteNavigation();
                    musicPlayer.stopAndDisconnect();
                    delegate.navigateToPreviousScreen();
                },
                resources.getString(R.string.no_button),
                null,
                true
        );
    }

    // MARK: - LocationServiceSubscriber methods

    @Override
    public void onLocationUpdate(@NonNull Location location) {
        model.setCurrentLocation(location);
        refreshUI();
    }

    // MARK: - MusicPlayerSubscriber methods

    @Override
    public void onTrackChanged(final BrunoTrack track) {
        model.onTrackChanged(track);
        delegate.updateCurrentSongUI(track.getName(), track.getArtists());
        delegate.showRouteInfoCard();
    }

    @Override
    public void onFallback() {
        musicPlayer.getPlaybackPosition(new Callback<Long, Throwable>() {
            @Override
            public void onSuccess(Long playbackPosition) {
                BrunoPlaylist playlist;

                try {
                    playlist = FileStorage.readFileAsSerializable(context, FileStorage.FALLBACK_PLAYLIST);
                    // Don't use a playlist with no tracks
                    if (playlist.isEmpty()) {
                        handleFallbackFailed();
                        return;
                    }
                }
                catch (Exception e) {
                    // When a user don't have a fallback playlist, FileStorage will throw a FileNotFoundError
                    handleFallbackFailed();
                    return;
                }

                handlePlaylistChanged(playlist, playbackPosition);
            }

            @Override
            public void onFailed(Throwable result) {
                handleFallbackFailed();
            }
        });
    }

    // MARK: - PedometerSubscriber methods

    @Override
    public void didStep() {
        model.incrementStep();
    }
}
