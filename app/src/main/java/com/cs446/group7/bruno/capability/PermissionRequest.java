package com.cs446.group7.bruno.capability;

import com.cs446.group7.bruno.utils.CompletionHandler;

public class PermissionRequest {
    private PermissionGroup permissionGroup;
    private CompletionHandler<Void, Void> completion;

    private static String title = "Permission Request";

    PermissionRequest(PermissionGroup permissionGroup, CompletionHandler<Void, Void> completion) {
        this.permissionGroup = permissionGroup;
        this.completion = completion;
    }

    public String[] getPermissionNames() {
        return permissionGroup.getPermissionNames();
    }

    public CompletionHandler<Void, Void> getCompletion() {
        return completion;
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
