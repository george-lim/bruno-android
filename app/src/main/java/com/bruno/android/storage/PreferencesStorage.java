package com.bruno.android.storage;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.preference.PreferenceManager;

import com.bruno.android.R;

public class PreferencesStorage {
    // Android's share preference key value storage
    private final SharedPreferences pref;

    // String identifier to get share preference value

    public static class KEYS {
        public static final String COMPLETED_ONBOARDING = "completed_onboarding";
        public static final String USER_AVATAR = "user_avatar";
        public static final String FALLBACK_PLAYLIST_ID = "fallback_playlist_id";
        public static final String MOCK_ROUTE_GENERATOR = "mock_route_generator";
        public static final String MOCK_PLAYLIST_GENERATOR = "mock_playlist_generator";
        public static final String MOCK_MUSIC_PLAYER = "mock_music_player";
        public static final String MOCK_LOCATION_SERVICE = "mock_location_service";
        public static final String MOCK_FITNESS_RECORD_DAO = "mock_fitness_record_dao";
        public static final String MOCK_SPOTIFY_AUTH_SERVICE = "mock_spotify_auth_service";
        public static final String MOCK_SPOTIFY_PLAYLIST_API = "mock_spotify_playlist_api";
    }

    public static final int DEFAULT_AVATAR = R.drawable.ic_avatar_1;

    public PreferencesStorage(@NonNull final Context context) {
        pref = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public boolean getBoolean(final String key, final boolean defaultVal) {
        return pref.getBoolean(key, defaultVal);
    }

    public void putBoolean(final String key, final boolean value) {
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    public int getInt(final String key, final int defaultVal) {
        return pref.getInt(key, defaultVal);
    }

    public void putInt(final String key, final int value) {
        SharedPreferences.Editor editor = pref.edit();
        editor.putInt(key, value);
        editor.apply();
    }

    public String getString(final String key, final String defaultVal) {
        return pref.getString(key, defaultVal);
    }

    public void putString(final String key, final String value) {
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(key, value);
        editor.apply();
    }
}
