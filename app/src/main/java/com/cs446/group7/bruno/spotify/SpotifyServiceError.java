package com.cs446.group7.bruno.spotify;

// See https://github.com/spotify/android-sdk/blob/master/app-remote-lib/ERRORS.md for a description of these errors
public enum SpotifyServiceError {
    AUTHENTICATION_FAILED,
    AUTHORIZATION_FAILED,
    APP_NOT_FOUND,
    LOGGED_OUT,
    NOT_LOGGED_IN,
    OFFLINE_MODE,
    CONNECTION_TERMINATED,
    DISCONNECTED,
    REMOTE_SERVICE_ERROR,
    UNSUPPORTED_FEATURE_VERSION,
    OTHER_ERROR;

    public String getErrorMessage() {
        switch (this) {
            case AUTHENTICATION_FAILED:
                return "Authentication with Spotify failed, please log in again with Spotify then try again.";
            case AUTHORIZATION_FAILED:
                return "Spotify was not authorized to be used on the Bruno's behalf. Please allow Bruno to use Spotify.";
            case APP_NOT_FOUND:
                return "Spotify app not found on the device, please install it from the Play Store.";
            case LOGGED_OUT:
                return "You have been logged out of Spotify. Please log in then try again.";
            case NOT_LOGGED_IN:
                return "You are not logged in to Spotify. Please log in through the app and try again.";
            case OFFLINE_MODE:
                return "Spotify is offline mode, but Bruno requires online features. Please enable it via the Spotify app.";
            case CONNECTION_TERMINATED:
                return "Connection to the Spotify app was terminated.";
            case DISCONNECTED:
                return "The Spotify app was disconnected.";
            case REMOTE_SERVICE_ERROR:
                return "The was an error with Spotify's remote service. Please check that there are no songs in your Spotify queue and try again.";
            case UNSUPPORTED_FEATURE_VERSION:
                return "Your current version of Spotify is not compatible with Bruno. Please update it to the latest version.";
            case OTHER_ERROR:
                return "Unexpected error from Spotify, please try again later.";
            default:
                // Should never reach here
                return "Spotify threw an error which Bruno did not account for.";
        }
    }
}
