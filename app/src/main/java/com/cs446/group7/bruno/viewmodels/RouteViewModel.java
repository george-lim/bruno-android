package com.cs446.group7.bruno.viewmodels;

import android.app.Application;
import android.content.Context;
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
import com.google.android.gms.maps.model.LatLng;


public class RouteViewModel extends AndroidViewModel implements OnRouteResponseCallback, LocationServiceSubscriber {
    private int duration;
    private boolean isWalkingMode = true;
    private MutableLiveData<RouteResult> routeResult = new MutableLiveData<>();
    private LatLng currentLocation = null;
    private RouteGenerator routeGenerator;

    public RouteViewModel(@NonNull Application application) {
        super(application);

        Context context = application.getApplicationContext();
        String apiKey = context.getResources().getString(R.string.google_maps_key);
        routeGenerator = new RouteGeneratorImpl(context, apiKey);
//        routeGenerator = BuildConfig.DEBUG
//                ? new MockRouteGeneratorImpl(context, apiKey)
//                : new RouteGeneratorImpl(context, apiKey);
    }

    public void setDuration(int duration) {
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

    private void generateRoute() {
        if (currentLocation == null) return;

        double speed = isWalkingMode ? SettingsService.PREFERRED_WALKING_SPEED : SettingsService.PREFERRED_RUNNING_SPEED;
        double totalDistance = duration * speed;
        double rotation = Math.random() * 2 * Math.PI;
        routeGenerator.generateRoute(RouteViewModel.this, currentLocation, totalDistance, rotation);
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
        if (currentLocation == null) {
            currentLocation = new LatLng(location.getLatitude(), location.getLongitude());
            generateRoute();
        } else {
            currentLocation = new LatLng(location.getLatitude(), location.getLongitude());
        }
    }

    public void initCurrentLocation() {
        MainActivity.getLocationService().startLocationUpdates(location -> {
            currentLocation = new LatLng(location.getLatitude(), location.getLongitude());
            generateRoute();
        });
    }

    public boolean isStartUp() {
        return currentLocation == null;
    }
}
