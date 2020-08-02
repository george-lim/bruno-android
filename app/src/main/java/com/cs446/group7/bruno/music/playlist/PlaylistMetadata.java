package com.cs446.group7.bruno.music.playlist;

import com.cs446.group7.bruno.music.BrunoTrack;

import java.util.List;

// Contains playlist metadata
public class PlaylistMetadata {

    private final String id;
    private final String name;
    private final int numTracks;

    public PlaylistMetadata(final String id, final String name, final int numTracks) {
        this.id = id;
        this.name = name;
        this.numTracks = numTracks;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getTrackCount() {
        return numTracks;
    }
}
