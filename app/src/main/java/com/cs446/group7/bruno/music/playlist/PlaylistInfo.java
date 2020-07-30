package com.cs446.group7.bruno.music.playlist;

import com.cs446.group7.bruno.music.BrunoTrack;

import java.util.List;

// Contains information about a playlist, used to select the user's fallback playlist
public class PlaylistInfo {

    public final String id;
    public final String name;
    public final String description;
    public final int numTracks;

    public PlaylistInfo(final String id, final String name, final String description, final int numTracks) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.numTracks = numTracks;
    }
}
