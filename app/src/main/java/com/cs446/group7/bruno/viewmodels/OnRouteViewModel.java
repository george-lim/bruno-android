package com.cs446.group7.bruno.viewmodels;

import android.content.Context;
import android.content.res.Resources;
import android.location.Location;
import android.util.Log;

import com.cs446.group7.bruno.BuildConfig;
import com.cs446.group7.bruno.MainActivity;
import com.cs446.group7.bruno.R;
import com.cs446.group7.bruno.dao.FitnessDetailsDAO;
import com.cs446.group7.bruno.location.LocationServiceSubscriber;
import com.cs446.group7.bruno.models.RouteModel;
import com.cs446.group7.bruno.music.BrunoTrack;
import com.cs446.group7.bruno.music.player.MockMusicPlayerImpl;
import com.cs446.group7.bruno.music.player.MusicPlayer;
import com.cs446.group7.bruno.music.player.MusicPlayerException;
import com.cs446.group7.bruno.music.player.MusicPlayerSubscriber;
import com.cs446.group7.bruno.preferencesstorage.PreferencesStorage;
import com.cs446.group7.bruno.sensor.PedometerSubscriber;
import com.cs446.group7.bruno.settings.SettingsService;
import com.cs446.group7.bruno.utils.Callback;
import com.cs446.group7.bruno.utils.LatLngUtils;
import com.cs446.group7.bruno.utils.NoFailCallback;
import com.google.android.gms.maps.model.LatLng;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import androidx.annotation.NonNull;

public class OnRouteViewModel implements LocationServiceSubscriber, MusicPlayerSubscriber, PedometerSubscriber {

    // MARK: - Constants

    private static final int CAMERA_TILT = 60;
    private static final int CAMERA_ZOOM = 18;
    private static final int BASE_TOLERANCE_RADIUS = 10;
    private static final int EXTRA_TOLERANCE_MARGIN = 1;

    // MARK: - Private members

    private Resources resources;
    private RouteModel model;
    private OnRouteViewModelDelegate delegate;

    private MusicPlayer musicPlayer;

    // TODO: Remove this when there's a better reset logic
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
            model.setUserStartTime();
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

        delegate.setupUI(userAvatarDrawableResourceId);

        BrunoTrack currentTrack = model.getCurrentTrack();

        if (currentTrack != null) {
            delegate.updateCurrentSongUI(currentTrack.getName(), currentTrack.getArtists());
        }

        delegate.drawRoute(model.getColourizedRoute());

        checkCheckpointUpdates();
        checkRouteProgress();

        final Location currentLocation = model.getCurrentLocation();
        final float bearing = currentLocation.getBearing();
        delegate.animateCamera(LatLngUtils.locationToLatLng(currentLocation), bearing, CAMERA_TILT, CAMERA_ZOOM);
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

    private void checkCheckpointUpdates() {
        // Fail-safe
        if (model.getColourizedRoute().getSegments().isEmpty()) {
            Log.e(getClass().getSimpleName(), "Checkpoints not found when checking checkpoint updates");
            return;
        }

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

        final LatLng currentCheckpoint = model.getCurrentCheckpoint();

        // Note: the radius drawn on UI is always constant as we cannot foresee the other location variables, it's just
        // to give an idea where the user should be around
        delegate.updateCheckpointMarker(currentCheckpoint, BASE_TOLERANCE_RADIUS);

        final LatLng currLatLng = LatLngUtils.locationToLatLng(currentLocation);

        final double distanceFromCheckpoint = LatLngUtils.getLatLngDistanceInMetres(currLatLng, currentCheckpoint);

        // Checkpoint is counted if and only if  user is within the tolerance radius;
        // this is calculated dynamically as the location updates, which may be larger than what is drawn
        if (distanceFromCheckpoint <= toleranceRadius) {
            // trackEndpoints are a subset of checkpoints
            if (model.getCurrentCheckpoint().equals(model.getCurrentTrackEndpoint())) {
                model.advanceTrackEndpoint();
            }
            final LatLng nextCheckpoint = model.advanceCheckpoint();

            // End of route, no more checkpoints
            if (nextCheckpoint == null) {
                isRouteCompleted = true;
                onRouteCompleted();
            } else {
                delegate.updateCheckpointMarker(nextCheckpoint, toleranceRadius);
            }
        }
    }

    /**
     * Route progress refers to the distance to the next trackEndpoint, as well as how far
     * ahead or behind the song the user is based on their current speed.
     */
    private void checkRouteProgress() {
        // Fail-safe
        if (isRouteCompleted) return;

        double distanceToTrackEndpoint = model.getDistanceToTrackEndpoint();
        delegate.updateDistanceToTrackEndpoint((int)distanceToTrackEndpoint + " m");

        // placeholder display until current track is ready
        if (model.getCurrentTrack() == null) {
            delegate.updateProgressToTrackEndpoint("0 m",
                    resources.getDrawable(R.drawable.ic_angle_double_up, null),
                    resources.getColor(R.color.colorSecondaryVariant, null));
            return;
        }

        musicPlayer.getPlaybackPosition(new Callback<Long, Throwable>() {
            @Override
            public void onSuccess(Long playbackPosition) {
                long songDurationToEndpoint = model.getCurrentTrack().getDuration() - playbackPosition;
                // expectedDistance is the predicted distance the user will travel before the current song ends
                double expectedDistance = model.getCurrentLocation().getSpeed() * (songDurationToEndpoint / 1000d);
                int diff = (int)(expectedDistance - distanceToTrackEndpoint);

                if (diff < 0) {
                    delegate.updateProgressToTrackEndpoint(
                            -diff + " m",
                            resources.getDrawable(R.drawable.ic_angle_double_down, null),
                            resources.getColor(R.color.colorPrimary, null));
                } else {
                    delegate.updateProgressToTrackEndpoint(
                            diff + " m",
                            resources.getDrawable(R.drawable.ic_angle_double_up, null),
                            resources.getColor(R.color.colorSecondaryVariant, null));
                }
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

    /**
     * Logic when the route is completed goes here.
     */
    private void onRouteCompleted() {
        model.setUserStopTime();
        musicPlayer.stopAndDisconnect();

        // TODO: save to fitness records
        final long userDuration = model.getUserDuration();
        final Locale locale = resources.getConfiguration().locale;

        final FitnessDetailsDAO fitnessDetailsData = new FitnessDetailsDAO(
                model.getMode() == RouteModel.Mode.RUN ? FitnessDetailsDAO.Mode.RUN : FitnessDetailsDAO.Mode.WALK,
                model.getUserStartTime(),
                model.getUserDuration(),
                1000, // TODO: add
                1000, // TODO: add
                model.getSteps(),
                model.getAllTracks(),
                model.getColourizedRoute()
        );

        // PersistenceService.store(...)

        // TODO: Remove this after persistence is done
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM d • h:mm aa", locale);

        Date startTime = model.getUserStartTime();

        Log.i(getClass().getSimpleName(), String.format("Exercise Start: %s", dateFormat.format(startTime)));
        Log.i(getClass().getSimpleName(), String.format("Exercise duration: %s seconds", userDuration / 1000d));


        model.hardReset();

        delegate.updateDistanceToTrackEndpoint("0 m");
        delegate.updateProgressToTrackEndpoint("0 m",
                resources.getDrawable(R.drawable.ic_angle_double_up, null),
                resources.getColor(R.color.colorSecondaryVariant, null));

        // TODO: Currently temporary; in the future we will probably take the user to the fitness details of this run
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
        delegate.animateCamera(LatLngUtils.locationToLatLng(location), location.getBearing(), CAMERA_TILT, CAMERA_ZOOM);
        checkCheckpointUpdates();
        checkRouteProgress();
    }

    // MARK: - MusicPlayerSubscriber methods

    @Override
    public void onTrackChanged(BrunoTrack track) {
        model.setCurrentTrack(track);
        delegate.updateCurrentSongUI(track.getName(), track.getArtists());
        delegate.showRouteInfoCard();
    }

    // MARK: - PedometerSubscriber methods

    @Override
    public void didStep(long timestamp) {
        model.incrementStep();
    }
}
