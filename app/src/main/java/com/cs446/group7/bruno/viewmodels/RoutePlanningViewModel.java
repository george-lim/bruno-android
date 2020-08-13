package com.cs446.group7.bruno.viewmodels;

import android.content.Context;
import android.content.res.Resources;
import android.location.Location;
import android.util.DisplayMetrics;
import android.util.Log;

import androidx.annotation.NonNull;

import com.cs446.group7.bruno.BuildConfig;
import com.cs446.group7.bruno.MainActivity;
import com.cs446.group7.bruno.R;
import com.cs446.group7.bruno.capability.Capability;
import com.cs446.group7.bruno.location.Coordinate;
import com.cs446.group7.bruno.location.LocationServiceSubscriber;
import com.cs446.group7.bruno.models.RouteModel;
import com.cs446.group7.bruno.models.TrackSegment;
import com.cs446.group7.bruno.music.BrunoPlaylist;
import com.cs446.group7.bruno.music.playlist.DynamicPlaylistGeneratorImpl;
import com.cs446.group7.bruno.music.playlist.MockPlaylistGeneratorImpl;
import com.cs446.group7.bruno.music.playlist.PlaylistGenerator;
import com.cs446.group7.bruno.routing.DynamicRouteGeneratorImpl;
import com.cs446.group7.bruno.routing.MockRouteGeneratorImpl;
import com.cs446.group7.bruno.routing.OnRouteResponseCallback;
import com.cs446.group7.bruno.routing.RouteGenerator;
import com.cs446.group7.bruno.routing.RouteGeneratorException;
import com.cs446.group7.bruno.routing.RouteGeneratorImpl;
import com.cs446.group7.bruno.routing.RouteSegment;
import com.cs446.group7.bruno.settings.SettingsService;
import com.cs446.group7.bruno.storage.PreferencesStorage;
import com.cs446.group7.bruno.utils.Callback;
import com.cs446.group7.bruno.utils.NoFailCallback;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import java.util.List;

public class RoutePlanningViewModel implements LocationServiceSubscriber, OnRouteResponseCallback {

    // MARK: - Constants

    private static final Capability[] REQUIRED_CAPABILITIES
            = { Capability.LOCATION, Capability.INTERNET };

    private final String TAG = getClass().getSimpleName();

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

        this.model.setRouteColours(resources.getIntArray(R.array.colorRouteList));

        routeGenerator = getRouteGenerator();
        playlistGenerator = getPlaylistGenerator();

        MainActivity.getLocationService().addSubscriber(this);

        setupUI();

        startLocationUpdates(result -> {
            if (!model.hasTrackSegments()) {
                processTrackSegments();
            }
        });
    }

    public void onDestroyView() {
        MainActivity.getLocationService().stopLocationUpdates();
        MainActivity.getLocationService().removeSubscriber(this);
    }

    // MARK: - Private methods

    private RouteGenerator getRouteGenerator() {
        String googleMapsKey = resources.getString(R.string.google_maps_key);
        return BuildConfig.DEBUG
                ? new DynamicRouteGeneratorImpl(
                        new RouteGeneratorImpl(googleMapsKey),
                        new MockRouteGeneratorImpl()
                )
                : new RouteGeneratorImpl(googleMapsKey);
    }

    private PlaylistGenerator getPlaylistGenerator() {
        return BuildConfig.DEBUG
                ? new DynamicPlaylistGeneratorImpl(
                        MainActivity.getSpotifyService().getPlaylistService(),
                        new MockPlaylistGeneratorImpl()
                )
                : MainActivity.getSpotifyService().getPlaylistService();
    }

    private void setupUI() {
        boolean isEveryCapabilityEnabled = MainActivity
                .getCapabilityService()
                .isEveryCapabilityEnabled(REQUIRED_CAPABILITIES);

        String startBtnText = model.hasTrackSegments() || isEveryCapabilityEnabled
                ? resources.getString(R.string.route_planning_start)
                : resources.getString(R.string.route_planning_create_route);

        String[] durationPickerDisplayedValues = new String[RouteModel.DURATIONS_IN_MINUTES.length];

        for (int i = 0; i < RouteModel.DURATIONS_IN_MINUTES.length; ++i) {
            durationPickerDisplayedValues[i] = Integer.toString(RouteModel.DURATIONS_IN_MINUTES[i]);
        }

        int userAvatarDrawableResourceId = MainActivity.getPreferencesStorage()
                .getInt(PreferencesStorage.KEYS.USER_AVATAR, PreferencesStorage.DEFAULT_AVATAR);

        delegate.setupUI(
                startBtnText,
                model.getMode() == RouteModel.Mode.WALK,
                durationPickerDisplayedValues,
                0,
                RouteModel.DURATIONS_IN_MINUTES.length - 1,
                model.getDurationIndex(),
                userAvatarDrawableResourceId
        );

        if (model.hasTrackSegments()) {
            onProcessTrackSegmentsSuccess();
        }

        final Coordinate coordinate = model.getCurrentCoordinate();
        if (coordinate != null) {
            delegate.moveUserMarker(coordinate.getLatLng());
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
            if (model.hasTrackSegments()) {
                onProcessTrackSegmentsSuccess();
            }

            return;
        }

        playlistGenerator.discoverPlaylist(new Callback<BrunoPlaylist, Exception>() {
            @Override
            public void onSuccess(BrunoPlaylist playlist) {
                model.setPlaylist(playlist);

                if (model.hasTrackSegments()) {
                    onProcessTrackSegmentsSuccess();
                }
            }

            @Override
            public void onFailed(Exception e) {
                model.setPlaylist(null);
                onProcessTrackSegmentsFailure();
                delegate.showRouteProcessingError(resources.getString(R.string.route_planning_playlist_error));

                if (e.getLocalizedMessage() != null) {
                    Log.e(TAG, e.getLocalizedMessage());
                }
            }
        });
    }

    private void generateRoute() {
        if (model.getCurrentCoordinate() == null) {
            onProcessTrackSegmentsFailure();
            return;
        }

        double speed = model.getMode() == RouteModel.Mode.WALK
                ? SettingsService.PREFERRED_WALKING_SPEED
                : SettingsService.PREFERRED_RUNNING_SPEED;
        double totalDistance = model.getDurationInMinutes() * speed;
        double rotation = Math.random() * 2 * Math.PI;

        routeGenerator.generateRoute(
                RoutePlanningViewModel.this,
                model.getCurrentCoordinate(),
                totalDistance,
                rotation
        );
    }

    private void processTrackSegments() {
        delegate.updateStartBtnEnabled(false);

        generatePlaylist();
        generateRoute();
    }

    private void drawRoute() {
        float routeWidth = 14;
        delegate.drawRoute(model.getTrackSegments(), routeWidth);
    }

    private void animateCamera() {
        LatLngBounds.Builder boundsBuilder = new LatLngBounds.Builder();

        for (TrackSegment trackSegment : model.getTrackSegments()) {
            for (LatLng latLng : trackSegment.getLatLngs()) {
                boundsBuilder.include(latLng);
            }
        }

        DisplayMetrics displayMetrics = resources.getDisplayMetrics();
        final float cardViewHeightDp = delegate.getCardViewHeight() / displayMetrics.density;
        final float mapFragmentHeightDp = delegate.getMapViewHeight() / displayMetrics.density;

        // from tests it seems like we need to add some height to cardView to get a good blockedScreenFraction
        final double blockedScreenFraction = (cardViewHeightDp + 40) / mapFragmentHeightDp;

        LatLngBounds bounds = boundsBuilder.build();
        final LatLng minLat = bounds.southwest, maxLat = bounds.northeast;

        // compute offset
        final double H = maxLat.latitude - minLat.latitude;
        final double T = H / (1 - blockedScreenFraction);
        final double offset = T - 2 * H;

        // find mirror point of maxLat and include in bounds
        final LatLng mirrorMaxLat= new LatLng(2 * minLat.latitude - maxLat.latitude - offset, maxLat.longitude);
        boundsBuilder.include(mirrorMaxLat);

        bounds = boundsBuilder.build();
        final int padding = 200;
        delegate.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, padding));
    }

    private void onProcessTrackSegmentsSuccess() {
        delegate.updateStartBtnText(resources.getString(R.string.route_planning_start));
        delegate.clearMap();
        drawRoute();
        animateCamera();
        delegate.moveUserMarker(model.getCurrentCoordinate().getLatLng());
        delegate.updateStartBtnEnabled(true);
    }

    private void onProcessTrackSegmentsFailure() {
        delegate.updateStartBtnText(resources.getString(R.string.route_planning_create_route));
        delegate.clearMap();
        if (model.getCurrentCoordinate() != null) {
            delegate.moveUserMarker(model.getCurrentCoordinate().getLatLng());
        }
        delegate.updateStartBtnEnabled(true);
    }

    // MARK: - User action handlers

    public void handleStartWalkingClick() {
        if (isRequestingCapabilities) return;
        isRequestingCapabilities = true;

        MainActivity.getCapabilityService().request(REQUIRED_CAPABILITIES, new Callback<Void, Void>() {
            @Override
            public void onSuccess(Void result) {
                if (model.hasTrackSegments()) {
                    delegate.navigateToNextScreen();
                }
                else if (!hasStartedLocationUpdates) {
                    startLocationUpdates(nextResult -> {
                        if (!model.hasTrackSegments()) {
                            processTrackSegments();
                        }
                    });
                }
                else {
                    processTrackSegments();
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

        processTrackSegments();
    }

    public void handleRunningModeClick() {
        if (model.getMode() == RouteModel.Mode.RUN) {
            return;
        }

        model.setMode(RouteModel.Mode.RUN);
        delegate.updateSelectedModeBtn(model.getMode() == RouteModel.Mode.WALK);

        processTrackSegments();
    }

    public void handleDurationSelected(int durationIndex) {
        if (model.getDurationIndex() == durationIndex) {
            return;
        }

        model.setDurationIndex(durationIndex);

        processTrackSegments();
    }

    // MARK: - LocationServiceSubscriber methods

    @Override
    public void onLocationUpdate(@NonNull Location location) {
        model.setCurrentLocation(location);
        delegate.moveUserMarker(model.getCurrentCoordinate().getLatLng());
    }

    // MARK: - OnRouteResponseCallback methods

    @Override
    public void onRouteReady(final List<RouteSegment> routeSegments) {
        model.setRouteSegments(routeSegments);

        if (model.hasTrackSegments()) {
            onProcessTrackSegmentsSuccess();
        }
    }

    @Override
    public void onRouteError(final RouteGeneratorException exception) {
        model.setRouteSegments(null);
        onProcessTrackSegmentsFailure();
        delegate.showRouteProcessingError(exception.getMessage());

        if (exception.getCause() != null && exception.getCause().getLocalizedMessage() != null) {
            Log.e(TAG, exception.getCause().getLocalizedMessage());
        }
    }
}
