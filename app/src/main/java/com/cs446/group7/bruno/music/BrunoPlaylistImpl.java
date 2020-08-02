package com.cs446.group7.bruno.music;

import java.util.List;

public class BrunoPlaylistImpl implements BrunoPlaylist {
    private String id;
    private String name;
    private List<BrunoTrack> tracks;

    public BrunoPlaylistImpl(final String id,
                             final String name,
                             final List<BrunoTrack> tracks) {
        this.id = id;
        this.name = name;
        this.tracks = tracks;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public List<BrunoTrack> getTracks() {
        return tracks;
    }
}
