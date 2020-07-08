package com.cs446.group7.bruno.routing;

import java.util.ArrayList;

// TODO: Remove this once Mohit pushes an actual BrunoTrack class
public class BrunoTrack {
    public String album;
    public String artist;
    public ArrayList<String> artists;
    public long duration; // This is in milliseconds
    public String name;

    public BrunoTrack(long duration) {
        this.duration = duration;
    }
}
