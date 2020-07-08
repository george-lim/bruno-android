package com.cs446.group7.bruno.location;

import android.location.Location;

public interface LocationServiceSubscriber {
    void onLocationUpdateSuccess(Location location);
    void onLocationUpdateFailure(LocationServiceException error);
}
