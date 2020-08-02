package com.cs446.group7.bruno.viewmodels;

import com.cs446.group7.bruno.music.playlist.PlaylistMetadata;

import java.util.List;

public interface FallbackPlaylistViewModelDelegate {
    void showPlaylistSelectionView(List<PlaylistMetadata> playlists);
    void showNoPlaylistsView();
    void showSpotifyErrorView(final String errorText);
}
