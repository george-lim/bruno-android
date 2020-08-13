package com.bruno.android.capability.permission;

import android.Manifest;

import androidx.annotation.NonNull;

// An enumerable model representing a group of related permissions
public enum PermissionGroup {
    LOCATION(
            new String[] {
                    Manifest.permission.ACCESS_FINE_LOCATION
            },
            "Bruno requires GPS capability to calculate nearby routes and track your pace during runs."
    );

    private String[] permissionNames;
    private String requestMessage;

    PermissionGroup(@NonNull final String[] permissionNames,
                    @NonNull final String requestMessage) {
        this.permissionNames = permissionNames;
        this.requestMessage = requestMessage;
    }

    public String[] getPermissionNames() {
        return permissionNames;
    }

    public String getRequestMessage() {
        return requestMessage;
    }
}
