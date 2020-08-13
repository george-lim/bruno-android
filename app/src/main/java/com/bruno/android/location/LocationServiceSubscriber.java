package com.bruno.android.location;

import android.location.Location;

import androidx.annotation.NonNull;

public interface LocationServiceSubscriber {
    void onLocationUpdate(@NonNull final Location location);
}
