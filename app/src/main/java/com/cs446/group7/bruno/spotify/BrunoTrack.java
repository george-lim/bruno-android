package com.cs446.group7.bruno.spotify;

import java.util.ArrayList;

// Simple container class containing information about a track
public class BrunoTrack {
    public final String name;
    public final String album;
    public final long duration; // Milliseconds
    public final ArrayList<String> artists;

    public BrunoTrack(final String inputName, final String inputAlbum, final long inputDuration,
                      final ArrayList<String> inputArtists) {
        name = inputName;
        album = inputAlbum;
        duration = inputDuration;
        artists = inputArtists;
    }

}
