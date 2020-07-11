package com.cs446.group7.bruno.spotify;

public enum SpotifyServiceError {
    AUTHENTICATION_FAILED("Authenticate with Spotify failed, please log in again in Spotify then trying again."),
    AUTHORIZATION_FAILED("Spotify was not authorized to be used on the Bruno's behalf. Please allow Bruno to use Spotify."),
    APP_NOT_FOUND("Spotify app not found on the device, please install it from the Play Store."),
    LOGGED_OUT("You have been logged out of Spotify. Please log in then try again."),
    NOT_LOGGED_IN("You are not logged in to Spotify. Please log in through the app and try again."),
    OFFLINE_MODE("Spotify is offline mode, but Bruno requires an online features. Please enable it via the Spotity app."),
    CONNECTION_TERMINATED("Connection to the Spotify app was terminated."),
    DISCONNECTED("The Spotify app was disconnected."),
    REMOTE_SERVICE_ERROR("The was an error with Spotify's remote service, please check that there are no songs in your spotify queue, and try again."),
    UNSUPPORTED_FEATURE_VERSION("Your current version of Spotify is not compatible with Bruno. Please update it to the latest version."),
    OTHER_ERROR("Unexpected error from Spotify, please try again later.");

    private String errorMessage;

    SpotifyServiceError(final String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}
