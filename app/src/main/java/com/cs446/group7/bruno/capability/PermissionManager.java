package com.cs446.group7.bruno.capability;

import android.content.Context;
import android.content.pm.PackageManager;

import com.cs446.group7.bruno.utils.CompletionHandler;

class PermissionManager {
    private PackageManager packageManager;
    private String packageName;
    private PermissionRequestDelegate delegate;

    PermissionManager(Context context, PermissionRequestDelegate delegate) {
        packageManager = context.getPackageManager();
        packageName = context.getPackageName();
        this.delegate = delegate;
    }

    private boolean isPermissionGranted(String permissionName) {
        int permissionStatus = packageManager.checkPermission(permissionName, packageName);
        return permissionStatus == PackageManager.PERMISSION_GRANTED;
    }

    boolean isPermissionGroupGranted(PermissionGroup permissionGroup) {
        for (String permissionName : permissionGroup.getPermissionNames()) {
            if (!isPermissionGranted(permissionName)) {
                return false;
            }
        }

        return true;
    }

    void requestPermission(PermissionGroup permissionGroup, CompletionHandler<Void, Void> completion) {
        if (isPermissionGroupGranted(permissionGroup)) {
            completion.onSuccess(null);
            return;
        }

        delegate.handlePermissionRequest(new PermissionRequest(permissionGroup, completion));
    }
}
