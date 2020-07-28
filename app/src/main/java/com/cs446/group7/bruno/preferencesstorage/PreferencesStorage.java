package com.cs446.group7.bruno.preferencesstorage;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.preference.PreferenceManager;

import com.cs446.group7.bruno.R;

public class PreferencesStorage {
    // Android's share preference key value storage
    private SharedPreferences pref;

    // String identifier to get share preference value
    public static String COMPLETED_ONBOARDING = "completed_onboarding";
    public static String USER_AVATAR = "user_avatar";
    public static int DEFAULT_AVATAR = R.drawable.ic_avatar_1;

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
}
