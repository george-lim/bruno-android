package com.bruno.android.location;

import android.location.Location;

import androidx.annotation.Nullable;

import com.bruno.android.MainActivity;
import com.bruno.android.storage.PreferencesStorage;
import com.bruno.android.utils.NoFailCallback;

public class DynamicLocationServiceImpl implements LocationService {
    private final LocationService locationService;
    private final LocationService mockLocationService;

    public DynamicLocationServiceImpl(final LocationService locationService,
                                      final LocationService mockLocationService) {
        this.locationService = locationService;
        this.mockLocationService = mockLocationService;
    }

    private LocationService getLocationService() {
        boolean isUsingMock = MainActivity.getPreferencesStorage().getBoolean(
                PreferencesStorage.KEYS.MOCK_LOCATION_SERVICE,
                false
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
