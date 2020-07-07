package com.cs446.group7.bruno.viewmodels;

import android.app.Application;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.cs446.group7.bruno.MainActivity;
import com.cs446.group7.bruno.R;
import com.cs446.group7.bruno.capability.Capability;
import com.cs446.group7.bruno.routing.OnRouteResponseCallback;
import com.cs446.group7.bruno.routing.Route;
import com.cs446.group7.bruno.routing.RouteGenerator;
import com.cs446.group7.bruno.routing.RouteGeneratorError;
import com.cs446.group7.bruno.routing.RouteGeneratorImpl;
import com.cs446.group7.bruno.settings.SettingsService;
import com.cs446.group7.bruno.utils.Callback;
import com.google.android.gms.maps.model.LatLng;


public class RouteViewModel extends AndroidViewModel implements OnRouteResponseCallback {
    private int duration;
    private boolean isWalkingMode = true;
    private MutableLiveData<RouteResult> routeResult = new MutableLiveData<>();
    private RouteGenerator routeGenerator;

    public RouteViewModel(@NonNull Application application) {
        super(application);

        Context context = getApplication().getApplicationContext();
        String apiKey = context.getResources().getString(R.string.google_maps_key);
        routeGenerator = new RouteGeneratorImpl(context, apiKey);
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
        Callback<Void, Void> permissionCallback = new Callback<Void, Void>() {
            @Override
            public void onSuccess(Void result) {
                // TODO: integrate LocationService to get current location
                LatLng start = new LatLng(43.652746, -79.383555);
                double speed = isWalkingMode ? SettingsService.PREFERRED_WALKING_SPEED : SettingsService.PREFERRED_RUNNING_SPEED;
                double totalDistance = duration * speed;
                double rotation = Math.random() * 2 * Math.PI;
                routeGenerator.generateRoute(RouteViewModel.this, start, totalDistance, rotation);
            }

            @Override
            public void onFailed(Void result) { }
        };

        MainActivity.getCapabilityService().request(Capability.LOCATION, permissionCallback);
    }

    @Override
    public void onRouteReady(Route route) {
        this.routeResult.setValue(new RouteResult(route, null, null));
    }

    @Override
    public void onRouteError(RouteGeneratorError error, Exception underlyingException) {
        this.routeResult.setValue(new RouteResult(null, error, underlyingException));
    }
}
