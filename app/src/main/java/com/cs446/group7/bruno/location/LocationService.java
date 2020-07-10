package com.cs446.group7.bruno.location;

import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Location;
import android.os.Looper;
import android.util.Log;

import com.cs446.group7.bruno.utils.Callback;
import com.cs446.group7.bruno.utils.NoFailCallback;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.Nullable;

/**
 * Service responsible for handling all logic related to querying the location and receiving location updates.
 * All methods marked with {@code @SuppressLint("MissingPermission")} requires the client to have the location enabled
 * before calling. All other methods are safe to call, regardless whether if location permission is granted.
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
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
    }

    /**
     * Start periodic location updates for all subscribers. The optional {@code callback} argument enables the client
     * to request the initial location before starting the location updates.
     *
     * NOTE: Location permissions must be enabled before calling this method (or the one above). If it is not, the client
     * will not receive location updates and if {@code callback} is given, {@code callback::onFailed} will be invoked
     * with {@link SecurityException}.
     *
     * @param callback callback function to handle the result of the initial location (optional)
     */
    @SuppressLint("MissingPermission")
    public void startLocationUpdates(@Nullable final NoFailCallback<Location> callback) {
        if (callback == null) {
            startLocationUpdates();
            return;
        }

        LocationRequest initialLocationRequest = LocationRequest
                .create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setNumUpdates(1);

        fusedLocationClient.requestLocationUpdates(initialLocationRequest, new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                Log.e("LocationService", locationResult.getLastLocation().toString());
                callback.onSuccess(locationResult.getLastLocation());
                startLocationUpdates();
            }
        }, Looper.getMainLooper());
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
