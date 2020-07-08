package com.cs446.group7.bruno.capability.hardware;

import androidx.annotation.NonNull;

// Delegate hardware request logic
public interface HardwareRequestDelegate {
    void handleHardwareRequest(@NonNull final HardwareRequest request);
}
