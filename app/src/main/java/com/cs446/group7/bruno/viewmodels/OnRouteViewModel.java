package com.cs446.group7.bruno.viewmodels;

import android.content.Context;
import android.content.res.Resources;
import android.location.Location;
import android.util.Log;

import androidx.annotation.NonNull;

import com.cs446.group7.bruno.MainActivity;
import com.cs446.group7.bruno.R;
import com.cs446.group7.bruno.location.LocationServiceSubscriber;
import com.cs446.group7.bruno.models.RouteModel;
import com.cs446.group7.bruno.music.BrunoTrack;
import com.cs446.group7.bruno.routing.RouteTrackMapping;
import com.cs446.group7.bruno.sensor.PedometerSubscriber;
import com.cs446.group7.bruno.spotify.SpotifyServiceError;
import com.cs446.group7.bruno.spotify.SpotifyServiceSubscriber;
import com.cs446.group7.bruno.utils.Callback;
import com.cs446.group7.bruno.utils.LatLngUtils;
import com.google.android.gms.maps.model.LatLng;

import java.util.List;

public class OnRouteViewModel implements LocationServiceSubscriber, SpotifyServiceSubscriber, PedometerSubscriber {

    // MARK: - Constants

    private static final int CAMERA_TILT = 60;
    private static final int CAMERA_ZOOM = 18;
    private static final int BASE_TOLERANCE_RADIUS = 5;

    // MARK: - Private members

    private Resources resources;
    private RouteModel model;
    private OnRouteViewModelDelegate delegate;
    private boolean isCompleted;

    // MARK: - Lifecycle methods

    public OnRouteViewModel(final Context context,
                            final RouteModel model,
                            final OnRouteViewModelDelegate delegate) {
        this.resources = context.getResources();
        this.model = model;
        this.delegate = delegate;
        this.isCompleted = false;

        MainActivity.getLocationService().addSubscriber(this);
        MainActivity.getLocationService().startLocationUpdates();
        MainActivity.getSensorService().addPedometerSubscriber(this);

        setupUI();
        connectToSpotify(context);
    }

    public void onDestroy() {
        MainActivity.getLocationService().removeSubscriber(this);
        MainActivity.getSpotifyService().removeSubscriber(this);
        MainActivity.getSensorService().removePedometerSubscriber(this);
    }

    // MARK: - Private methods

    private void setupUI() {
        int userAvatarDrawableResourceId = R.drawable.ic_avatar_1;

        delegate.setupUI(userAvatarDrawableResourceId);

        BrunoTrack currentTrack = model.getCurrentTrack();

        if (currentTrack != null) {
            delegate.updateCurrentSongUI(currentTrack.name, currentTrack.album);
        }

        delegate.drawRoute(model.getRouteTrackMappings(),
                resources.getIntArray(R.array.colorRouteList));

        checkCheckpointUpdates();

        final float bearing = model.getCurrentLocation().getBearing();
        final LatLng currentLatLng = new LatLng(model.getCurrentLocation().getLatitude(),
                model.getCurrentLocation().getLongitude());

        delegate.animateCamera(currentLatLng, bearing, CAMERA_TILT, CAMERA_ZOOM);
    }

    private void connectToSpotify(final Context context) {
        if (MainActivity.getSpotifyService().isConnected()) {
            playSpotifyPlaylist();
            return;
        }

        delegate.showProgressDialog(
                resources.getString(R.string.run_preparation_title),
                resources.getString(R.string.run_preparation_message),
                false,
                false
        );

        MainActivity.getSpotifyService().connect(context, new Callback<Void, SpotifyServiceError>() {
            @Override
            public void onSuccess(Void result) {
                delegate.dismissProgressDialog();
                playSpotifyPlaylist();
            }

            @Override
            public void onFailed(SpotifyServiceError result) {
                delegate.dismissProgressDialog();
                Log.e(getClass().getSimpleName(), "onFailed connect: " + result.getErrorMessage());

                delegate.showAlertDialog(
                        resources.getString(R.string.spotify_error),
                        result.getErrorMessage(),
                        resources.getString(R.string.ok_button),
                        (dialogInterface, i) -> delegate.navigateToPreviousScreen(),
                        false
                );
            }
        });
    }

    private void playSpotifyPlaylist() {
        MainActivity.getSpotifyService().setPlayerPlaylist(RouteModel.DEFAULT_PLAYLIST_ID);
        MainActivity.getSpotifyService().play(new Callback<Void, Exception>() {
            @Override
            public void onSuccess(Void result) {
                MainActivity.getSpotifyService().addSubscriber(OnRouteViewModel.this);
            }

            @Override
            public void onFailed(Exception result) {
                Log.e(getClass().getSimpleName(), "onFailed play: " + result.getLocalizedMessage());
            }
        });
    }

    private void disconnectFromSpotify() {
        if (!MainActivity.getSpotifyService().isConnected()) {
            return;
        }

        MainActivity.getSpotifyService().pause(new Callback<Void, Exception>() {
            @Override
            public void onSuccess(Void result) {
                MainActivity.getSpotifyService().disconnect();
            }

            @Override
            public void onFailed(Exception result) {
                MainActivity.getSpotifyService().disconnect();
            }
        });
    }

    private void checkCheckpointUpdates() {
        final List<RouteTrackMapping> routeTrackMapping = model.getRouteTrackMappings();
        if (routeTrackMapping.isEmpty()) {
            Log.w(getClass().getSimpleName(), "RouteTrackMapping is empty! No checkpoints generated");
            return;
        }

        /*
            Set a tolerance radius depending on how fast the user is moving. The faster they are, the more
            margin we should give them. It should also depend on how accurate the GPS is, the more variance, the bigger
            the margin should be given.
         */

        final Location currentLocation = model.getCurrentLocation();

        // adjustments to speed within [0, 3] m/s, chosen based on the fact that people run around 2-3 m/s
        final double speedMargin = Math.min(3, currentLocation.getSpeed());

        // max amount of deviation from the actual location (meters)
        final double accuracyDeviation = currentLocation.getAccuracy();

        // total tolerance radius
        final double toleranceRadius = BASE_TOLERANCE_RADIUS + speedMargin + accuracyDeviation;

        final LatLng currentCheckpoint = model.getCurrentCheckpoint();

        // Note: the radius drawn on UI is always constant as we cannot foresee the other location variables, it's just
        // to give an idea where the user should be around
        delegate.updateCheckpointMarker(currentCheckpoint, BASE_TOLERANCE_RADIUS + 3);

        final LatLng currLatLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());

        final double distanceFromCheckpoint = LatLngUtils.getLatLngDistanceInMetres(currLatLng, currentCheckpoint);

        // Checkpoint is counted if and only if  user is within the tolerance radius;
        // this is calculated dynamically as the location updates, which may be larger than what is drawn
        if (distanceFromCheckpoint <= toleranceRadius) {
            final LatLng nextCheckpoint = model.advanceCheckpoint();

            // End of route, no more checkpoints
            if (nextCheckpoint == null) {
                isCompleted = true;
                onRouteCompleted();
            } else {
                delegate.updateCheckpointMarker(nextCheckpoint, toleranceRadius);
            }
        }
    }

    public void onRouteCompleted() {
        disconnectFromSpotify();
        delegate.showAlertDialog(
                resources.getString(R.string.run_completion_title),
                resources.getString(R.string.run_completion_message),
                resources.getString(R.string.ok_button),
                (dialogInterface, i) -> {
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
                    disconnectFromSpotify();
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
        if (isCompleted) return;
        LatLng latlng = new LatLng(location.getLatitude(), location.getLongitude());
        model.setCurrentLocation(location);
        delegate.animateCamera(latlng, location.getBearing(), CAMERA_TILT, CAMERA_ZOOM);
        checkCheckpointUpdates();
    }

    // MARK: - SpotifyServiceSubscriber methods

    @Override
    public void onTrackChanged(BrunoTrack track) {
        model.setCurrentTrack(track);
        delegate.updateCurrentSongUI(track.name, track.album);
    }

    // MARK: - PedometerSubscriber methods

    @Override
    public void didStep(long timestamp) {
        model.incrementStep();
    }
}
