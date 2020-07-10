package com.cs446.group7.bruno.capability.hardware;

import androidx.annotation.NonNull;

// Delegate hardware request logic
public interface HardwareRequestDelegate {
    void showHardwareRequestPrompt(@NonNull final HardwareRequest request);
    void handleHardwareRequest(@NonNull final HardwareRequest request);
    void handleHardwareRejection(@NonNull final HardwareRequest request);
}
