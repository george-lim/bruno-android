package com.cs446.group7.bruno.spotify;

import java.util.ArrayList;

// Simple container class containing information about a track
public class BrunoTrack {
    public final String album;
    public final String artist;
    public final ArrayList<String> artists;
    public final long duration;
    public final String name;

    public BrunoTrack(String inputAlbum, String inputArtist, ArrayList<String> inputArtists,
                      long inputDuration, String inputName) {
        album = inputAlbum;
        artist = inputArtist;
        artists = inputArtists;
        duration = inputDuration;
        name = inputName;
    }

}
