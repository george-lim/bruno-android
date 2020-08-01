package com.cs446.group7.bruno.capability.permission;

import androidx.annotation.NonNull;

// Delegate permission request logic
public interface PermissionRequestDelegate {
    void handlePermissionRequest(@NonNull final PermissionRequest request);
}
