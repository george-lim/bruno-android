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

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }

        if (!(o instanceof BrunoTrack)) {
            return false;
        }

        BrunoTrack a = (BrunoTrack) o;
        return a.album == this.album &&
                a.artist == this.artist &&
                a.artists.equals(this.artists) &&
                a.duration == this.duration &&
                a.name == this.name;
    }
}
