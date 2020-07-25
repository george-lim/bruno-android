package com.cs446.group7.bruno.spotify;

import androidx.annotation.Nullable;

import com.cs446.group7.bruno.music.player.MusicPlayerException;
import com.spotify.android.appremote.api.error.AuthenticationFailedException;
import com.spotify.android.appremote.api.error.CouldNotFindSpotifyApp;
import com.spotify.android.appremote.api.error.LoggedOutException;
import com.spotify.android.appremote.api.error.NotLoggedInException;
import com.spotify.android.appremote.api.error.OfflineModeException;
import com.spotify.android.appremote.api.error.SpotifyConnectionTerminatedException;
import com.spotify.android.appremote.api.error.SpotifyDisconnectedException;
import com.spotify.android.appremote.api.error.SpotifyRemoteServiceException;
import com.spotify.android.appremote.api.error.UnsupportedFeatureVersionException;
import com.spotify.android.appremote.api.error.UserNotAuthorizedException;

public class SpotifyPlayerException extends MusicPlayerException {
    public SpotifyPlayerException(Throwable throwable) {
        super(throwable);
    }

    @Nullable
    @Override
    public String getMessage() {
        Throwable throwable = getCause();

        if (throwable instanceof AuthenticationFailedException) {
            return "Authentication with Spotify failed, please log in again with Spotify then try again.";
        }
        else if (throwable instanceof UserNotAuthorizedException) {
            return "Spotify was not authorized to be used on the Bruno's behalf. Please allow Bruno to use Spotify.";
        }
        else if (throwable instanceof CouldNotFindSpotifyApp) {
            return "Spotify app not found on the device, please install it from the Play Store.";
        }
        else if (throwable instanceof LoggedOutException) {
            return "You have been logged out of Spotify. Please log in then try again.";
        }
        else if (throwable instanceof NotLoggedInException) {
            return "You are not logged in to Spotify. Please log in through the app and try again.";
        }
        else if (throwable instanceof OfflineModeException) {
            return "Spotify is in offline mode, but Bruno requires online features. Please enable it via the Spotify app.";
        }
        else if (throwable instanceof SpotifyConnectionTerminatedException) {
            return "Connection to the Spotify app was terminated.";
        }
        else if (throwable instanceof SpotifyDisconnectedException) {
            return "The Spotify app was disconnected.";
        }
        else if (throwable instanceof SpotifyRemoteServiceException) {
            return "The was an error with Spotify's remote service. Please check that there are no songs in your Spotify queue and try again.";
        }
        else if (throwable instanceof UnsupportedFeatureVersionException) {
            return "Your current version of Spotify is not compatible with Bruno. Please update it to the latest version.";
        }
        else {
            return throwable.getMessage();
        }
    }
}
