package com.cs446.group7.bruno.spotify.playlist;

import com.cs446.group7.bruno.music.BrunoPlaylist;
import com.cs446.group7.bruno.music.playlist.PlaylistMetadata;
import com.cs446.group7.bruno.utils.Callback;

import java.util.List;

/**
 * This class is responsible for implementing functions which call the Spotify Web API.
 */
public interface SpotifyPlaylistAPI {
    void getPublicAuthorizationToken(final Callback<String, Exception> callback);
    void getPlaylist(final String playlistId,
                     final String accessToken,
                     final Callback<BrunoPlaylist, Exception> callback);
    void getUserPlaylistLibrary(final String accessToken,
                                final Callback<List<PlaylistMetadata>, Exception> callback);
}
