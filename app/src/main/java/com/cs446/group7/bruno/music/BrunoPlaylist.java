package com.cs446.group7.bruno.music;

import java.util.List;

// Encapsulates playlist information
public abstract class BrunoPlaylist {
    public abstract String getId();
    public abstract String getName();
    public abstract List<BrunoTrack> getTracks();

    public long getDuration() {
        long duration = 0;

        for (BrunoTrack track : getTracks()) {
            duration += track.getDuration();
        }

        return duration;
    }
}
