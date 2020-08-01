package com.cs446.group7.bruno.viewmodels;

import com.cs446.group7.bruno.music.playlist.PlaylistInfo;

import java.util.List;

public interface FallbackPlaylistViewModelDelegate {
    void updatePlaylistData(List<PlaylistInfo> playlistInfos);
}
