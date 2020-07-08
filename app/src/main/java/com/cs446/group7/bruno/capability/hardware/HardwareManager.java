package com.cs446.group7.bruno.capability.hardware;

import android.content.Context;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.cs446.group7.bruno.capability.Capability;
import com.cs446.group7.bruno.utils.Callback;
import com.cs446.group7.bruno.utils.NoFailCallback;

// Manages hardware capability checking and requests
public class HardwareManager {
    private LocationManager locationManager;
    private ConnectivityManager connectivityManager;
    private HardwareRequestDelegate delegate;

    public HardwareManager(final Context context,
                           final HardwareRequestDelegate delegate) {
        locationManager = (LocationManager)context.getSystemService(context.LOCATION_SERVICE);
        connectivityManager = (ConnectivityManager)context.getSystemService(context.CONNECTIVITY_SERVICE);
        this.delegate = delegate;
    }

    // Predicate that determines whether the user has location enabled
    private boolean isLocationEnabled() {
        boolean isGPSProviderEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        boolean isNetworkProviderEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        return isGPSProviderEnabled || isNetworkProviderEnabled;
    }

    /*
      Predicate that determines whether the user is connected to a network
      NOTE: This check only passes if the user is truly connected to a network.
            Toggling WiFi or cellular data is not enough.
     */
    private boolean isConnectedToNetwork() {
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    // Predicate that determines if a hardware capability is enabled
    public boolean isHardwareEnabled(final Capability capability) {
        switch (capability) {
            case LOCATION:
                return isLocationEnabled();
            case INTERNET:
                return isConnectedToNetwork();
            default:
                return false;
        }
    }

    // Requests hardware capability if not already enabled
    public void requestHardware(final Capability capability,
                                final Callback<Void, Void> callback) {
        if (isHardwareEnabled(capability)) {
            callback.onSuccess(null);
            return;
        }

        // Verify that the user has enabled hardware
        NoFailCallback<Void> verifyCallback = result -> {
            if (isHardwareEnabled(capability)) {
                callback.onSuccess(null);
                return;
            }

            callback.onFailed(null);
        };

        delegate.handleHardwareRequest(new HardwareRequest(capability, verifyCallback));
    }
}
