package com.cs446.group7.bruno.music;

// Simple container class containing information about a track
public class BrunoTrack {
    private String name;
    private String album;
    private long duration; // Milliseconds

    public BrunoTrack(final String name, final String album, long duration) {
        this.name = name;
        this.album = album;
        this.duration = duration;
    }

    public String getName() {
        return name;
    }

    public String getAlbum() {
        return album;
    }

    public long getDuration() {
        return duration;
    }
}
