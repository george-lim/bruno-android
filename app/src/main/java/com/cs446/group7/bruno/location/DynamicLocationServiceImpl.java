package com.cs446.group7.bruno.location;

import android.location.Location;

import androidx.annotation.Nullable;

import com.cs446.group7.bruno.MainActivity;
import com.cs446.group7.bruno.storage.PreferencesStorage;
import com.cs446.group7.bruno.utils.NoFailCallback;

public class DynamicLocationServiceImpl implements LocationService {
    private LocationService locationService;
    private LocationService mockLocationService;

    public DynamicLocationServiceImpl(final LocationService locationService,
                                      final LocationService mockLocationService) {
        this.locationService = locationService;
        this.mockLocationService = mockLocationService;
    }

    private LocationService getLocationService() {
        boolean isUsingMock = MainActivity.getPreferencesStorage().getBoolean(
                PreferencesStorage.KEYS.MOCK_LOCATION_SERVICE,
                true
        );

        return isUsingMock ? mockLocationService : locationService;
    }

    @Override
    public void startLocationUpdates() {
        getLocationService().startLocationUpdates();
    }

    @Override
    public void startLocationUpdates(@Nullable final NoFailCallback<Location> initialLocationCallback) {
        getLocationService().startLocationUpdates(initialLocationCallback);
    }

    @Override
    public void stopLocationUpdates() {
        getLocationService().stopLocationUpdates();
    }

    @Override
    public void addSubscriber(final LocationServiceSubscriber subscriber) {
        getLocationService().addSubscriber(subscriber);
    }

    @Override
    public void removeSubscriber(final LocationServiceSubscriber subscriber) {
        getLocationService().removeSubscriber(subscriber);
    }
}
