package com.cs446.group7.bruno.viewmodels;

import com.cs446.group7.bruno.BuildConfig;
import com.cs446.group7.bruno.MainActivity;
import com.cs446.group7.bruno.music.playlist.MockPlaylistGeneratorImpl;
import com.cs446.group7.bruno.music.playlist.PlaylistGenerator;
import com.cs446.group7.bruno.music.playlist.PlaylistInfo;
import com.cs446.group7.bruno.spotify.auth.SpotifyAuthService;
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
                getPlaylistGenerator().getUserPlaylists(token, new Callback<List<PlaylistInfo>, Exception>() {
                    @Override
                    public void onSuccess(List<PlaylistInfo> playlistInfos) {
                        delegate.updatePlaylistData(playlistInfos);
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

    private PlaylistGenerator getPlaylistGenerator() {
        return BuildConfig.DEBUG
                ? new MockPlaylistGeneratorImpl()
                : MainActivity.getSpotifyService().getPlaylistService();
    }
}
