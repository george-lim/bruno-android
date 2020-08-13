package com.bruno.android.music.playlist;

import com.bruno.android.music.BrunoPlaylist;
import com.bruno.android.utils.Callback;

public interface PlaylistGenerator {
    // Generates a BrunoPlaylist using default values
    void discoverPlaylist(final Callback<BrunoPlaylist, Exception> callback);
}
