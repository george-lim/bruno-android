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
        if (model.getPlaylist() == null) {
            requestNewPlaylist();
        }

        MainActivity.getLocationService().addSubscriber(this);

        setupUI();
        startLocationUpdates();
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

    private void setupUI() {
        boolean isEveryCapabilityEnabled = MainActivity
                .getCapabilityService()
                .isEveryCapabilityEnabled(REQUIRED_CAPABILITIES);

        String startBtnText = model.getRoute() != null || isEveryCapabilityEnabled
                ? resources.getString(R.string.route_planning_start)
                : resources.getString(R.string.route_planning_generate_route);

        String[] durationPickerDisplayedValues = new String[RouteModel.DURATIONS_IN_MINUTES.length];

        for (int i = 0; i < RouteModel.DURATIONS_IN_MINUTES.length; ++i) {
            durationPickerDisplayedValues[i] = Integer.toString(RouteModel.DURATIONS_IN_MINUTES[i]);
        }

        int userAvatarDrawableResourceId = R.drawable.ic_avatar_1;

        delegate.setupUI(
                startBtnText,
                model.getMode() == RouteModel.Mode.WALK,
                durationPickerDisplayedValues,
                0,
                RouteModel.DURATIONS_IN_MINUTES.length - 1,
                model.getDurationIndex(),
                userAvatarDrawableResourceId
        );

        Route route = model.getRoute();

        if (route != null) {
            onRouteReady(route);
        }

        final Location currentLocation = model.getCurrentLocation();
        if (currentLocation != null) {
            delegate.moveUserMarker(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()));
        }
    }

    private void startLocationUpdates() {
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

            if (model.getRoute() == null) {
                generateRoute();
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
        final Location currentLocation = model.getCurrentLocation();

        routeGenerator.generateRoute(
                RoutePlanningViewModel.this,
                new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()),
                totalDistance,
                rotation
        );
    }

    // MARK: - User action handlers

    public void handleStartWalkingClick() {
        if (isRequestingCapabilities) return;
        isRequestingCapabilities = true;

        MainActivity.getCapabilityService().request(REQUIRED_CAPABILITIES, new Callback<Void, Void>() {
            @Override
            public void onSuccess(Void result) {
                if (model.getRoute() != null) {
                    delegate.navigateToNextScreen();
                }
                else if (!hasStartedLocationUpdates) {
                    startLocationUpdates();
                }
                else {
                    generateRoute();
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
        model.setRoute(null);

        delegate.updateSelectedModeBtn(model.getMode() == RouteModel.Mode.WALK);

        generateRoute();
    }

    public void handleRunningModeClick() {
        if (model.getMode() == RouteModel.Mode.RUN) {
            return;
        }

        model.setMode(RouteModel.Mode.RUN);
        model.setRoute(null);

        delegate.updateSelectedModeBtn(model.getMode() == RouteModel.Mode.WALK);

        generateRoute();
    }

    public void handleDurationSelected(int durationIndex) {
        if (model.getDurationIndex() == durationIndex) {
            return;
        }

        model.setDurationIndex(durationIndex);
        model.setRoute(null);

        generateRoute();
    }

    // MARK: - LocationServiceSubscriber methods

    @Override
    public void onLocationUpdate(@NonNull Location location) {
        LatLng latlng = new LatLng(location.getLatitude(), location.getLongitude());
        model.setCurrentLocation(location);
        delegate.moveUserMarker(latlng);
    }

    // MARK: - OnRouteResponseCallback methods

    @Override
    public void onRouteReady(final Route route) {
        model.setRoute(route);

        if (model.getPlaylist() != null) {
            final List<RouteTrackMapping> routeTrackMappings = mapRouteToTracks(route, model.getPlaylist());
            final List<LatLng> routeCheckpoints = RouteProcessor.getCheckpoints(routeTrackMappings);

            model.setRouteTrackMappings(routeTrackMappings);
            model.setRouteCheckpoints(routeCheckpoints);
            model.setRoute(route);

            delegate.updateStartBtnText(resources.getString(R.string.route_planning_start));
            delegate.clearMap();
            delegate.drawRoute(routeTrackMappings, resources.getIntArray(R.array.colorRouteList));

            final Location currentLocation = model.getCurrentLocation();
            delegate.moveUserMarker(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()));
        }
    }

    @Override
    public void onRouteError(final RouteGeneratorError error,
                             final Exception underlyingException) {
        model.setRoute(null);
        model.setRouteTrackMappings(null);

        delegate.updateStartBtnText(resources.getString(R.string.route_planning_generate_route));
        delegate.clearMap();
        delegate.showRouteGenerationError(error.getDescription());

        final Location currentLocation = model.getCurrentLocation();
        delegate.moveUserMarker(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()));

        Log.e(getClass().getSimpleName(), underlyingException.getLocalizedMessage());
    }

    private void requestNewPlaylist() {
        playlistGenerator.getPlaylist(RouteModel.DEFAULT_PLAYLIST_ID, new Callback<BrunoPlaylist, Exception>() {
            @Override
            public void onSuccess(BrunoPlaylist playlist) {
                model.setPlaylist(playlist);

                // if there is a route already, call onRouteReady because we were waiting on the playlist
                if (model.getRoute() != null) {
                    onRouteReady(model.getRoute());
                }
            }

            @Override
            public void onFailed(Exception e) {
                // assuming that PlaylistGenerator has already attempted retry
                Log.e(getClass().getSimpleName(), e.getLocalizedMessage());
            }
        });
    }

    private static List<RouteTrackMapping> mapRouteToTracks(final Route route, final BrunoPlaylist playlist) {
        List<RouteTrackMapping> routeTrackMappings;
        try {
            routeTrackMappings = RouteProcessor.execute(route.getRouteSegments(), playlist);
        } catch (RouteProcessor.TrackIndexOutOfBoundsException e) {
            // should never reach here because we assume the playlist will always be long enough
            routeTrackMappings = new ArrayList<>();
        }

        return routeTrackMappings;
    }
}
