package com.cs446.group7.bruno.location;

import android.location.Location;

import androidx.annotation.NonNull;

public interface LocationServiceSubscriber {
    void onLocationUpdate(@NonNull final Location location);
}
