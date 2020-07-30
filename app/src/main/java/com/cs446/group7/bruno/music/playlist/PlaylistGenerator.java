package com.cs446.group7.bruno.music.playlist;

import com.cs446.group7.bruno.music.BrunoPlaylist;
import com.cs446.group7.bruno.utils.Callback;

import java.util.List;

// Any class which generates a BrunoPlaylist should implement this interface
public interface PlaylistGenerator {
    // Gets a BrunoPlaylist. May require an API call.
    void getPlaylist(final Callback<BrunoPlaylist, Exception> callback);
    void getUserPlaylists(final String accessToken, final Callback<List<PlaylistInfo>, Exception> callback);
}
