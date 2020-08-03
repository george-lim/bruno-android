package com.cs446.group7.bruno.music;

import java.util.ArrayList;
import java.util.List;

// Encapsulates playlist information
public abstract class BrunoPlaylist {
    public abstract String getId();
    public abstract String getName();
    public abstract BrunoTrack getTrack(int index);

    public List<BrunoTrack> getTracksUpToDuration(long duration) {
        List<BrunoTrack> result = new ArrayList<>();
        int trackIndex;

        for (trackIndex = 0; true; ++trackIndex) {
            BrunoTrack track = getTrack(trackIndex);

            if (duration - track.getDuration() <= 0) {
                break;
            }

            duration -= track.getDuration();
            result.add(track);
        }

        result.add(getTrack(trackIndex).split(duration));

        return result;
    }
}
