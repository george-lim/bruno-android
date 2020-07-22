package com.cs446.group7.bruno.preferencesstorage;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.preference.PreferenceManager;

public class PreferencesStorage {
    // Android's share preference key value storage
    private SharedPreferences pref;

    // String identifier to get share preference value
    public static String COMPLETED_ONBOARDING = "completed_onboarding";

    public PreferencesStorage(@NonNull final Context context) {
        pref = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public boolean getBoolean(@NonNull final String key, @NonNull final boolean defaultVal) {
        return pref.getBoolean(key, defaultVal);
    }

    public void setBoolean(@NonNull final String key, @NonNull final boolean value) {
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }
}
