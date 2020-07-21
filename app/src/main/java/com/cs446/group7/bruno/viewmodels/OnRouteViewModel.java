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
import com.cs446.group7.bruno.sensor.PedometerSubscriber;
import com.cs446.group7.bruno.spotify.SpotifyServiceError;
import com.cs446.group7.bruno.spotify.SpotifyServiceSubscriber;
import com.cs446.group7.bruno.utils.Callback;
import com.google.android.gms.maps.model.LatLng;

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
