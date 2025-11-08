package com.bruno.android.routing;

import com.bruno.android.MainActivity;
import com.bruno.android.location.Coordinate;
import com.bruno.android.storage.PreferencesStorage;

public class DynamicRouteGeneratorImpl extends RouteGenerator {
    private final RouteGenerator routeGenerator;
    private final RouteGenerator mockRouteGenerator;

    public DynamicRouteGeneratorImpl(final RouteGenerator routeGenerator,
                                     final RouteGenerator mockRouteGenerator) {
        this.routeGenerator = routeGenerator;
        this.mockRouteGenerator = mockRouteGenerator;
    }

    private RouteGenerator getRouteGenerator() {
        boolean isUsingMock = MainActivity.getPreferencesStorage().getBoolean(
                PreferencesStorage.KEYS.MOCK_ROUTE_GENERATOR,
                false
        );

        return isUsingMock ? mockRouteGenerator : routeGenerator;
    }

    @Override
    public void generateRoute(final OnRouteResponseCallback callback,
                              final Coordinate origin,
                              double totalDistance,
                              double rotation) {
        getRouteGenerator().generateRoute(callback, origin, totalDistance, rotation);
    }
}
