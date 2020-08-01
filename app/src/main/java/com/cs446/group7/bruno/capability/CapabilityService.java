package com.cs446.group7.bruno.capability;

import android.content.Context;

import androidx.annotation.NonNull;

import com.cs446.group7.bruno.capability.hardware.HardwareManager;
import com.cs446.group7.bruno.capability.hardware.HardwareRequestDelegate;
import com.cs446.group7.bruno.capability.permission.PermissionManager;
import com.cs446.group7.bruno.capability.permission.PermissionRequestDelegate;
import com.cs446.group7.bruno.utils.Callback;
import com.cs446.group7.bruno.utils.ClosureQueue;

/*
    CapabilityService is a service that provides capability invariants to clients. When requesting
    for a capability, CapabilityService will:

    1. Check that all associated permissions are granted
    2. Check that all related hardware capabilities are enabled

    before successfully fulfilling the client callback. Furthermore, CapabilityService will attempt
    to request permissions from the user if the permission check fails.
 */
public class CapabilityService {
    private PermissionManager permissionManager;
    private HardwareManager hardwareManager;

    public CapabilityService(@NonNull final Context context,
                             @NonNull final PermissionRequestDelegate permissionRequestDelegate,
                             @NonNull final HardwareRequestDelegate hardwareRequestDelegate) {
        permissionManager = new PermissionManager(context, permissionRequestDelegate);
        hardwareManager = new HardwareManager(context, hardwareRequestDelegate);
    }

    // Synchronously check if permissions are enabled for a capability
    public boolean isPermissionEnabled(@NonNull final Capability capability) {
        return permissionManager.isPermissionGroupGranted(capability.getPermissionGroup());
    }

    // Synchronously check if hardware dependencies are enabled for a capability
    public boolean isHardwareCapabilityEnabled(@NonNull final Capability capability) {
        return hardwareManager.isHardwareEnabled(capability);
    }

    // Synchronously check if a capability is enabled, without requesting user action
    public boolean isCapabilityEnabled(@NonNull final Capability capability) {
        return isPermissionEnabled(capability) && isHardwareCapabilityEnabled(capability);
    }

    // Synchronously check if all capabilities are enabled at once, without requesting user action
    public boolean isEveryCapabilityEnabled(@NonNull final Capability[] capabilities) {
        for (Capability capability : capabilities) {
            if (!isCapabilityEnabled(capability)) {
                return false;
            }
        }

        return true;
    }

    // Checks if a capability is enabled, and requests user action if any check fails
    public void request(@NonNull final Capability capability,
                        @NonNull final Callback<Void, Void> callback) {
        permissionManager.requestPermission(capability.getPermissionGroup(), new Callback<Void, Void>() {
            @Override
            public void onSuccess(Void result) {
                hardwareManager.requestHardware(capability, callback);
            }

            @Override
            public void onFailed(Void result) {
                callback.onFailed(null);
            }
        });
    }

    // Sequentially requests multiple capabilities
    public void request(@NonNull final Capability[] capabilities,
                        @NonNull final Callback<Void, Void> clientCallback) {
        ClosureQueue<Void, Void> queue = new ClosureQueue<>();

        for (Capability capability : capabilities) {
            queue.add((result, callback) -> request(capability, callback));
        }

        queue.run(clientCallback);
    }
}
