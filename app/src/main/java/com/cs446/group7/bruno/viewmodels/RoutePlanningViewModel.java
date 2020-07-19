package com.cs446.group7.bruno.viewmodels;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.util.Log;

import androidx.annotation.NonNull;

import com.cs446.group7.bruno.BuildConfig;
import com.cs446.group7.bruno.MainActivity;
import com.cs446.group7.bruno.R;
import com.cs446.group7.bruno.capability.Capability;
import com.cs446.group7.bruno.location.LocationServiceSubscriber;
import com.cs446.group7.bruno.models.RouteModel;
import com.cs446.group7.bruno.routing.MockRouteGeneratorImpl;
import com.cs446.group7.bruno.routing.OnRouteResponseCallback;
import com.cs446.group7.bruno.routing.Route;
import com.cs446.group7.bruno.routing.RouteGenerator;
import com.cs446.group7.bruno.routing.RouteGeneratorError;
import com.cs446.group7.bruno.routing.RouteGeneratorImpl;
import com.cs446.group7.bruno.settings.SettingsService;
import com.cs446.group7.bruno.utils.BitmapUtils;
import com.cs446.group7.bruno.utils.Callback;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;

public class RoutePlanningViewModel implements LocationServiceSubscriber, OnRouteResponseCallback {

    // MARK: - Constants

    private static final Capability[] REQUIRED_CAPABILITIES
            = { Capability.LOCATION, Capability.INTERNET };

    // MARK: - Private members

    private Resources resources;
    private RouteModel model;
    private RoutePlanningViewModelDelegate delegate;

    private RouteGenerator routeGenerator;
    private BitmapDescriptor userMarkerIcon;

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
        userMarkerIcon = getUserMarkerIcon();

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

    private BitmapDescriptor getUserMarkerIcon() {
        Drawable avatarDrawable = resources.getDrawable(R.drawable.ic_avatar_1, null);
        return BitmapDescriptorFactory.fromBitmap(BitmapUtils.getBitmapFromVectorDrawable(avatarDrawable));
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

        delegate.setupUI(
                startBtnText,
                model.getMode() == RouteModel.Mode.WALK,
                durationPickerDisplayedValues,
                0,
                RouteModel.DURATIONS_IN_MINUTES.length - 1,
                model.getDurationIndex()
        );
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

            Route route = model.getRoute();

            if (route != null) {
                onRouteReady(route);
            }
            else {
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

        routeGenerator.generateRoute(
                RoutePlanningViewModel.this,
                model.getCurrentLocation(),
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

        generateRoute();
    }

    public void handleRunningModeClick() {
        if (model.getMode() == RouteModel.Mode.RUN) {
            return;
        }

        model.setMode(RouteModel.Mode.RUN);
        delegate.updateSelectedModeBtn(model.getMode() == RouteModel.Mode.WALK);

        generateRoute();
    }

    public void handleDurationSelected(int durationIndex) {
        if (model.getDurationIndex() == durationIndex) {
            return;
        }

        model.setDurationIndex(durationIndex);

        generateRoute();
    }

    // MARK: - LocationServiceSubscriber methods

    @Override
    public void onLocationUpdate(@NonNull Location location) {
        LatLng latlng = new LatLng(location.getLatitude(), location.getLongitude());
        model.setCurrentLocation(latlng);
        delegate.moveUserMarker(latlng, userMarkerIcon);
    }

    // MARK: - OnRouteResponseCallback methods

    @Override
    public void onRouteReady(final Route route) {
        model.setRoute(route);
        delegate.updateStartBtnText(resources.getString(R.string.route_planning_start));
        delegate.drawRoute(route, model.getCurrentLocation(), userMarkerIcon);
    }

    @Override
    public void onRouteError(final RouteGeneratorError error,
                             final Exception underlyingException) {
        delegate.showRouteGenerationError(error.getDescription());
        Log.e(getClass().getSimpleName(), underlyingException.getLocalizedMessage());
    }
}
