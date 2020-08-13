package com.bruno.android.location;

import android.location.Location;

import androidx.annotation.Nullable;

import com.bruno.android.utils.NoFailCallback;

public interface LocationService {
    void startLocationUpdates();
    void startLocationUpdates(@Nullable final NoFailCallback<Location> initialLocationCallback);
    void stopLocationUpdates();
    void addSubscriber(final LocationServiceSubscriber subscriber);
    void removeSubscriber(final LocationServiceSubscriber subscriber);
}
