package com.cs446.group7.bruno.capability;

import android.content.Context;

import androidx.annotation.NonNull;

import com.cs446.group7.bruno.utils.Callback;

/*
    CapabilityService is a service that provides capability invariants to clients. When requesting
    for a capability, CapabilityService will:

    1. Check that all associated permissions are granted
    2. Check that all related hardware services are enabled

    before successfully fulfilling the client callback. Furthermore, CapabilityService will attempt
    to request permissions from the user if the permission check fails.
 */
public class CapabilityService {
    private PermissionManager permissionManager;

    public CapabilityService(@NonNull final Context context,
                             @NonNull final PermissionRequestDelegate permissionRequestDelegate) {
        permissionManager = new PermissionManager(context, permissionRequestDelegate);
    }

    // Synchronously check if a capability is enabled, without requesting user action
    public boolean isCapabilityEnabled(@NonNull final Capability capability) {
        return permissionManager.isPermissionGroupGranted(capability.getPermissionGroup());
    }

    // Checks if a capability is enabled, and requests user action if any check fails
    public void request(@NonNull final Capability capability,
                        @NonNull final Callback<Void, Void> callback) {
        permissionManager.requestPermission(capability.getPermissionGroup(), callback);
    }
}
