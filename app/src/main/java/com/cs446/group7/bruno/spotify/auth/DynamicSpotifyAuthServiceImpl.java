package com.cs446.group7.bruno.spotify.auth;

import com.cs446.group7.bruno.MainActivity;
import com.cs446.group7.bruno.storage.PreferencesStorage;
import com.cs446.group7.bruno.utils.Callback;

public class DynamicSpotifyAuthServiceImpl implements SpotifyAuthService {
    private SpotifyAuthService authService;
    private SpotifyAuthService mockAuthService;

    public DynamicSpotifyAuthServiceImpl(final SpotifyAuthService authService,
                                         final SpotifyAuthService mockAuthService) {
        this.authService = authService;
        this.mockAuthService = mockAuthService;
    }

    private SpotifyAuthService getAuthService() {
        boolean isUsingMock = MainActivity.getPreferencesStorage().getBoolean(
                PreferencesStorage.KEYS.MOCK_SPOTIFY_AUTH_SERVICE,
                true
        );

        return isUsingMock ? mockAuthService : authService;
    }

    @Override
    public void requestUserAuth(final Callback<String, Void> clientCallback) {
        getAuthService().requestUserAuth(clientCallback);
    }

    @Override
    public void checkIfUserIsPremium(final String token,
                                     final Callback<Boolean, Exception> callback) {
        getAuthService().checkIfUserIsPremium(token, callback);
    }
}
