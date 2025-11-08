package com.bruno.android.viewmodels;

import android.content.Context;
import android.content.res.Resources;
import android.location.Location;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;

import com.bruno.android.BuildConfig;
import com.bruno.android.MainActivity;
import com.bruno.android.R;
import com.bruno.android.location.BrunoBot;
import com.bruno.android.location.Coordinate;
import com.bruno.android.location.DynamicLocationServiceImpl;
import com.bruno.android.location.LocationService;
import com.bruno.android.location.LocationServiceSubscriber;
import com.bruno.android.models.RouteModel;
import com.bruno.android.music.BrunoPlaylist;
import com.bruno.android.music.BrunoTrack;
import com.bruno.android.music.player.DynamicMusicPlayerImpl;
import com.bruno.android.music.player.MockMusicPlayerImpl;
import com.bruno.android.music.player.MusicPlayer;
import com.bruno.android.music.player.MusicPlayerException;
import com.bruno.android.music.player.MusicPlayerSubscriber;
import com.bruno.android.sensor.PedometerSubscriber;
import com.bruno.android.storage.FileStorage;
import com.bruno.android.storage.PreferencesStorage;
import com.bruno.android.utils.Callback;
import com.bruno.android.utils.NoFailCallback;

public class RouteNavigationViewModel
        implements LocationServiceSubscriber, MusicPlayerSubscriber, PedometerSubscriber {

    // MARK: - Constants

    private static final int CAMERA_TILT = 60;
    private static final int CAMERA_ZOOM = 18;

    // MARK: - Private members

    private final Resources resources;
    private final RouteModel model;
    private final RouteNavigationViewModelDelegate delegate;
    private final Context context;

    private final LocationService locationService;
    private final MusicPlayer musicPlayer;
    private boolean hasCompletedRoute;

    private final String TAG = getClass().getSimpleName();

    // MARK: - Lifecycle methods

    public RouteNavigationViewModel(final Context context,
                                    final RouteModel model,
                                    final RouteNavigationViewModelDelegate delegate) {
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
        locationService.stopLocationUpdates();
        locationService.removeSubscriber(this);
        MainActivity.getSensorService().removePedometerSubscriber(this);
        musicPlayer.removeSubscriber(this);
    }

    // MARK: - Private methods

    private LocationService getLocationService() {
        return BuildConfig.DEBUG
                ? new DynamicLocationServiceImpl(
                MainActivity.getLocationService(),
                new BrunoBot(model)
        )
                : MainActivity.getLocationService();
    }

    private MusicPlayer getMusicPlayer() {
        return BuildConfig.DEBUG
                ? new DynamicMusicPlayerImpl(
                MainActivity.getSpotifyService().getPlayerService(),
                new MockMusicPlayerImpl()
        )
                : MainActivity.getSpotifyService().getPlayerService();
    }

    private void setupUI() {
        int userAvatarDrawableResourceId = MainActivity.getPreferencesStorage()
                .getInt(PreferencesStorage.KEYS.USER_AVATAR, PreferencesStorage.DEFAULT_AVATAR);
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
        float bearing = !model.hasCompletedAllCheckpoints()
                ? model.getCurrentLocation().bearingTo(model.getCheckpoint().getLocation())
                : model.getCurrentLocation().getBearing();

        delegate.animateCamera(
                model.getCurrentCoordinate().getLatLng(),
                bearing,
                CAMERA_TILT,
                CAMERA_ZOOM
        );

        musicPlayer.getPlaybackPosition(new Callback<>() {
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
        } else {
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
                (dialogInterface, i) -> {
                    model.stopRouteNavigation();
                    delegate.navigateToPreviousScreen();
                },
                false
        );
    }

    /*
        NOTE: Spotify retains and reuses the onFailed callback from connectPlayer to handle connection
              errors during the entirety of the run, even after the connection process has ended.
     */
    private void connectPlayer(final Context context, final NoFailCallback<Void> callback) {
        showPlayerConnectProgressDialog();

        musicPlayer.connect(context, new Callback<>() {
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
        int userPlaylistDistance = (int) model.getDistanceBetweenUserAndPlaylist(playbackPosition);

        if (userPlaylistDistance < 0) {
            delegate.updateDistanceBetweenUserAndPlaylist(
                    -userPlaylistDistance + " m",
                    ResourcesCompat.getDrawable(resources, R.drawable.ic_angle_double_down, null),
                    resources.getColor(R.color.colorPrimary, null));
        } else {
            delegate.updateDistanceBetweenUserAndPlaylist(
                    userPlaylistDistance + " m",
                    ResourcesCompat.getDrawable(resources, R.drawable.ic_angle_double_up, null),
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
        musicPlayer.getPlaybackPosition(new Callback<>() {
            @Override
            public void onSuccess(Long playbackPosition) {
                BrunoPlaylist playlist;

                try {
                    playlist = FileStorage.readFileAsSerializable(
                            context,
                            FileStorage.KEYS.FALLBACK_PLAYLIST
                    );

                    // Don't use a playlist with no tracks
                    if (playlist.isEmpty()) {
                        handleFallbackFailed();
                        return;
                    }
                } catch (Exception e) {
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
