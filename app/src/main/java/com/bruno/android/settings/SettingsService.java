package com.bruno.android.settings;

// nothing much here, just some hardcoded values for now
public class SettingsService {
    // metres per minute, based on Google Map's walking speed of 5 km/hr
    public static final double PREFERRED_WALKING_SPEED = 5000d / 60d;
    // metres per minute, based on data for average minutes per mile
    public static final double PREFERRED_RUNNING_SPEED = 1609.34 / 9.8;
}
