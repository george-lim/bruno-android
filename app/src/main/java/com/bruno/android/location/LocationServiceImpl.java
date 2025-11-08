package com.bruno.android.location;

import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Location;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bruno.android.utils.NoFailCallback;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;

import java.util.ArrayList;
import java.util.List;

/**
 * Service responsible for handling all logic related to querying the location and receiving location updates.
 * All methods marked with {@code @SuppressLint("MissingPermission")} requires the client to have the location enabled
 * before calling. All other methods are safe to call, regardless if location permission is granted.
 */
public class LocationServiceImpl implements LocationService {

    private static final int UPDATE_INTERVAL_MILLISECONDS = 5000;
    private static final int FASTEST_UPDATE_INTERVAL_MILLISECONDS = 1000;

    private final FusedLocationProviderClient fusedLocationClient;
    private final List<LocationServiceSubscriber> subscriberList;
    private final LocationRequest locationRequest;

    private final LocationCallback locationCallback = new LocationCallback() {
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
        locationRequest = new LocationRequest.Builder(UPDATE_INTERVAL_MILLISECONDS)
                .setPriority(Priority.PRIORITY_HIGH_ACCURACY)
                .setMinUpdateIntervalMillis(FASTEST_UPDATE_INTERVAL_MILLISECONDS)
                .build();
    }

    /**
     * Start the periodic location updates for all subscribers.
     * <p>
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
     * <p>
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

        fusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null).addOnSuccessListener(location -> {
            initialLocationCallback.onSuccess(location);
            startLocationUpdates();
        });
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
