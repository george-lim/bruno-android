package com.cs446.group7.bruno.capability;

// Delegate permission request logic
public interface PermissionRequestDelegate {
    void handlePermissionRequest(final PermissionRequest request);
}
