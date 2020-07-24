package com.cs446.group7.bruno.viewmodels;

import android.content.Context;
import android.content.res.Resources;
import android.location.Location;
import android.util.Log;

import androidx.annotation.NonNull;

import com.cs446.group7.bruno.BuildConfig;
import com.cs446.group7.bruno.MainActivity;
import com.cs446.group7.bruno.R;
import com.cs446.group7.bruno.capability.Capability;
import com.cs446.group7.bruno.location.LocationServiceSubscriber;
import com.cs446.group7.bruno.models.RouteModel;
import com.cs446.group7.bruno.music.BrunoPlaylist;
import com.cs446.group7.bruno.music.playlist.MockPlaylistGeneratorImpl;
import com.cs446.group7.bruno.music.playlist.PlaylistGenerator;
import com.cs446.group7.bruno.preferencesstorage.PreferencesStorage;
import com.cs446.group7.bruno.routing.MockRouteGeneratorImpl;
import com.cs446.group7.bruno.routing.OnRouteResponseCallback;
import com.cs446.group7.bruno.routing.Route;
import com.cs446.group7.bruno.routing.RouteGenerator;
import com.cs446.group7.bruno.routing.RouteGeneratorError;
import com.cs446.group7.bruno.routing.RouteGeneratorImpl;
import com.cs446.group7.bruno.routing.RouteProcessor;
import com.cs446.group7.bruno.routing.RouteTrackMapping;
import com.cs446.group7.bruno.settings.SettingsService;
import com.cs446.group7.bruno.spotify.SpotifyService;
import com.cs446.group7.bruno.utils.Callback;
import com.cs446.group7.bruno.utils.NoFailCallback;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

public class RoutePlanningViewModel implements LocationServiceSubscriber, OnRouteResponseCallback {

    // MARK: - Constants

    private static final Capability[] REQUIRED_CAPABILITIES
            = { Capability.LOCATION, Capability.INTERNET };

    // MARK: - Private members

    private Resources resources;
    private RouteModel model;
    private RoutePlanningViewModelDelegate delegate;

    private RouteGenerator routeGenerator;
    private PlaylistGenerator playlistGenerator;

    private boolean isRequestingCapabilities = false;
    private boolean hasStartedLocationUpdates = false;

    // MARK: - Lifecycle methods

    public RoutePlanningViewModel(final Context context,
                                  final RouteModel model,
                                  final RoutePlanningViewModelDelegate delegate) {
        this.resources = context.getResources();
        this.model = model;
        this.delegate = delegate;

        routeGenerator = getRouteGenerator(context);
        playlistGenerator = getPlaylistGenerator(context);

        MainActivity.getLocationService().addSubscriber(this);

        setupUI();

        startLocationUpdates(result -> {
            if (!hasColourizedRoute()) {
                processColourizedRoute();
            }
        });
    }

    public void onDestroy() {
        MainActivity.getLocationService().stopLocationUpdates();
        MainActivity.getLocationService().removeSubscriber(this);
    }

    // MARK: - Private methods

    private RouteGenerator getRouteGenerator(final Context context) {
        String googleMapsKey = resources.getString(R.string.google_maps_key);
        return BuildConfig.DEBUG
                ? new MockRouteGeneratorImpl(context, googleMapsKey)
                : new RouteGeneratorImpl(context, googleMapsKey);
    }

    private PlaylistGenerator getPlaylistGenerator(final Context context) {
        return BuildConfig.DEBUG
                ? new MockPlaylistGeneratorImpl()
                : new SpotifyService(context);
    }

    private boolean hasColourizedRoute() {
        return model.getRoute() != null && model.getPlaylist() != null;
    }

    private void setupUI() {
        boolean isEveryCapabilityEnabled = MainActivity
                .getCapabilityService()
                .isEveryCapabilityEnabled(REQUIRED_CAPABILITIES);

        String startBtnText = hasColourizedRoute() || isEveryCapabilityEnabled
                ? resources.getString(R.string.route_planning_start)
                : resources.getString(R.string.route_planning_create_route);

        String[] durationPickerDisplayedValues = new String[RouteModel.DURATIONS_IN_MINUTES.length];

        for (int i = 0; i < RouteModel.DURATIONS_IN_MINUTES.length; ++i) {
            durationPickerDisplayedValues[i] = Integer.toString(RouteModel.DURATIONS_IN_MINUTES[i]);
        }

        PreferencesStorage pref = MainActivity.getPreferencesStorage();
        int userAvatarDrawableResourceId = pref.getInt(PreferencesStorage.USER_AVATAR, R.drawable.ic_avatar_1);

        delegate.setupUI(
                startBtnText,
                model.getMode() == RouteModel.Mode.WALK,
                durationPickerDisplayedValues,
                0,
                RouteModel.DURATIONS_IN_MINUTES.length - 1,
                model.getDurationIndex(),
                userAvatarDrawableResourceId
        );

        if (hasColourizedRoute()) {
            onProcessColourizedRouteSuccess();
        }

        LatLng currentLocation = model.getCurrentLocation();

        if (currentLocation != null) {
            delegate.moveUserMarker(currentLocation);
        }
    }

    private void startLocationUpdates(NoFailCallback<Void> callback) {
        if (hasStartedLocationUpdates) {
            return;
        }

        boolean isEveryCapabilityEnabled = MainActivity
                .getCapabilityService()
                .isEveryCapabilityEnabled(REQUIRED_CAPABILITIES);

        if (!isEveryCapabilityEnabled) {
            return;
        }

        MainActivity.getLocationService().startLocationUpdates(location -> {
            hasStartedLocationUpdates = true;
            onLocationUpdate(location);
            callback.onSuccess(null);
        });
    }

    private void generatePlaylist() {
        if (model.getPlaylist() != null) {
            if (hasColourizedRoute()) {
                onProcessColourizedRouteSuccess();
            }

            return;
        }

        playlistGenerator.getPlaylist(RouteModel.DEFAULT_PLAYLIST_ID, new Callback<BrunoPlaylist, Exception>() {
            @Override
            public void onSuccess(BrunoPlaylist playlist) {
                model.setPlaylist(playlist);

                if (hasColourizedRoute()) {
                    onProcessColourizedRouteSuccess();
                }
            }

            @Override
            public void onFailed(Exception e) {
                onProcessColourizedRouteFailure();
                delegate.showRouteProcessingError(resources.getString(R.string.route_planning_playlist_error));
                Log.e(getClass().getSimpleName(), e.getLocalizedMessage());
            }
        });
    }

    private void generateRoute() {
        if (model.getCurrentLocation() == null) {
            return;
        }

        double speed = model.getMode() == RouteModel.Mode.WALK
                ? SettingsService.PREFERRED_WALKING_SPEED
                : SettingsService.PREFERRED_RUNNING_SPEED;
        double totalDistance = model.getDurationInMinutes() * speed;
        double rotation = Math.random() * 2 * Math.PI;

        routeGenerator.generateRoute(
                RoutePlanningViewModel.this,
                model.getCurrentLocation(),
                totalDistance,
                rotation
        );
    }

    private List<RouteTrackMapping> mapRouteToTracks(final Route route, final BrunoPlaylist playlist) {
        List<RouteTrackMapping> routeTrackMappings;

        try {
            routeTrackMappings = RouteProcessor.execute(route.getRouteSegments(), playlist);
        } catch (RouteProcessor.TrackIndexOutOfBoundsException e) {
            // should never reach here because we assume the playlist will always be long enough
            routeTrackMappings = new ArrayList<>();
        }

        return routeTrackMappings;
    }

    private void processColourizedRoute() {
        model.setRoute(null);
        delegate.updateStartBtnEnabled(false);

        generatePlaylist();
        generateRoute();
    }

    private void onProcessColourizedRouteSuccess() {
        final List<RouteTrackMapping> routeTrackMappings = mapRouteToTracks(model.getRoute(), model.getPlaylist());
        model.setRouteTrackMappings(routeTrackMappings);

        delegate.updateStartBtnText(resources.getString(R.string.route_planning_start));
        delegate.clearMap();
        delegate.drawRoute(routeTrackMappings, resources.getIntArray(R.array.colorRouteList));
        delegate.moveUserMarker(model.getCurrentLocation());
        delegate.updateStartBtnEnabled(true);
    }

    private void onProcessColourizedRouteFailure() {
        model.setRoute(null);
        model.setRouteTrackMappings(null);

        delegate.updateStartBtnText(resources.getString(R.string.route_planning_create_route));
        delegate.clearMap();
        delegate.moveUserMarker(model.getCurrentLocation());
        delegate.updateStartBtnEnabled(true);
    }

    // MARK: - User action handlers

    public void handleStartWalkingClick() {
        if (isRequestingCapabilities) return;
        isRequestingCapabilities = true;

        MainActivity.getCapabilityService().request(REQUIRED_CAPABILITIES, new Callback<Void, Void>() {
            @Override
            public void onSuccess(Void result) {
                if (hasColourizedRoute()) {
                    delegate.navigateToNextScreen();
                }
                else if (!hasStartedLocationUpdates) {
                    startLocationUpdates(nextResult -> {
                        if (!hasColourizedRoute()) {
                            processColourizedRoute();
                        }
                    });
                }
                else {
                    processColourizedRoute();
                }

                isRequestingCapabilities = false;
            }

            @Override
            public void onFailed(Void result) {
                isRequestingCapabilities = false;
            }
        });
    }

    public void handleWalkingModeClick() {
        if (model.getMode() == RouteModel.Mode.WALK) {
            return;
        }

        model.setMode(RouteModel.Mode.WALK);
        delegate.updateSelectedModeBtn(model.getMode() == RouteModel.Mode.WALK);

        processColourizedRoute();
    }

    public void handleRunningModeClick() {
        if (model.getMode() == RouteModel.Mode.RUN) {
            return;
        }

        model.setMode(RouteModel.Mode.RUN);
        delegate.updateSelectedModeBtn(model.getMode() == RouteModel.Mode.WALK);

        processColourizedRoute();
    }

    public void handleDurationSelected(int durationIndex) {
        if (model.getDurationIndex() == durationIndex) {
            return;
        }

        model.setDurationIndex(durationIndex);

        processColourizedRoute();
    }

    // MARK: - LocationServiceSubscriber methods

    @Override
    public void onLocationUpdate(@NonNull Location location) {
        LatLng latlng = new LatLng(location.getLatitude(), location.getLongitude());
        model.setCurrentLocation(latlng);
        delegate.moveUserMarker(latlng);
    }

    // MARK: - OnRouteResponseCallback methods

    @Override
    public void onRouteReady(final Route route) {
        model.setRoute(route);

        if (hasColourizedRoute()) {
            onProcessColourizedRouteSuccess();
        }
    }

    @Override
    public void onRouteError(final RouteGeneratorError error,
                             final Exception underlyingException) {
        onProcessColourizedRouteFailure();
        delegate.showRouteProcessingError(error.getDescription());

        final String errorMsg = underlyingException.getLocalizedMessage();
        Log.e(getClass().getSimpleName(), errorMsg == null ? "Unknown error" : errorMsg);
    }
}
