package com.cs446.group7.bruno.music.playlist;

import com.cs446.group7.bruno.MainActivity;
import com.cs446.group7.bruno.music.BrunoPlaylist;
import com.cs446.group7.bruno.storage.PreferencesStorage;
import com.cs446.group7.bruno.utils.Callback;

public class DynamicPlaylistGeneratorImpl implements PlaylistGenerator {
    private PlaylistGenerator playlistGenerator;
    private PlaylistGenerator mockPlaylistGenerator;

    public DynamicPlaylistGeneratorImpl(final PlaylistGenerator playlistGenerator,
                                        final PlaylistGenerator mockPlaylistGenerator) {
        this.playlistGenerator = playlistGenerator;
        this.mockPlaylistGenerator = mockPlaylistGenerator;
    }

    private PlaylistGenerator getPlaylistGenerator() {
        boolean isUsingMock = MainActivity.getPreferencesStorage().getBoolean(
                PreferencesStorage.KEYS.MOCK_PLAYLIST_GENERATOR,
                true
        );

        return isUsingMock ? mockPlaylistGenerator : playlistGenerator;
    }

    @Override
    public void discoverPlaylist(final Callback<BrunoPlaylist, Exception> callback) {
        getPlaylistGenerator().discoverPlaylist(callback);
    }
}
