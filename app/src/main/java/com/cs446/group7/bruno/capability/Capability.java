package com.cs446.group7.bruno.capability;

public enum Capability {
    LOCATION(PermissionGroup.LOCATION),
    INTERNET(null);

    private PermissionGroup permissionGroup;

    Capability(PermissionGroup permissionGroup) {
        this.permissionGroup = permissionGroup;
    }

    public PermissionGroup getPermissionGroup() {
        return permissionGroup;
    }
}
