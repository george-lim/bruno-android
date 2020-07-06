package com.cs446.group7.bruno.location;

import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Location;
import android.os.Looper;
import android.util.Log;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import java.util.ArrayList;
import java.util.List;

/**
 * Singleton responsible for handling all logic related to querying the location
 */
public class LocationService {

    private static final int UPDATE_INTERVAL_MILLISECONDS = 5000;
    private static final int FASTEST_UPDATE_INTERVAL_MILLISECONDS = 2000;

    private FusedLocationProviderClient fusedLocationClient;
    private List<LocationServiceSubscriber> subscriberList;
    private LocationRequest locationRequest;
    private static String TAG;

    private static LocationService instance;

    private LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            final Location result = locationResult.getLastLocation();
            if (result == null) {
                for (LocationServiceSubscriber subscriber : subscriberList) {
                    subscriber.onLocationUpdateFailure(
                            new LocationServiceException(LocationServiceException.ErrorType.NULL_LOCATION_ERROR));
                }
            } else {
                for (LocationServiceSubscriber subscriber : subscriberList) {
                    subscriber.onLocationUpdateSuccess(result);
                }
            }
        }
    };

    public static void init(Context context) {
        if (instance != null) {
            Log.w(TAG, "init called again, singleton re-initialized");
        }
        instance = new LocationService(context);
    }

    public static LocationService getInstance() {
        if (instance == null) {
            throw new AssertionError("you must call 'init' first!");
        }
        return instance;
    }

    private LocationService(final Context context) {
        subscriberList = new ArrayList<>();
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(context);

        locationRequest = LocationRequest.create();

        // Desired duration at which updates are received
        locationRequest.setInterval(UPDATE_INTERVAL_MILLISECONDS);

        // Fastest interval that we can handle requests. eg. It can handle updates as fast as every 2000 ms
        locationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_MILLISECONDS);

        // Prioritize high accuracy results
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        TAG = getClass().getSimpleName();
    }

    @SuppressLint("MissingPermission")
    public void startLocationUpdates() {
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
    }

    public void stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback);
    }

    public void addSubscriber(final LocationServiceSubscriber subscriber) {
        subscriberList.add(subscriber);
    }

    public void removeSubscriber(final LocationServiceSubscriber subscriber) {
        subscriberList.remove(subscriber);
    }

    /**
     * Gets the current location of device.
     *
     * Note: Location permissions must be enabled for this to work
     *
     * @param callback callback interface
     */
    @SuppressLint("MissingPermission")
    public void getCurrentLocation(LocationServiceSubscriber callback) {
        fusedLocationClient
                .getLastLocation()
                .addOnSuccessListener(location -> {
                    /*
                        Android's own handler can return null even on a successful call,
                        we will treat them as errors.
                     */
                    if (location == null) {
                        callback.onLocationUpdateFailure(new LocationServiceException(
                                LocationServiceException.ErrorType.NULL_LOCATION_ERROR));
                    } else {
                        callback.onLocationUpdateSuccess(location);
                    }
                })
                .addOnFailureListener(error -> {
                    callback.onLocationUpdateFailure(new LocationServiceException(error));
                });
    }
}
