package com.cs446.group7.bruno.location;

import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Location;
import android.os.Looper;

import com.cs446.group7.bruno.utils.NoFailCallback;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.Nullable;

/**
 * Singleton responsible for handling all logic related to querying the location
 */
public class LocationService {

    private static final int UPDATE_INTERVAL_MILLISECONDS = 5000;
    private static final int FASTEST_UPDATE_INTERVAL_MILLISECONDS = 2000;

    private FusedLocationProviderClient fusedLocationClient;
    private List<LocationServiceSubscriber> subscriberList;
    private LocationRequest locationRequest;

    private LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            final Location result = locationResult.getLastLocation();
            if (result != null) {
                for (LocationServiceSubscriber subscriber : subscriberList) {
                    subscriber.onLocationUpdate(result);
                }
            }
        }
    };

    public LocationService(final Context context) {
        subscriberList = new ArrayList<>();
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(context);

        locationRequest = LocationRequest.create();

        // Desired duration at which updates are received
        locationRequest.setInterval(UPDATE_INTERVAL_MILLISECONDS);

        // Fastest interval that we can handle requests. eg. It can handle updates as fast as every 2000 ms
        locationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_MILLISECONDS);

        // Prioritize high accuracy results
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    @SuppressLint("MissingPermission")
    public void startLocationUpdates() {
        startLocationUpdates(null);
    }

    /**
     * Allows an optional callback to retrieve the initial location.
     */
    @SuppressLint("MissingPermission")
    public void startLocationUpdates(@Nullable final NoFailCallback<Location> callback) {
        fusedLocationClient
                .getLastLocation()
                .addOnSuccessListener(location -> { // Note: it is possible for 'location' to be null
                    if (callback != null) {
                        callback.onSuccess(location);
                    }
                    fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
                });
    }

    public void stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback);
    }

    public void addSubscriber(final LocationServiceSubscriber subscriber) {
        if (subscriberList.contains(subscriber)) return;
        subscriberList.add(subscriber);
    }

    public void removeSubscriber(final LocationServiceSubscriber subscriber) {
        subscriberList.remove(subscriber);
    }
}
