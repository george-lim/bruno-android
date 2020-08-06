package com.cs446.group7.bruno.location;

import android.location.Location;

import androidx.annotation.Nullable;

import com.cs446.group7.bruno.utils.NoFailCallback;

public interface LocationService {
    void startLocationUpdates();
    void startLocationUpdates(@Nullable final NoFailCallback<Location> initialLocationCallback);
    void stopLocationUpdates();
    void addSubscriber(final LocationServiceSubscriber subscriber);
    void removeSubscriber(final LocationServiceSubscriber subscriber);
}
