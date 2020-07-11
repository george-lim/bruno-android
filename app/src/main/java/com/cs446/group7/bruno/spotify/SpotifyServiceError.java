package com.cs446.group7.bruno.spotify;

public enum SpotifyServiceError {
    AUTHENTICATION_FAILED("User failed to authenticate with Spotify"),
    AUTHORIZATION_FAILED("User did not authorize Spotify to be used on the client's behalf"),
    APP_NOT_FOUND("The Spotify app cannot be found on the device"),
    LOGGED_OUT("User logged out of Spotify"),
    NOT_LOGGED_IN("User is not logged in to Spotify"),
    OFFLINE_MODE("User is in offline mode, but the app requires an online feature"),
    CONNECTION_TERMINATED("Connection to the Spotify app was terminated"),
    DISCONNECTED("The Spotify app was disconnected"),
    REMOTE_SERVICE_ERROR("The client is in an invalid state"),
    UNSUPPORTED_FEATURE_VERSION("the feature set of the Spotify app and the current version of App Remote is not compatible"),
    OTHER_ERROR("Unexpected error");

    private String errorMessage;

    SpotifyServiceError(final String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}
