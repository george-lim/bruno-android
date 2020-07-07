package com.cs446.group7.bruno.capability.hardware;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.cs446.group7.bruno.capability.Capability;
import com.cs446.group7.bruno.utils.Callback;
import com.cs446.group7.bruno.utils.NoFailCallback;

public class HardwareManager {
    private ConnectivityManager connectivityManager;
    private HardwareRequestDelegate delegate;

    public HardwareManager(final Context context,
                           final HardwareRequestDelegate delegate) {
        connectivityManager = (ConnectivityManager)context.getSystemService(context.CONNECTIVITY_SERVICE);
        this.delegate = delegate;
    }

    private boolean isConnectedToNetwork() {
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public boolean isHardwareEnabled(final Capability capability) {
        switch (capability) {
            case LOCATION:
                return isConnectedToNetwork();
        }

        return false;
    }

    public void requestHardware(final Capability capability,
                                final Callback<Void, Void> callback) {
        if (isHardwareEnabled(capability)) {
            callback.onSuccess(null);
            return;
        }

        // See if the user enabled hardware
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
