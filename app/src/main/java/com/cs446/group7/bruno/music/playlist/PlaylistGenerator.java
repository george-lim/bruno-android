package com.cs446.group7.bruno.music.playlist;

import com.cs446.group7.bruno.music.BrunoPlaylist;
import com.cs446.group7.bruno.utils.Callback;

public interface PlaylistGenerator {
    // Generates a BrunoPlaylist using default values
    void discoverPlaylist(final Callback<BrunoPlaylist, Exception> callback);
}
