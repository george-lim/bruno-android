package com.bruno.android.spotify.auth;

import com.bruno.android.MainActivity;
import com.bruno.android.storage.PreferencesStorage;
import com.bruno.android.utils.Callback;

public class DynamicSpotifyAuthServiceImpl implements SpotifyAuthService {
    private final SpotifyAuthService authService;
    private final SpotifyAuthService mockAuthService;

    public DynamicSpotifyAuthServiceImpl(final SpotifyAuthService authService,
                                         final SpotifyAuthService mockAuthService) {
        this.authService = authService;
        this.mockAuthService = mockAuthService;
    }

    private SpotifyAuthService getAuthService() {
        boolean isUsingMock = MainActivity.getPreferencesStorage().getBoolean(
                PreferencesStorage.KEYS.MOCK_SPOTIFY_AUTH_SERVICE,
                false
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
