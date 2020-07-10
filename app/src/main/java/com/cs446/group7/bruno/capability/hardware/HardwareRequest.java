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

    public Capability getCapability() {
        return capability;
    }

    public NoFailCallback<Void> getCallback() {
        return callback;
    }

    public String getTitle() {
        return TITLE;
    }

    public String getRequestMessage() {
        switch (capability) {
            case LOCATION:
                return "Bruno requires you to enable device location to proceed.";
            case INTERNET:
                return "Bruno requires an active internet connection to proceed. Please wait until your device is connected to an active network before proceeding.";
            default:
                return null;
        }
    }

    public String getRejectionMessage() {
        return "Bruno is still missing " + capability.name().toLowerCase()
                + " capability. You can enable it through system settings.";
    }
}
