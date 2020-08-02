package com.cs446.group7.bruno.music;

import java.util.ArrayList;
import java.util.List;

// Encapsulates playlist information
public abstract class BrunoPlaylist {
    public abstract String getId();
    public abstract String getName();
    public abstract List<BrunoTrack> getTracks();

    public List<BrunoTrack> getTracksUpToDuration(long duration) {
        List<BrunoTrack> tracks = getTracks();
        List<BrunoTrack> result = new ArrayList<>();
        int i;

        for (i = 0; true; i = (i + 1) % tracks.size()) {
            BrunoTrack track = tracks.get(i);

            if (duration - track.getDuration() <= 0) {
                break;
            }

            duration -= track.getDuration();
            result.add(track);
        }

        result.add(tracks.get(i).split(duration));

        return result;
    }
}
