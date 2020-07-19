package com.cs446.group7.bruno.viewmodels;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.location.Location;

import androidx.annotation.NonNull;

import com.cs446.group7.bruno.BuildConfig;
import com.cs446.group7.bruno.MainActivity;
import com.cs446.group7.bruno.R;
import com.cs446.group7.bruno.capability.Capability;
import com.cs446.group7.bruno.location.LocationServiceSubscriber;
import com.cs446.group7.bruno.models.RouteModel;
import com.cs446.group7.bruno.routing.MockRouteGeneratorImpl;
import com.cs446.group7.bruno.routing.RouteGenerator;
import com.cs446.group7.bruno.routing.RouteGeneratorImpl;
import com.cs446.group7.bruno.utils.BitmapUtils;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;

public class RoutePlanningViewModel implements LocationServiceSubscriber {

    // MARK: - Constants

    private static final Capability[] REQUIRED_CAPABILITIES
            = { Capability.LOCATION, Capability.INTERNET };

    // MARK: - Private members

    private Context context;
    private RouteModel model;
    private RoutePlanningViewModelDelegate delegate;

    private RouteGenerator routeGenerator;
    private BitmapDescriptor avatarMarker;

    // MARK: - Lifecycle methods

    public RoutePlanningViewModel(final Context context,
                                  final RouteModel model,
                                  final RoutePlanningViewModelDelegate delegate) {
        this.context = context;
        this.model = model;
        this.delegate = delegate;

        String googleMapsKey = context.getResources().getString(R.string.google_maps_key);
        routeGenerator = BuildConfig.DEBUG
                ? new MockRouteGeneratorImpl(context, googleMapsKey)
                : new RouteGeneratorImpl(context, googleMapsKey);

        Drawable avatarDrawable = context.getResources().getDrawable(R.drawable.ic_avatar_1, null);
        avatarMarker = BitmapDescriptorFactory.fromBitmap(BitmapUtils.getBitmapFromVectorDrawable(avatarDrawable));

        MainActivity.getLocationService().addSubscriber(this);

        setupUI();
    }

    private void setupUI() {
        boolean isEveryCapabilityEnabled = MainActivity
                .getCapabilityService()
                .isEveryCapabilityEnabled(REQUIRED_CAPABILITIES);

        String startBtnText = model.getRoute() != null || isEveryCapabilityEnabled
                ? context.getResources().getString(R.string.route_planning_start)
                : context.getResources().getString(R.string.route_planning_generate_route);

        String[] durationPickerDisplayedValues = new String[RouteModel.DURATIONS_IN_MINUTES.length];

        for (int i = 0; i < RouteModel.DURATIONS_IN_MINUTES.length; ++i) {
            durationPickerDisplayedValues[i] = Integer.toString(RouteModel.DURATIONS_IN_MINUTES[i]);
        }

        delegate.setupUI(
                startBtnText,
                true,
                durationPickerDisplayedValues,
                0,
                RouteModel.DURATIONS_IN_MINUTES.length - 1,
                model.getDurationIndex()
        );
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        MainActivity.getLocationService().stopLocationUpdates();
        MainActivity.getLocationService().removeSubscriber(this);
    }

    // MARK: - User action handlers

    public void handleStartWalkingClick() {}

    public void handleWalkingModeClick() {
        if (model.getMode() == RouteModel.Mode.WALK) {
            return;
        }

        model.setMode(RouteModel.Mode.WALK);
        delegate.updateSelectedModeBtn(model.getMode() == RouteModel.Mode.WALK);
    }

    public void handleRunningModeClick() {
        if (model.getMode() == RouteModel.Mode.RUN) {
            return;
        }

        model.setMode(RouteModel.Mode.RUN);
        delegate.updateSelectedModeBtn(model.getMode() == RouteModel.Mode.WALK);
    }

    public void handleDurationSelected(int scrollState) {}

    // MARK: - LocationServiceSubscriber methods

    @Override
    public void onLocationUpdate(@NonNull Location location) {

    }
}
