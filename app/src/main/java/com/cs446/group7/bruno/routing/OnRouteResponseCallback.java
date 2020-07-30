package com.cs446.group7.bruno.routing;

import java.util.List;

public interface OnRouteResponseCallback {
    void onRouteReady(List<RouteSegment> routeSegments);
    void onRouteError(RouteGeneratorError error, Exception underlyingException);
}
