package com.bruno.android.viewmodels;

import android.content.Context;
import android.content.res.Resources;
import android.location.Location;
import android.util.DisplayMetrics;
import android.util.Log;

import androidx.annotation.NonNull;

import com.bruno.android.BuildConfig;
import com.bruno.android.MainActivity;
import com.bruno.android.R;
import com.bruno.android.capability.Capability;
import com.bruno.android.location.Coordinate;
import com.bruno.android.location.LocationServiceSubscriber;
import com.bruno.android.models.RouteModel;
import com.bruno.android.models.TrackSegment;
import com.bruno.android.music.BrunoPlaylist;
import com.bruno.android.music.playlist.DynamicPlaylistGeneratorImpl;
import com.bruno.android.music.playlist.MockPlaylistGeneratorImpl;
import com.bruno.android.music.playlist.PlaylistGenerator;
import com.bruno.android.routing.DynamicRouteGeneratorImpl;
import com.bruno.android.routing.MockRouteGeneratorImpl;
import com.bruno.android.routing.OnRouteResponseCallback;
import com.bruno.android.routing.RouteGenerator;
import com.bruno.android.routing.RouteGeneratorException;
import com.bruno.android.routing.RouteGeneratorImpl;
import com.bruno.android.routing.RouteSegment;
import com.bruno.android.settings.SettingsService;
import com.bruno.android.storage.PreferencesStorage;
import com.bruno.android.utils.Callback;
import com.bruno.android.utils.NoFailCallback;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import java.util.List;

public class RoutePlanningViewModel implements LocationServiceSubscriber, OnRouteResponseCallback {

    // MARK: - Constants

    private static final Capability[] REQUIRED_CAPABILITIES
            = {Capability.LOCATION, Capability.INTERNET};

    private final String TAG = getClass().getSimpleName();

    // MARK: - Private members

    private final Resources resources;
    private final RouteModel model;
    private final RoutePlanningViewModelDelegate delegate;

    private final RouteGenerator routeGenerator;
    private final PlaylistGenerator playlistGenerator;

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
        return BuildConfig.DEBUG
                ? new DynamicRouteGeneratorImpl(
                new RouteGeneratorImpl(),
                new MockRouteGeneratorImpl()
        )
                : new RouteGeneratorImpl();
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

        playlistGenerator.discoverPlaylist(new Callback<>() {
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
        final LatLng mirrorMaxLat = new LatLng(2 * minLat.latitude - maxLat.latitude - offset, maxLat.longitude);
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

        MainActivity.getCapabilityService().request(REQUIRED_CAPABILITIES, new Callback<>() {
            @Override
            public void onSuccess(Void result) {
                if (model.hasTrackSegments()) {
                    delegate.navigateToNextScreen();
                } else if (!hasStartedLocationUpdates) {
                    startLocationUpdates(nextResult -> {
                        if (!model.hasTrackSegments()) {
                            processTrackSegments();
                        }
                    });
                } else {
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
