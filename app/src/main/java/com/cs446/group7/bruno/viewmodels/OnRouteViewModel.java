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

    // MARK: - Private members

    private Resources resources;
    private RouteModel model;
    private OnRouteViewModelDelegate delegate;

    // MARK: - Lifecycle methods

    public OnRouteViewModel(final Context context,
                            final RouteModel model,
                            final OnRouteViewModelDelegate delegate) {
        this.resources = context.getResources();
        this.model = model;
        this.delegate = delegate;

        MainActivity.getLocationService().addSubscriber(this);
        MainActivity.getLocationService().startLocationUpdates();
        MainActivity.getSensorService().addPedometerSubscriber(this);

        setupUI();
        connectToSpotify(context);
    }

    public void onDestroy() {
        MainActivity.getLocationService().stopLocationUpdates();
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
        delegate.showAllCheckPoints(model.getRouteCheckpoints());
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

        final LatLng currentCheckpoint = model.getCurrentCheckpoint();
        delegate.updateCheckpointMarker(currentCheckpoint);

        final Location currLocation = model.getCurrentLocation();
        final LatLng currLatLng = new LatLng(currLocation.getLatitude(), currLocation.getLongitude());

        /*
            Set a tolerance radius depending on how fast the user is moving. The faster they are, the more
            margin we should give them
         */
        final double toleranceRadius = 3; //currLocation.getSpeed();
        final double distanceFromCheckpoint = LatLngUtils.getLatLngDistanceInMetres(currLatLng, currentCheckpoint);

        if (distanceFromCheckpoint <= toleranceRadius) {
            final LatLng nextCheckpoint = model.advanceCheckpoint();

            // End of route, no more checkpoints
            if (nextCheckpoint == null) {
                Log.i(getClass().getSimpleName(), "You finished the run!");
                onRouteCompleted();
            } else {
                delegate.updateCheckpointMarker(nextCheckpoint);
            }
        }
    }

    public void onRouteCompleted() {
        disconnectFromSpotify();
        delegate.showAlertDialog(
                "Route Completed!",
                "Hooray! You have compleleted your exercise. You can see how you did under in the Fitness Records tab.",
                resources.getString(R.string.ok_button),
                (dialogInterface, i) -> {
                    delegate.navigateToPreviousScreen();
                },
                true
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
