package com.cs446.group7.bruno.location;

import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Location;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;

public class LocationService {
    private FusedLocationProviderClient fusedLocationClient;
    private List<LocationServiceSubscriber> subscriberList;

    public LocationService(final Context context) {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(context);
        subscriberList = new ArrayList<>();
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
