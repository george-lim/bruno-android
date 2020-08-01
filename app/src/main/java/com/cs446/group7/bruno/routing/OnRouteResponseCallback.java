package com.cs446.group7.bruno.routing;

import java.util.List;

public interface OnRouteResponseCallback {
    void onRouteReady(final List<RouteSegment> routeSegments);
    void onRouteError(final RouteGeneratorException exception);
}
