package com.bruno.android.music.playlist;

import com.bruno.android.MainActivity;
import com.bruno.android.music.BrunoPlaylist;
import com.bruno.android.storage.PreferencesStorage;
import com.bruno.android.utils.Callback;

public class DynamicPlaylistGeneratorImpl implements PlaylistGenerator {
    private final PlaylistGenerator playlistGenerator;
    private final PlaylistGenerator mockPlaylistGenerator;

    public DynamicPlaylistGeneratorImpl(final PlaylistGenerator playlistGenerator,
                                        final PlaylistGenerator mockPlaylistGenerator) {
        this.playlistGenerator = playlistGenerator;
        this.mockPlaylistGenerator = mockPlaylistGenerator;
    }

    private PlaylistGenerator getPlaylistGenerator() {
        boolean isUsingMock = MainActivity.getPreferencesStorage().getBoolean(
                PreferencesStorage.KEYS.MOCK_PLAYLIST_GENERATOR,
                false
        );

        return isUsingMock ? mockPlaylistGenerator : playlistGenerator;
    }

    @Override
    public void discoverPlaylist(final Callback<BrunoPlaylist, Exception> callback) {
        getPlaylistGenerator().discoverPlaylist(callback);
    }
}
