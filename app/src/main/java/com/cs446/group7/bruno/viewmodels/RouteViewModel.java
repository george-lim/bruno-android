package com.cs446.group7.bruno.viewmodels;

import android.app.Application;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.location.Location;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.cs446.group7.bruno.BuildConfig;
import com.cs446.group7.bruno.MainActivity;
import com.cs446.group7.bruno.R;
import com.cs446.group7.bruno.location.LocationServiceSubscriber;
import com.cs446.group7.bruno.routing.MockRouteGeneratorImpl;
import com.cs446.group7.bruno.routing.OnRouteResponseCallback;
import com.cs446.group7.bruno.routing.Route;
import com.cs446.group7.bruno.routing.RouteGenerator;
import com.cs446.group7.bruno.routing.RouteGeneratorError;
import com.cs446.group7.bruno.routing.RouteGeneratorImpl;
import com.cs446.group7.bruno.settings.SettingsService;
import com.cs446.group7.bruno.utils.BitmapUtils;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;

public class RouteViewModel extends AndroidViewModel implements OnRouteResponseCallback, LocationServiceSubscriber {
    private int duration;
    private boolean isWalkingMode = true;
    private MutableLiveData<RouteResult> routeResult = new MutableLiveData<>();
    private MutableLiveData<LatLng> currentLocation = new MutableLiveData<>(null);
    private RouteGenerator routeGenerator;
    private BitmapDescriptor avatarMarker;

    public RouteViewModel(@NonNull Application application) {
        super(application);

        Context context = application.getApplicationContext();
        String apiKey = context.getResources().getString(R.string.google_maps_key);
        routeGenerator = BuildConfig.DEBUG
                ? new MockRouteGeneratorImpl(context, apiKey)
                : new RouteGeneratorImpl(context, apiKey);

        // Store avatar bitmap in view model because conversion of vector drawable to bitmap
        // can be resource heavy
        Drawable avatarDrawable = context.getResources().getDrawable(R.drawable.ic_avatar_1, null);
        avatarMarker = BitmapDescriptorFactory.fromBitmap(BitmapUtils.getBitmapFromVectorDrawable(avatarDrawable));

        MainActivity.getLocationService().addSubscriber(this);
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        MainActivity.getLocationService().stopLocationUpdates();
        MainActivity.getLocationService().removeSubscriber(this);
    }

    public void setDuration(int duration) {
        if (duration == this.duration) {
            return;
        }

        this.duration = duration;
        generateRoute();
    }

    public void setWalkingMode(boolean isWalkingMode) {
        this.isWalkingMode = isWalkingMode;
        generateRoute();
    }

    public LiveData<RouteResult> getRouteResult() {
        return routeResult;
    }

    public LiveData<LatLng> getCurrentLocation() { return currentLocation; }

    public BitmapDescriptor getAvatarMarker() {
        return avatarMarker;
    }

    private void generateRoute() {
        if (currentLocation.getValue() == null) return;

        double speed = isWalkingMode ? SettingsService.PREFERRED_WALKING_SPEED : SettingsService.PREFERRED_RUNNING_SPEED;
        double totalDistance = duration * speed;
        double rotation = Math.random() * 2 * Math.PI;
        routeGenerator.generateRoute(RouteViewModel.this, currentLocation.getValue(), totalDistance, rotation);
    }

    @Override
    public void onRouteReady(Route route) {
        this.routeResult.setValue(new RouteResult(route, null, null));
    }

    @Override
    public void onRouteError(RouteGeneratorError error, Exception underlyingException) {
        this.routeResult.setValue(new RouteResult(null, error, underlyingException));
    }

    @Override
    public void onLocationUpdate(@NonNull Location location) {
        LatLng loc = new LatLng(location.getLatitude(), location.getLongitude());
        currentLocation.setValue(loc);
    }

    private void startLocationUpdates() {
        MainActivity.getLocationService().startLocationUpdates(location -> {
            LatLng loc = new LatLng(location.getLatitude(), location.getLongitude());
            currentLocation.setValue(loc);
            generateRoute();
        });
    }

    public void startRouteGeneration() {
        startLocationUpdates();
    }

    public void startRouteGeneration(int duration) {
        this.duration = duration;
        startRouteGeneration();
    }

    public boolean hasGeneratedRouteOnce() {
        return currentLocation != null && duration > 0;
    }

    public boolean isRoutePlanningComplete() {
        return routeResult.getValue() != null && routeResult.getValue().getRoute() != null;
    }
}
