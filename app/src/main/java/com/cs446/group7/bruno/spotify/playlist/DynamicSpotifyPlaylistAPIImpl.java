package com.cs446.group7.bruno.spotify.playlist;

import com.cs446.group7.bruno.MainActivity;
import com.cs446.group7.bruno.music.BrunoPlaylist;
import com.cs446.group7.bruno.music.playlist.PlaylistMetadata;
import com.cs446.group7.bruno.storage.PreferencesStorage;
import com.cs446.group7.bruno.utils.Callback;

import java.util.List;

public class DynamicSpotifyPlaylistAPIImpl implements SpotifyPlaylistAPI {
    private SpotifyPlaylistAPI playlistAPI;
    private SpotifyPlaylistAPI mockPlaylistAPI;

    public DynamicSpotifyPlaylistAPIImpl(final SpotifyPlaylistAPI playlistAPI,
                                         final SpotifyPlaylistAPI mockPlaylistAPI) {
        this.playlistAPI = playlistAPI;
        this.mockPlaylistAPI = mockPlaylistAPI;
    }

    private SpotifyPlaylistAPI getPlaylistAPI() {
        boolean isUsingMock = MainActivity.getPreferencesStorage().getBoolean(
                PreferencesStorage.KEYS.MOCK_SPOTIFY_PLAYLIST_API,
                true
        );

        return isUsingMock ? mockPlaylistAPI : playlistAPI;
    }

    @Override
    public void getPlaylist(final String accessToken,
                            final String playlistId,
                            final Callback<BrunoPlaylist, Exception> callback) {
        getPlaylistAPI().getPlaylist(accessToken, playlistId, callback);
    }

    @Override
    public void getUserPlaylistLibrary(final String accessToken,
                                       final Callback<List<PlaylistMetadata>, Exception> callback) {
        getPlaylistAPI().getUserPlaylistLibrary(accessToken, callback);
    }
}
