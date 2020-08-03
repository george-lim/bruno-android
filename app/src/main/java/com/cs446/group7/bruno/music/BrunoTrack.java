package com.cs446.group7.bruno.music;

import java.io.Serializable;

// Simple container class containing information about a track
public class BrunoTrack implements Serializable {
    private String name;
    private String artists;
    private long duration; // Milliseconds

    public BrunoTrack(final String name, final String artists, long duration) {
        this.name = name;
        this.artists = artists;
        this.duration = duration;
    }

    public String getName() {
        return name;
    }

    public String getArtists() {
        return artists;
    }

    public long getDuration() {
        return duration;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof BrunoTrack)) return false;

        BrunoTrack otherTrack = (BrunoTrack) obj;
        return name.equals(otherTrack.name) &&
                artists.equals(otherTrack.artists) &&
                duration == otherTrack.duration;
    }
}
