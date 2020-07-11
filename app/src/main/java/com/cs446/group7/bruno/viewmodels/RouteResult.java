package com.cs446.group7.bruno.viewmodels;

import com.cs446.group7.bruno.routing.Route;
import com.cs446.group7.bruno.routing.RouteGeneratorError;

public class RouteResult {
    private Route route;
    private RouteGeneratorError error;
    private Exception underlyingException;

    public RouteResult(Route route, RouteGeneratorError error, Exception underlyingException) {
        this.route = route;
        this.error = error;
        this.underlyingException = underlyingException;
    }

    public Route getRoute() {
        return route;
    }

    public RouteGeneratorError getError() {
        return error;
    }

    public Exception getUnderlyingException() {
        return underlyingException;
    }
}
