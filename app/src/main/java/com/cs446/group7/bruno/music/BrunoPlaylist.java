package com.cs446.group7.bruno.music;

import java.util.List;

// Class which encapsulates all playlist information used by our app
// Contains track information through a list of BrunoTracks
public class BrunoPlaylist {

    public final String id;
    public final String name;
    public final String description;
    public final List<BrunoTrack> tracks;

    public BrunoPlaylist(final String id,
                         final String name,
                         final String description,
                         final List<BrunoTrack> tracks) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.tracks = tracks;
    }
}
