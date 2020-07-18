package com.cs446.group7.bruno.music.playlist;

import com.cs446.group7.bruno.music.BrunoPlaylist;
import com.cs446.group7.bruno.utils.Callback;

// Any class which generates a BrunoPlaylist should implement this interface
public interface PlaylistGenerator {
    // Gets a BrunoPlaylist. May require an API call.
    void getPlaylist(String playlistId, Callback<BrunoPlaylist, Exception> callback);
}
