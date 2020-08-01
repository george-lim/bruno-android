package com.cs446.group7.bruno.capability.hardware;

import android.content.Context;
import android.location.LocationManager;
import android.net.ConnectivityManager;

import com.cs446.group7.bruno.capability.Capability;
import com.cs446.group7.bruno.utils.Callback;
import com.cs446.group7.bruno.utils.NoFailClosureQueue;

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
        return connectivityManager.getActiveNetwork() != null;
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
                                final Callback<Void, Void> clientCallback) {
        if (isHardwareEnabled(capability)) {
            clientCallback.onSuccess(null);
            return;
        }

        NoFailClosureQueue<Void> queue = new NoFailClosureQueue<>();

        // First have delegate show initial hardware request prompt
        queue.add((result, callback)
                -> delegate.showHardwareRequestPrompt(new HardwareRequest(capability, callback)));

        // Then check if hardware is enabled after initial prompt
        queue.add((result, callback) -> {
            // Complete queue early if capability is enabled
            if (isHardwareEnabled(capability)) {
                clientCallback.onSuccess(result);
                return;
            }

            // If hardware is still not enabled, have delegate make hardware request
            delegate.handleHardwareRequest(new HardwareRequest(capability, callback));
        });

        // Then check if hardware is enabled after hardware request was made
        queue.add((result, callback) -> {
            // Complete queue early if capability is enabled
            if (isHardwareEnabled(capability)) {
                clientCallback.onSuccess(result);
                return;
            }

            // If hardware is still not enabled, have delegate handle the request rejection
            delegate.handleHardwareRejection(new HardwareRequest(capability, clientCallback::onFailed));
        });

        queue.run(clientCallback::onSuccess);
    }
}
