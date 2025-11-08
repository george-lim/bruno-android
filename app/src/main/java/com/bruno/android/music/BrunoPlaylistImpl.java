package com.bruno.android.music;

import com.bruno.android.music.playlist.PlaylistMetadata;

import java.io.Serializable;
import java.util.List;

public class BrunoPlaylistImpl extends BrunoPlaylist implements Serializable {
    private final PlaylistMetadata metadata;
    private final List<BrunoTrack> tracks;

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

    @Override
    public boolean isEmpty() {
        return tracks.isEmpty();
    }
}
