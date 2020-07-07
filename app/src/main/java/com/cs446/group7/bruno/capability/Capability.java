package com.cs446.group7.bruno.capability;

import com.cs446.group7.bruno.capability.permission.PermissionGroup;

// An enumerable model representing a capability
public enum Capability {
    LOCATION(PermissionGroup.LOCATION);

    private PermissionGroup permissionGroup;

    Capability(final PermissionGroup permissionGroup) {
        this.permissionGroup = permissionGroup;
    }

    public PermissionGroup getPermissionGroup() {
        return permissionGroup;
    }
}
