package com.cs446.group7.bruno.location;

import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Location;
import android.os.Looper;

import androidx.annotation.Nullable;

import com.cs446.group7.bruno.utils.NoFailCallback;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import java.util.ArrayList;
import java.util.List;

/**
 * Service responsible for handling all logic related to querying the location and receiving location updates.
 * All methods marked with {@code @SuppressLint("MissingPermission")} requires the client to have the location enabled
 * before calling. All other methods are safe to call, regardless if location permission is granted.
 */
public class LocationServiceImpl implements LocationService {

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

    public LocationServiceImpl(final Context context) {
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

    /**
     * Start the periodic location updates for all subscribers.
     *
     * NOTE: Location permissions must be enabled before calling this method. If it is not, subscribers
     * will not receive location updates.
     */
    @SuppressLint("MissingPermission")
    @Override
    public void startLocationUpdates() {
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
    }

    /**
     * Start periodic location updates for all subscribers. The optional {@code initialLocationCallback} argument enables the client
     * to receive the initial location before starting the regular, periodic updates.
     *
     * NOTE: Location permissions must be enabled before calling this method. If it is not, the client
     * will not receive location updates and {@code initialLocationCallback} will not be invoked.
     *
     * @param initialLocationCallback initialLocationCallback function to handle the result of the initial location (optional)
     */
    @SuppressLint("MissingPermission")
    @Override
    public void startLocationUpdates(@Nullable final NoFailCallback<Location> initialLocationCallback) {
        if (initialLocationCallback == null) {
            startLocationUpdates();
            return;
        }

        // request for initial location before starting it for the subscribers
        LocationRequest initialLocationRequest = LocationRequest
                .create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setNumUpdates(1); // Receive one update as the initial location, then stop

        // Request initial location push, this will only be triggered once
        fusedLocationClient.requestLocationUpdates(initialLocationRequest, new LocationCallback() {

            // Initial location received, invoke the callback and start the periodic updates for subscribers
            @Override
            public void onLocationResult(LocationResult locationResult) {
                initialLocationCallback.onSuccess(locationResult.getLastLocation());
                startLocationUpdates();
            }
        }, Looper.getMainLooper());
    }

    @Override
    public void stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback);
    }

    @Override
    public void addSubscriber(final LocationServiceSubscriber subscriber) {
        if (subscriberList.contains(subscriber)) return;
        subscriberList.add(subscriber);
    }

    @Override
    public void removeSubscriber(final LocationServiceSubscriber subscriber) {
        subscriberList.remove(subscriber);
    }
}
