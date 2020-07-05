package com.cs446.group7.bruno.capability;

import android.content.Context;
import android.content.pm.PackageManager;

import com.cs446.group7.bruno.utils.Callback;

// Manages permission status checking and permission requests
class PermissionManager {
    private PackageManager packageManager;
    private String packageName;
    private PermissionRequestDelegate delegate;

    PermissionManager(final Context context, final PermissionRequestDelegate delegate) {
        packageManager = context.getPackageManager();
        packageName = context.getPackageName();
        this.delegate = delegate;
    }

    // Predicate that determines if a permission is granted given permission name
    private boolean isPermissionGranted(final String permissionName) {
        int permissionStatus = packageManager.checkPermission(permissionName, packageName);
        return permissionStatus == PackageManager.PERMISSION_GRANTED;
    }

    // Predicate that determines if a permission group is granted
    boolean isPermissionGroupGranted(final PermissionGroup permissionGroup) {
        for (String permissionName : permissionGroup.getPermissionNames()) {
            if (!isPermissionGranted(permissionName)) {
                return false;
            }
        }

        return true;
    }

    // Requests permissions if not already granted
    void requestPermission(final PermissionGroup permissionGroup,
                           final Callback<Void, Void> callback) {
        if (isPermissionGroupGranted(permissionGroup)) {
            callback.onSuccess(null);
            return;
        }

        delegate.handlePermissionRequest(new PermissionRequest(permissionGroup, callback));
    }
}
