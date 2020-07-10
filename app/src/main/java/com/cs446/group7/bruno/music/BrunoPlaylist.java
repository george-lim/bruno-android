package com.cs446.group7.bruno.music;

import java.util.List;

// Class which encapsulates all playlist information used by our app
// Contains track information through a list of BrunoTracks
public class BrunoPlaylist {

    public final String name;
    public final String description;
    public final int totalTracks;
    public final long totalDuration;
    public final List<BrunoTrack> tracks;

    public BrunoPlaylist(final String name, final String description, final int totalTracks,
                         final long totalDuration, final List<BrunoTrack> tracks) {
        this.name = name;
        this.description = description;
        this.totalTracks = totalTracks;
        this.totalDuration = totalDuration;
        this.tracks = tracks;
    }
}
