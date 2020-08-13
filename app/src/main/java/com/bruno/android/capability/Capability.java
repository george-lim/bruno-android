package com.bruno.android.capability;

import com.bruno.android.capability.permission.PermissionGroup;

// An enumerable model representing a capability
public enum Capability {
    LOCATION(PermissionGroup.LOCATION),
    INTERNET(null);

    private PermissionGroup permissionGroup;

    Capability(final PermissionGroup permissionGroup) {
        this.permissionGroup = permissionGroup;
    }

    public PermissionGroup getPermissionGroup() {
        return permissionGroup;
    }
}
