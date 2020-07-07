package com.cs446.group7.bruno.capability.hardware;

import com.cs446.group7.bruno.capability.Capability;
import com.cs446.group7.bruno.utils.NoFailCallback;

// A model that contains all hardware request info
public class HardwareRequest {
    private Capability capability;
    private NoFailCallback<Void> callback;

    // All hardware requests share the same title internally
    private static final String TITLE = "Hardware Request";

    public HardwareRequest(final Capability capability,
                           final NoFailCallback<Void> callback) {
        this.capability = capability;
        this.callback = callback;
    }

    public NoFailCallback<Void> getCallback() {
        return callback;
    }

    public String getTitle() {
        return TITLE;
    }

    public String getMessage() {
        return "Bruno requires you to enable ... to proceed. Please enable it, then tap \"OK\".";
    }
}
