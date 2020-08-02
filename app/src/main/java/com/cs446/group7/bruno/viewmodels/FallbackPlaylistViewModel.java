package com.cs446.group7.bruno.viewmodels;

import com.cs446.group7.bruno.BuildConfig;
import com.cs446.group7.bruno.MainActivity;
import com.cs446.group7.bruno.music.playlist.PlaylistMetadata;
import com.cs446.group7.bruno.spotify.auth.SpotifyAuthService;
import com.cs446.group7.bruno.spotify.playlist.MockSpotifyPlaylistAPIImpl;
import com.cs446.group7.bruno.spotify.playlist.SpotifyPlaylistAPI;
import com.cs446.group7.bruno.utils.Callback;

import java.util.List;

public class FallbackPlaylistViewModel {

    private FallbackPlaylistViewModelDelegate delegate;
    private String token;

    public FallbackPlaylistViewModel(FallbackPlaylistViewModelDelegate delegate) {
        this.delegate = delegate;
    }

    public void getUserPrivatePlaylist() {
        SpotifyAuthService auth = MainActivity.getSpotifyService().getAuthService();
        auth.requestUserAuth(new Callback<String, Void>() {
            @Override
            public void onSuccess(String token) {
                getSpotifyPlaylistAPI().getUserPlaylistLibrary(token, new Callback<List<PlaylistMetadata>, Exception>() {
                    @Override
                    public void onSuccess(List<PlaylistMetadata> playlistMetadata) {
                        delegate.updatePlaylistData(playlistMetadata);
                    }

                    @Override
                    public void onFailed(Exception result) {

                    }
                });
            }

            @Override
            public void onFailed(Void result) {

            }
        });
    }

    private SpotifyPlaylistAPI getSpotifyPlaylistAPI() {
        return BuildConfig.DEBUG
                ? new MockSpotifyPlaylistAPIImpl()
                : MainActivity.getSpotifyService().getPlaylistService();
    }
}
