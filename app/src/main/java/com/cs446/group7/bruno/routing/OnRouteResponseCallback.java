package com.cs446.group7.bruno.routing;

public interface OnRouteResponseCallback {
    void onRouteReady(Route route);
    void onRouteError(RouteGeneratorError error);
}
