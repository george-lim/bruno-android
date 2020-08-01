package com.cs446.group7.bruno.music.playlist;

import com.cs446.group7.bruno.music.BrunoTrack;

import java.util.List;

// Contains information about a playlist, used to select the user's fallback playlist
public class PlaylistInfo {

    private final String id;
    private final String name;
    private final String description;
    private final int numTracks;

    public PlaylistInfo(final String id, final String name, final String description, final int numTracks) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.numTracks = numTracks;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public int getTrackCount() {
        return numTracks;
    }
}
