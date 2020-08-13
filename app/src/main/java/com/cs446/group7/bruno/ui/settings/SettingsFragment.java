package com.cs446.group7.bruno.ui.settings;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.IdRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.cs446.group7.bruno.BuildConfig;
import com.cs446.group7.bruno.MainActivity;
import com.cs446.group7.bruno.R;
import com.cs446.group7.bruno.location.BrunoBot;
import com.cs446.group7.bruno.location.LocationServiceImpl;
import com.cs446.group7.bruno.music.player.MockMusicPlayerImpl;
import com.cs446.group7.bruno.music.playlist.MockPlaylistGeneratorImpl;
import com.cs446.group7.bruno.persistence.FitnessRecordDAO_Impl;
import com.cs446.group7.bruno.persistence.MockFitnessRecordDAO;
import com.cs446.group7.bruno.routing.MockRouteGeneratorImpl;
import com.cs446.group7.bruno.routing.RouteGeneratorImpl;
import com.cs446.group7.bruno.spotify.SpotifyPlayerService;
import com.cs446.group7.bruno.spotify.auth.MockSpotifyAuthServiceImpl;
import com.cs446.group7.bruno.spotify.auth.SpotifyAuthServiceImpl;
import com.cs446.group7.bruno.spotify.playlist.MockSpotifyPlaylistAPIImpl;
import com.cs446.group7.bruno.spotify.playlist.SpotifyPlaylistService;
import com.cs446.group7.bruno.storage.PreferencesStorage;
import com.cs446.group7.bruno.ui.AppbarFormatter;

public class SettingsFragment extends Fragment {

    // MARK: Private members

    private TextView tvMockRouteGeneratorHint;
    private TextView tvMockPlaylistGeneratorHint;
    private TextView tvMockMusicPlayerHint;
    private TextView tvMockLocationServiceHint;
    private TextView tvMockFitnessRecordDAOHint;
    private TextView tvMockSpotifyAuthServiceHint;
    private TextView tvMockSpotifyPlaylistAPIHint;

    // MARK: - Lifecycle methods

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        AppbarFormatter.format(
                (AppCompatActivity) requireActivity(),
                view,
                R.id.appbar_settings,
                getResources().getString(R.string.title_settings),
                false);

        setupUserPreferences(view);
        setupDebugPreferences(view);
        setupAboutBruno(view);
        return view;
    }

    // MARK: - Private methods

    private void setupUserPreferences(final View rootView) {
        LinearLayout avatarItem = rootView.findViewById(R.id.settings_avatar);
        avatarItem.setOnClickListener(view ->
                navigateUsingAction(R.id.action_fragmenttoplevel_to_fragmentsettingavatar));
        LinearLayout fallbackPlaylistItem = rootView.findViewById(R.id.settings_fallback_playlist);
        fallbackPlaylistItem.setOnClickListener(view ->
                navigateUsingAction(R.id.action_fragmenttoplevel_to_fragmentfallbackplaylist));
    }

    private void updateMockRouteGeneratorHint() {
        boolean isUsingMock = MainActivity.getPreferencesStorage().getBoolean(
                PreferencesStorage.KEYS.MOCK_ROUTE_GENERATOR,
                true
        );

        tvMockRouteGeneratorHint.setText(isUsingMock
                ? MockRouteGeneratorImpl.class.getSimpleName()
                : RouteGeneratorImpl.class.getSimpleName());
    }

    private void updateMockPlaylistGeneratorHint() {
        boolean isUsingMock = MainActivity.getPreferencesStorage().getBoolean(
                PreferencesStorage.KEYS.MOCK_PLAYLIST_GENERATOR,
                true
        );

        tvMockPlaylistGeneratorHint.setText(isUsingMock
                ? MockPlaylistGeneratorImpl.class.getSimpleName()
                : SpotifyPlaylistService.class.getSimpleName());
    }

    private void updateMockMusicPlayerHint() {
        boolean isUsingMock = MainActivity.getPreferencesStorage().getBoolean(
                PreferencesStorage.KEYS.MOCK_MUSIC_PLAYER,
                true
        );

        tvMockMusicPlayerHint.setText(isUsingMock
                ? MockMusicPlayerImpl.class.getSimpleName()
                : SpotifyPlayerService.class.getSimpleName());
    }

    private void updateMockLocationServiceHint() {
        boolean isUsingMock = MainActivity.getPreferencesStorage().getBoolean(
                PreferencesStorage.KEYS.MOCK_LOCATION_SERVICE,
                true
        );

        tvMockLocationServiceHint.setText(isUsingMock
                ? BrunoBot.class.getSimpleName()
                : LocationServiceImpl.class.getSimpleName());
    }

    private void updateMockFitnessRecordDAOHint() {
        boolean isUsingMock = MainActivity.getPreferencesStorage().getBoolean(
                PreferencesStorage.KEYS.MOCK_FITNESS_RECORD_DAO,
                true
        );

        tvMockFitnessRecordDAOHint.setText(isUsingMock
                ? MockFitnessRecordDAO.class.getSimpleName()
                : FitnessRecordDAO_Impl.class.getSimpleName());
    }

    private void updateMockSpotifyAuthServiceHint() {
        boolean isUsingMock = MainActivity.getPreferencesStorage().getBoolean(
                PreferencesStorage.KEYS.MOCK_SPOTIFY_AUTH_SERVICE,
                true
        );

        tvMockSpotifyAuthServiceHint.setText(isUsingMock
                ? MockSpotifyAuthServiceImpl.class.getSimpleName()
                : SpotifyAuthServiceImpl.class.getSimpleName());
    }

    private void updateMockSpotifyPlaylistAPIHint() {
        boolean isUsingMock = MainActivity.getPreferencesStorage().getBoolean(
                PreferencesStorage.KEYS.MOCK_SPOTIFY_PLAYLIST_API,
                true
        );

        tvMockSpotifyPlaylistAPIHint.setText(isUsingMock
                ? MockSpotifyPlaylistAPIImpl.class.getSimpleName()
                : SpotifyPlaylistService.class.getSimpleName());
    }

    private void setupDebugPreferences(final View rootView) {
        LinearLayout debugPreferences = rootView.findViewById(R.id.debug_preferences);
        debugPreferences.setVisibility(BuildConfig.DEBUG ? View.VISIBLE : View.GONE);

        tvMockRouteGeneratorHint = rootView.findViewById(
                R.id.debug_preferences_mock_route_generator_hint_text_view
        );
        tvMockPlaylistGeneratorHint = rootView.findViewById(
                R.id.debug_preferences_mock_playlist_generator_hint_text_view
        );
        tvMockMusicPlayerHint = rootView.findViewById(
                R.id.debug_preferences_mock_music_player_hint_text_view
        );
        tvMockLocationServiceHint = rootView.findViewById(
                R.id.debug_preferences_mock_location_service_hint_text_view
        );
        tvMockFitnessRecordDAOHint = rootView.findViewById(
                R.id.debug_preferences_mock_fitness_record_dao_hint_text_view
        );
        tvMockSpotifyAuthServiceHint = rootView.findViewById(
                R.id.debug_preferences_mock_spotify_auth_service_hint_text_view
        );
        tvMockSpotifyPlaylistAPIHint = rootView.findViewById(
                R.id.debug_preferences_mock_spotify_playlist_api_hint_text_view
        );

        updateMockRouteGeneratorHint();
        updateMockPlaylistGeneratorHint();
        updateMockMusicPlayerHint();
        updateMockLocationServiceHint();
        updateMockFitnessRecordDAOHint();
        updateMockSpotifyAuthServiceHint();
        updateMockSpotifyPlaylistAPIHint();

        LinearLayout debugPreferencesMockRouteGenerator = rootView.findViewById(
                R.id.debug_preferences_mock_route_generator
        );

        debugPreferencesMockRouteGenerator.setOnClickListener(view -> {
            boolean isUsingMock = MainActivity.getPreferencesStorage().getBoolean(
                    PreferencesStorage.KEYS.MOCK_ROUTE_GENERATOR,
                    true
            );

            MainActivity
                    .getPreferencesStorage()
                    .putBoolean(PreferencesStorage.KEYS.MOCK_ROUTE_GENERATOR, !isUsingMock);

            updateMockRouteGeneratorHint();
        });

        LinearLayout debugPreferencesMockPlaylistGenerator = rootView.findViewById(
                R.id.debug_preferences_mock_playlist_generator
        );

        debugPreferencesMockPlaylistGenerator.setOnClickListener(view -> {
            boolean isUsingMock = MainActivity.getPreferencesStorage().getBoolean(
                    PreferencesStorage.KEYS.MOCK_PLAYLIST_GENERATOR,
                    true
            );

            MainActivity
                    .getPreferencesStorage()
                    .putBoolean(PreferencesStorage.KEYS.MOCK_PLAYLIST_GENERATOR, !isUsingMock);

            updateMockPlaylistGeneratorHint();
        });

        LinearLayout debugPreferencesMockMusicPlayer = rootView.findViewById(
                R.id.debug_preferences_mock_music_player
        );

        debugPreferencesMockMusicPlayer.setOnClickListener(view -> {
            boolean isUsingMock = MainActivity.getPreferencesStorage().getBoolean(
                    PreferencesStorage.KEYS.MOCK_MUSIC_PLAYER,
                    true
            );

            MainActivity
                    .getPreferencesStorage()
                    .putBoolean(PreferencesStorage.KEYS.MOCK_MUSIC_PLAYER, !isUsingMock);

            updateMockMusicPlayerHint();
        });

        LinearLayout debugPreferencesMockLocationService = rootView.findViewById(
                R.id.debug_preferences_mock_location_service
        );

        debugPreferencesMockLocationService.setOnClickListener(view -> {
            boolean isUsingMock = MainActivity.getPreferencesStorage().getBoolean(
                    PreferencesStorage.KEYS.MOCK_LOCATION_SERVICE,
                    true
            );

            MainActivity
                    .getPreferencesStorage()
                    .putBoolean(PreferencesStorage.KEYS.MOCK_LOCATION_SERVICE, !isUsingMock);

            updateMockLocationServiceHint();
        });

        LinearLayout debugPreferencesMockFitnessRecordDAO = rootView.findViewById(
                R.id.debug_preferences_mock_fitness_record_dao
        );

        debugPreferencesMockFitnessRecordDAO.setOnClickListener(view -> {
            boolean isUsingMock = MainActivity.getPreferencesStorage().getBoolean(
                    PreferencesStorage.KEYS.MOCK_FITNESS_RECORD_DAO,
                    true
            );

            MainActivity
                    .getPreferencesStorage()
                    .putBoolean(PreferencesStorage.KEYS.MOCK_FITNESS_RECORD_DAO, !isUsingMock);

            updateMockFitnessRecordDAOHint();
        });

        LinearLayout debugPreferencesMockSpotifyAuthService = rootView.findViewById(
                R.id.debug_preferences_mock_spotify_auth_service
        );

        debugPreferencesMockSpotifyAuthService.setOnClickListener(view -> {
            boolean isUsingMock = MainActivity.getPreferencesStorage().getBoolean(
                    PreferencesStorage.KEYS.MOCK_SPOTIFY_AUTH_SERVICE,
                    true
            );

            MainActivity
                    .getPreferencesStorage()
                    .putBoolean(PreferencesStorage.KEYS.MOCK_SPOTIFY_AUTH_SERVICE, !isUsingMock);

            updateMockSpotifyAuthServiceHint();
        });

        LinearLayout debugPreferencesMockSpotifyPlaylistAPI = rootView.findViewById(
                R.id.debug_preferences_mock_spotify_playlist_api
        );

        debugPreferencesMockSpotifyPlaylistAPI.setOnClickListener(view -> {
            boolean isUsingMock = MainActivity.getPreferencesStorage().getBoolean(
                    PreferencesStorage.KEYS.MOCK_SPOTIFY_PLAYLIST_API,
                    true
            );

            MainActivity
                    .getPreferencesStorage()
                    .putBoolean(PreferencesStorage.KEYS.MOCK_SPOTIFY_PLAYLIST_API, !isUsingMock);

            updateMockSpotifyPlaylistAPIHint();
        });
    }

    private void setupAboutBruno(final View rootView) {
        LinearLayout termsAndConditionsItem = rootView.findViewById(R.id.settings_terms_and_conditions);
        termsAndConditionsItem.setOnClickListener(view ->
                navigateUsingAction(R.id.action_fragmenttoplvl_to_fragmenttermsandconditions));
        LinearLayout privacyPolicyItem = rootView.findViewById(R.id.settings_privacy_policy);
        privacyPolicyItem.setOnClickListener(view ->
                navigateUsingAction(R.id.action_fragmenttoplvl_to_fragmentprivacypolicy));
        LinearLayout creditsItem = rootView.findViewById(R.id.settings_credits);
        creditsItem.setOnClickListener(view ->
                navigateUsingAction(R.id.action_fragmenttoplvl_to_fragmentcredits));
    }

    private void navigateUsingAction(@IdRes final int action) {
        if (getActivity() != null) {
            NavController navController = Navigation.findNavController(
                    getActivity(),
                    R.id.nav_host_fragment
            );
            navController.navigate(action);
        }
    }
}
