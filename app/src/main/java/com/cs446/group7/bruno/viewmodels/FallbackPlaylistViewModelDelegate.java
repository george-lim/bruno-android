package com.cs446.group7.bruno.viewmodels;

import com.cs446.group7.bruno.music.playlist.PlaylistMetadata;

import java.util.List;

public interface FallbackPlaylistViewModelDelegate {
    void updatePlaylistData(List<PlaylistMetadata> playlistMetadata);
}
