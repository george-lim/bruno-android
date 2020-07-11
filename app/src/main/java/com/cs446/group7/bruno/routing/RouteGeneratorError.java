package com.cs446.group7.bruno.routing;

public enum RouteGeneratorError {
    PARSE_ERROR,
    SERVER_ERROR,
    NO_CONNECTION_ERROR,
    OTHER_ERROR;

    public String getDescription() {
        switch (this) {
            case PARSE_ERROR:
                return "Error parsing route, please try again!";
            case SERVER_ERROR:
                return "A server error occurred, please try again!";
            case NO_CONNECTION_ERROR:
                return "No network, please enable internet access!";
            default:
                return "Something went wrong, please try again!";
        }
    }
}
