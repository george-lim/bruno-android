package com.cs446.group7.bruno.music;

import com.cs446.group7.bruno.music.playlist.PlaylistMetadata;

import java.util.List;

public class BrunoPlaylistImpl extends BrunoPlaylist {
    private PlaylistMetadata metadata;
    private List<BrunoTrack> tracks;

    public BrunoPlaylistImpl(final String id,
                             final String name,
                             final List<BrunoTrack> tracks) {
        this.metadata = new PlaylistMetadata(id, name, tracks.size());
        this.tracks = tracks;
    }

    @Override
    public String getId() {
        return metadata.getId();
    }

    @Override
    public String getName() {
        return metadata.getName();
    }

    @Override
    public BrunoTrack getTrack(int index) {
        return tracks.get(index % tracks.size());
    }
}
