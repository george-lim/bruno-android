package com.cs446.group7.bruno.viewmodels;

import android.content.Context;
import android.content.res.Resources;
import android.location.Location;
import android.util.Log;

import com.cs446.group7.bruno.BuildConfig;
import com.cs446.group7.bruno.MainActivity;
import com.cs446.group7.bruno.R;
import com.cs446.group7.bruno.location.Coordinate;
import com.cs446.group7.bruno.location.LocationServiceSubscriber;
import com.cs446.group7.bruno.models.RouteModel;
import com.cs446.group7.bruno.music.BrunoPlaylist;
import com.cs446.group7.bruno.music.BrunoTrack;
import com.cs446.group7.bruno.music.player.MockMusicPlayerImpl;
import com.cs446.group7.bruno.music.player.MusicPlayer;
import com.cs446.group7.bruno.music.player.MusicPlayerException;
import com.cs446.group7.bruno.music.player.MusicPlayerSubscriber;
import com.cs446.group7.bruno.storage.PreferencesStorage;
import com.cs446.group7.bruno.sensor.PedometerSubscriber;
import com.cs446.group7.bruno.settings.SettingsService;
import com.cs446.group7.bruno.utils.Callback;
import com.cs446.group7.bruno.utils.NoFailCallback;

import androidx.annotation.NonNull;

public class OnRouteViewModel implements LocationServiceSubscriber, MusicPlayerSubscriber, PedometerSubscriber {

    // MARK: - Constants

    private static final int CAMERA_TILT = 60;
    private static final int CAMERA_ZOOM = 18;
    private static final int BASE_TOLERANCE_RADIUS = 10;
    private static final int EXTRA_TOLERANCE_MARGIN = 1;

    // MARK: - Private members

    // DEBUG ONLY
    private static final int NUM_DEBUG_CHECKPOINTS = 5;
    private int debugCheckpointIndex;

    private Resources resources;
    private RouteModel model;
    private OnRouteViewModelDelegate delegate;

    private MusicPlayer musicPlayer;

    private boolean isRouteCompleted;

    // MARK: - Lifecycle methods

    public OnRouteViewModel(final Context context,
                            final RouteModel model,
                            final OnRouteViewModelDelegate delegate) {
        this.resources = context.getResources();
        this.model = model;
        this.delegate = delegate;
        this.isRouteCompleted = false;

        musicPlayer = getMusicPlayer();
        musicPlayer.setPlayerPlaylist(model.getPlaylist());

        MainActivity.getLocationService().addSubscriber(this);
        MainActivity.getLocationService().startLocationUpdates();
        MainActivity.getSensorService().addPedometerSubscriber(this);
        musicPlayer.addSubscriber(this);

        setupUI();

        // Connect player, and play playlist after connection succeeds
        connectPlayer(context, result -> {
            model.startRouteNavigation();
            musicPlayer.play();
        });
    }

    public void onDestroy() {
        MainActivity.getLocationService().removeSubscriber(this);
        MainActivity.getSensorService().removePedometerSubscriber(this);
        musicPlayer.removeSubscriber(this);
    }

    // MARK: - Private methods

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

        delegate.drawRoute(model.getTrackSegments());

        refreshUI();
    }

    private void refreshUI() {
        delegate.animateCamera(
                model.getCurrentCoordinate().getLatLng(),
                model.getCurrentLocation().getBearing(),
                CAMERA_TILT,
                CAMERA_ZOOM
        );

        updateDistanceBetweenUserAndPlaylist();
        updateDistanceToCheckpoint();
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
                Log.e(getClass().getSimpleName(), "onFailed connect: " + errorMessage);

                dismissPlayerConnectProgressDialog();
                showPlayerConnectFailureDialog(errorMessage);
            }
        });
    }

    private void updateDistanceBetweenUserAndPlaylist() {
        // Fail-safe
        if (isRouteCompleted) return;

        // placeholder display until current track is ready
        if (model.getCurrentTrack() == null) {
            delegate.updateDistanceBetweenUserAndPlaylist("0 m",
                    resources.getDrawable(R.drawable.ic_angle_double_up, null),
                    resources.getColor(R.color.colorSecondary, null));
            return;
        }

        musicPlayer.getPlaybackPosition(new Callback<Long, Throwable>() {
            @Override
            public void onSuccess(Long playbackPosition) {
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

                updateBrunoCoordinate(playbackPosition);
            }

            @Override
            public void onFailed(Throwable error) {
                Log.e(getClass().getSimpleName(),
                        error.getLocalizedMessage() == null
                                ? "Error occurred when getting playback position"
                                : error.getLocalizedMessage());
            }
        });
    }

    private void updateBrunoCoordinate(long playbackPosition) {
        final Coordinate brunoCoordinate = model.getPlaylistRouteCoordinate(playbackPosition);

        // Fail-safe
        if (brunoCoordinate == null) return;

        delegate.updateBrunoMarker(brunoCoordinate.getLatLng());
    }

    private void updateDistanceToCheckpoint() {
        /*
            Set a tolerance radius depending on how fast the user is moving. The faster they are, the more
            margin we should give them. It should also depend on how accurate the GPS is, the more variance, the bigger
            the margin should be given.
         */

        final Location currentLocation = model.getCurrentLocation();

        // give extra tolerance if the user is moving faster as their location is more uncertain
        final double speedMargin = Math.min(SettingsService.PREFERRED_RUNNING_SPEED / 60 + EXTRA_TOLERANCE_MARGIN,
                currentLocation.getSpeed());

        // max amount of deviation from the actual location (meters)
        final double accuracyDeviation = currentLocation.getAccuracy();

        // total tolerance radius
        final double toleranceRadius = BASE_TOLERANCE_RADIUS + speedMargin + accuracyDeviation;

        final Coordinate currentCheckpoint = model.getCheckpoint();

        // Note: the radius drawn on UI is always constant as we cannot foresee the other location variables, it's just
        // to give an idea where the user should be around
        delegate.updateCheckpointMarker(currentCheckpoint.getLatLng(), BASE_TOLERANCE_RADIUS);

        final double distanceFromCheckpoint = model.getCurrentCoordinate().getDistance(currentCheckpoint);

        // Checkpoint is counted if and only if  user is within the tolerance radius;
        // this is calculated dynamically as the location updates, which may be larger than what is drawn
        if (BuildConfig.DEBUG || distanceFromCheckpoint <= toleranceRadius) {
            model.advanceCheckpoint();

            if (model.hasCompletedAllCheckpoints() || (BuildConfig.DEBUG && debugCheckpointIndex > NUM_DEBUG_CHECKPOINTS)) {
                isRouteCompleted = true;
                stopRouteNavigation(result -> onRouteCompleted());
            }
            else {
                delegate.updateCheckpointMarker(model.getCheckpoint().getLatLng(), toleranceRadius);
            }
            debugCheckpointIndex++;
        }

        double distanceToCheckpoint = model.getDistanceToCheckpoint();
        delegate.updateDistanceToCheckpoint((int)distanceToCheckpoint + " m");
    }

    private void handlePlaylistChange(final BrunoPlaylist playlist, long playbackPosition) {
        musicPlayer.stop();
        musicPlayer.setPlayerPlaylist(playlist);

        model.mergePlaylist(playlist, playbackPosition);
        delegate.clearMap();
        delegate.drawRoute(model.getTrackSegments());
        refreshUI();

        musicPlayer.play();
    }

    // Final model changes before route completion
    private void stopRouteNavigation(final NoFailCallback<Void> callback) {
        musicPlayer.getPlaybackPosition(new Callback<Long, Throwable>() {
            @Override
            public void onSuccess(Long result) {
                model.stopRouteNavigation(result);
                model.hardReset();
                callback.onSuccess(null);
            }

            @Override
            public void onFailed(Throwable result) {
                model.stopRouteNavigation(0);
                model.hardReset();
                callback.onSuccess(null);
            }
        });
    }

    /**
     * Logic when the route is completed goes here.
     */
    private void onRouteCompleted() {
        musicPlayer.stopAndDisconnect();
        delegate.updateDistanceBetweenUserAndPlaylist("0 m",
                resources.getDrawable(R.drawable.ic_angle_double_up, null),
                resources.getColor(R.color.colorSecondary, null));
        delegate.updateDistanceToCheckpoint("0 m");

        delegate.showAlertDialog(
                resources.getString(R.string.run_completion_title),
                resources.getString(R.string.run_completion_message),
                resources.getString(R.string.ok_button),
                (dialogInterface, i) -> delegate.navigateToPreviousScreen(),
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
                    model.softReset();
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
        if (isRouteCompleted) return;
        model.setCurrentLocation(location);
        refreshUI();
    }

    // MARK: - MusicPlayerSubscriber methods

    @Override
    public void onTrackChanged(BrunoTrack track) {
        model.setCurrentTrack(track);
        delegate.updateCurrentSongUI(track.getName(), track.getArtists());
        delegate.showRouteInfoCard();
    }

    @Override
    public void onFallback() {
        // TODO: Add logic here once the fallback playlist is retrievable from storage
    }

    // MARK: - PedometerSubscriber methods

    @Override
    public void didStep(long timestamp) {
        model.incrementStep();
    }
}
