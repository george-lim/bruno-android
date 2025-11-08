package com.bruno.android.spotify.playlist;

import com.bruno.android.music.BrunoPlaylist;
import com.bruno.android.music.playlist.PlaylistMetadata;
import com.bruno.android.utils.Callback;

import java.util.List;

/**
 * This class is responsible for implementing functions which call the Spotify Web API.
 */
public interface SpotifyPlaylistAPI {
    void getPlaylist(final String accessToken,
                     final String playlistId,
                     final Callback<BrunoPlaylist, Exception> callback);

    void getUserPlaylistLibrary(final String accessToken,
                                final Callback<List<PlaylistMetadata>, Exception> callback);
}
