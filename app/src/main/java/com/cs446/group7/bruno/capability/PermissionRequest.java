package com.cs446.group7.bruno.capability;

import com.cs446.group7.bruno.utils.Callback;

// A model that contains all permission request info
public class PermissionRequest {
    private PermissionGroup permissionGroup;
    private Callback<Void, Void> callback;

    private static String title = "Permission Request";

    PermissionRequest(final PermissionGroup permissionGroup, final Callback<Void, Void> callback) {
        this.permissionGroup = permissionGroup;
        this.callback = callback;
    }

    public String[] getPermissionNames() {
        return permissionGroup.getPermissionNames();
    }

    public Callback<Void, Void> getCallback() {
        return callback;
    }

    public String getTitle() {
        return title;
    }

    public String getPermissionRequestMessage() {
        return permissionGroup.getRequestMessage();
    }

    public String getPermissionDeniedMessage() {
        return "Unfortunately, Bruno requires " + permissionGroup.name().toLowerCase()
                + " permission to proceed. You can enable the permission through system settings.";
    }
}
