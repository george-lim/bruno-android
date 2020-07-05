package com.cs446.group7.bruno.capability;

import android.Manifest;

// Permission Groups that require user authorization
enum PermissionGroup {
    LOCATION(
            new String[] {
                    Manifest.permission.ACCESS_FINE_LOCATION
            },
            "Bruno requires GPS capability to calculate nearby routes and track your pace during runs."
    );

    private String[] permissionNames;
    private String requestMessage;

    PermissionGroup(String[] permissionNames, String requestMessage) {
        this.permissionNames = permissionNames;
        this.requestMessage = requestMessage;
    }

    String[] getPermissionNames() {
        return permissionNames;
    }

    String getRequestMessage() {
        return requestMessage;
    }
}
