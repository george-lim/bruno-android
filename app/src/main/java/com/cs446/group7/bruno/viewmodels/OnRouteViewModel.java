package com.cs446.group7.bruno.viewmodels;

import android.content.Context;
import android.content.res.Resources;
import android.location.Location;
import android.util.Log;

import androidx.annotation.NonNull;

import com.cs446.group7.bruno.BuildConfig;
import com.cs446.group7.bruno.MainActivity;
import com.cs446.group7.bruno.R;
import com.cs446.group7.bruno.location.LocationServiceSubscriber;
import com.cs446.group7.bruno.models.RouteModel;
import com.cs446.group7.bruno.music.BrunoTrack;
import com.cs446.group7.bruno.music.player.MockMusicPlayerImpl;
import com.cs446.group7.bruno.music.player.MusicPlayer;
import com.cs446.group7.bruno.music.player.MusicPlayerError;
import com.cs446.group7.bruno.sensor.PedometerSubscriber;
import com.cs446.group7.bruno.music.player.MusicPlayerSubscriber;
import com.cs446.group7.bruno.utils.Callback;
import com.google.android.gms.maps.model.LatLng;

public class OnRouteViewModel implements LocationServiceSubscriber, MusicPlayerSubscriber, PedometerSubscriber {

    // MARK: - Constants

    private static final int CAMERA_TILT = 60;
    private static final int CAMERA_ZOOM = 18;

    // MARK: - Private members

    private Resources resources;
    private RouteModel model;
    private OnRouteViewModelDelegate delegate;

    private MusicPlayer musicPlayer;

    // MARK: - Lifecycle methods

    public OnRouteViewModel(final Context context,
                            final RouteModel model,
                            final OnRouteViewModelDelegate delegate) {
        this.resources = context.getResources();
        this.model = model;
        this.delegate = delegate;

        musicPlayer = getMusicPlayer();

        MainActivity.getLocationService().addSubscriber(this);
        MainActivity.getLocationService().startLocationUpdates();
        MainActivity.getSensorService().addPedometerSubscriber(this);
        musicPlayer.addSubscriber(this);

        setupUI();
        connectToPlayer(context);
    }

    public void onDestroy() {
        MainActivity.getLocationService().stopLocationUpdates();
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
        int userAvatarDrawableResourceId = R.drawable.ic_avatar_1;

        delegate.setupUI(userAvatarDrawableResourceId);

        BrunoTrack currentTrack = model.getCurrentTrack();

        if (currentTrack != null) {
            delegate.updateCurrentSongUI(currentTrack.name, currentTrack.album);
        }

        delegate.drawRoute(model.getRouteTrackMappings(),
                resources.getIntArray(R.array.colorRouteList));
        delegate.animateCamera(model.getCurrentLocation(), CAMERA_TILT, CAMERA_ZOOM);
    }

    private void connectToPlayer(final Context context) {
        delegate.showProgressDialog(
                resources.getString(R.string.run_preparation_title),
                resources.getString(R.string.run_preparation_message),
                false,
                false
        );

        musicPlayer.connect(context, new Callback<Void, MusicPlayerError>() {
            @Override
            public void onSuccess(Void result) {
                delegate.dismissProgressDialog();
                playPlaylist();
            }

            @Override
            public void onFailed(MusicPlayerError result) {
                delegate.dismissProgressDialog();
                Log.e(getClass().getSimpleName(), "onFailed connect: " + result.getErrorMessage());

                delegate.showAlertDialog(
                        resources.getString(R.string.player_error),
                        result.getErrorMessage(),
                        resources.getString(R.string.ok_button),
                        (dialogInterface, i) -> delegate.navigateToPreviousScreen(),
                        false
                );
            }
        });
    }

    private void playPlaylist() {
        musicPlayer.setPlayerPlaylist(RouteModel.DEFAULT_PLAYLIST_ID);
        musicPlayer.play(new Callback<Void, Exception>() {
            @Override
            public void onSuccess(Void result) { }

            @Override
            public void onFailed(Exception result) {
                Log.e(getClass().getSimpleName(), "onFailed play: " + result.getLocalizedMessage());
            }
        });
    }

    private void disconnectPlayer() {
       musicPlayer.stop(new Callback<Void, Exception>() {
            @Override
            public void onSuccess(Void result) {
                musicPlayer.disconnect();
            }

            @Override
            public void onFailed(Exception result) {
                musicPlayer.disconnect();
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
                    disconnectPlayer();
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
        model.setCurrentLocation(latlng);
        delegate.animateCamera(latlng, CAMERA_TILT, CAMERA_ZOOM);
    }

    // MARK: - MusicPlayerSubscriber methods

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
