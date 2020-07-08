package com.cs446.group7.bruno.music;

public abstract class PlaylistGenerator {
    abstract public void getPlaylist(OnPlaylistCallback callback, String playlistId);
}