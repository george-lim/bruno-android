package com.cs446.group7.bruno.capability;

import android.content.Context;

import com.cs446.group7.bruno.utils.CompletionHandler;

public class CapabilityService {
    private PermissionManager permissionManager;

    public CapabilityService(Context context, PermissionRequestDelegate permissionRequestDelegate) {
        permissionManager = new PermissionManager(context, permissionRequestDelegate);
    }

    public boolean isCapabilityEnabled(Capability capability) {
        return permissionManager.isPermissionGroupGranted(capability.getPermissionGroup());
    }

    public void request(Capability capability, CompletionHandler<Void, Void> completion) {
        permissionManager.requestPermission(capability.getPermissionGroup(), completion);
    }
}
