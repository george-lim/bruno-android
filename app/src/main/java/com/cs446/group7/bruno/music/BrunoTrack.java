package com.cs446.group7.bruno.music;

import java.util.ArrayList;

// Simple container class containing information about a track
public class BrunoTrack {

    public final String name;
    public final String album;
    public final long duration; // Milliseconds
    public final ArrayList<String> artists;

    public BrunoTrack(final String name, final String album, final long duration,
                      final ArrayList<String> artists) {
        this.name = name;
        this.album = album;
        this.duration = duration;
        this.artists = artists;
    }

}
