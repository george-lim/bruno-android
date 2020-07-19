package com.cs446.group7.bruno.viewmodels;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.util.Log;

import androidx.annotation.NonNull;

import com.cs446.group7.bruno.MainActivity;
import com.cs446.group7.bruno.R;
import com.cs446.group7.bruno.location.LocationServiceSubscriber;
import com.cs446.group7.bruno.models.RouteModel;
import com.cs446.group7.bruno.music.BrunoTrack;
import com.cs446.group7.bruno.spotify.SpotifyServiceError;
import com.cs446.group7.bruno.spotify.SpotifyServiceSubscriber;
import com.cs446.group7.bruno.utils.BitmapUtils;
import com.cs446.group7.bruno.utils.Callback;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;

public class OnRouteViewModel implements LocationServiceSubscriber, SpotifyServiceSubscriber {

    // MARK: - Constants

    private static final String DEFAULT_PLAYLIST_ID = "7fPwZk4KFD2yfU7J5O1JVz";
    private static final int CAMERA_TILT = 60;
    private static final int CAMERA_ZOOM = 18;

    // MARK: - Private members

    private Resources resources;
    private RouteModel model;
    private OnRouteViewModelDelegate delegate;

    private BitmapDescriptor userMarkerIcon;

    // MARK: - Lifecycle methods

    public OnRouteViewModel(final Context context,
                            final RouteModel model,
                            final OnRouteViewModelDelegate delegate) {
        this.resources = context.getResources();
        this.model = model;
        this.delegate = delegate;

        userMarkerIcon = getUserMarkerIcon();

        MainActivity.getLocationService().addSubscriber(this);
        MainActivity.getLocationService().startLocationUpdates();

        setupUI();
        connectToSpotify(context);
    }

    public void onDestroy() {
        MainActivity.getLocationService().stopLocationUpdates();
        MainActivity.getLocationService().removeSubscriber(this);

        if (MainActivity.getSpotifyService().isConnected()) {
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
    }

    // MARK: - Private methods

    private BitmapDescriptor getUserMarkerIcon() {
        Drawable avatarDrawable = resources.getDrawable(R.drawable.ic_avatar_1, null);
        return BitmapDescriptorFactory.fromBitmap(BitmapUtils.getBitmapFromVectorDrawable(avatarDrawable));
    }

    private void setupUI() {
        delegate.setupUI();

        BrunoTrack currentTrack = model.getCurrentTrack();

        if (currentTrack != null) {
            delegate.updateCurrentSongUI(currentTrack.name, currentTrack.album);
        }

        delegate.drawRoute(model.getRoute());
        delegate.animateCamera(model.getCurrentLocation(), userMarkerIcon, CAMERA_TILT, CAMERA_ZOOM);
    }

    private void connectToSpotify(final Context context) {
        delegate.showProgressDialog(
                resources.getString(R.string.run_preparation_title),
                resources.getString(R.string.run_preparation_message),
                false,
                false
        );

        OnRouteViewModel thisObject = this;

        MainActivity.getSpotifyService().connect(context, new Callback<Void, SpotifyServiceError>() {
            @Override
            public void onSuccess(Void result) {
                delegate.dismissProgressDialog();

                MainActivity.getSpotifyService().setPlayerPlaylist(DEFAULT_PLAYLIST_ID);
                MainActivity.getSpotifyService().play(new Callback<Void, Exception>() {
                    @Override
                    public void onSuccess(Void result) {
                        MainActivity.getSpotifyService().addSubscriber(thisObject);
                    }

                    @Override
                    public void onFailed(Exception result) {
                        Log.e(getClass().getSimpleName(), "onFailed play: " + result.getLocalizedMessage());
                    }
                });
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

    // MARK: - User action handlers

    public void handleExitRoute() {
        delegate.showAlertDialog(
                resources.getString(R.string.run_exit_title),
                resources.getString(R.string.run_exit_message),
                resources.getString(R.string.yes_button),
                (dialogInterface, i) -> delegate.navigateToPreviousScreen(),
                resources.getString(R.string.no_button),
                null,
                true
        );
    }

    // MARK: - LocationServiceSubscriber methods

    @Override
    public void onLocationUpdate(@NonNull Location location) {
        LatLng latlng = new LatLng(location.getLatitude(), location.getLongitude());
        model.setCurrentLocation(latlng);
        delegate.animateCamera(latlng, userMarkerIcon, CAMERA_TILT, CAMERA_ZOOM);
    }

    // MARK: - SpotifyServiceSubscriber methods

    @Override
    public void onTrackChanged(BrunoTrack track) {
        model.setCurrentTrack(track);
        delegate.updateCurrentSongUI(track.name, track.album);
    }
}
