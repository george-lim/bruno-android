package com.cs446.group7.bruno.music;

import java.util.List;

// Class which encapsulates all playlist information used by our app
// Contains track information through a list of BrunoTracks
public class BrunoPlaylist {

    final public String name;
    final public String description;
    final int totalTracks;
    final long totalDuration;
    final public List<BrunoTrack> tracks;

    public BrunoPlaylist(final String inputName, final String inputDescription, final int inputTotalTracks,
                         final long inputTotalDuration, final List<BrunoTrack> inputTracks) {
        name = inputName;
        description = inputDescription;
        totalTracks = inputTotalTracks;
        totalDuration = inputTotalDuration;
        tracks = inputTracks;
    }

}
