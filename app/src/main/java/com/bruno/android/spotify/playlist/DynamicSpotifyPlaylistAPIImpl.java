package com.bruno.android.spotify.playlist;

import com.bruno.android.MainActivity;
import com.bruno.android.music.BrunoPlaylist;
import com.bruno.android.music.playlist.PlaylistMetadata;
import com.bruno.android.storage.PreferencesStorage;
import com.bruno.android.utils.Callback;

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
