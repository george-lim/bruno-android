package com.cs446.group7.bruno.routing;

import com.cs446.group7.bruno.MainActivity;
import com.cs446.group7.bruno.location.Coordinate;
import com.cs446.group7.bruno.storage.PreferencesStorage;

public class DynamicRouteGeneratorImpl extends RouteGenerator {
    private RouteGenerator routeGenerator;
    private RouteGenerator mockRouteGenerator;

    public DynamicRouteGeneratorImpl(final RouteGenerator routeGenerator,
                                     final RouteGenerator mockRouteGenerator) {
        this.routeGenerator = routeGenerator;
        this.mockRouteGenerator = mockRouteGenerator;
    }

    private RouteGenerator getRouteGenerator() {
        boolean isUsingMock = MainActivity.getPreferencesStorage().getBoolean(
                PreferencesStorage.KEYS.MOCK_ROUTE_GENERATOR,
                true
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
