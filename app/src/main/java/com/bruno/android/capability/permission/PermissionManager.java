package com.bruno.android.capability.permission;

import android.content.Context;
import android.content.pm.PackageManager;

import com.bruno.android.utils.Callback;

// Manages permission status checking and permission requests
public class PermissionManager {
    private PackageManager packageManager;
    private String packageName;
    private PermissionRequestDelegate delegate;

    public PermissionManager(final Context context,
                             final PermissionRequestDelegate delegate) {
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
    public boolean isPermissionGroupGranted(final PermissionGroup permissionGroup) {
        if (permissionGroup != null) {
            for (String permissionName : permissionGroup.getPermissionNames()) {
                if (!isPermissionGranted(permissionName)) {
                    return false;
                }
            }
        }

        return true;
    }

    // Requests permissions if not already granted
    public void requestPermission(final PermissionGroup permissionGroup,
                                  final Callback<Void, Void> callback) {
        if (permissionGroup == null || isPermissionGroupGranted(permissionGroup)) {
            callback.onSuccess(null);
            return;
        }

        delegate.handlePermissionRequest(new PermissionRequest(permissionGroup, callback));
    }
}
