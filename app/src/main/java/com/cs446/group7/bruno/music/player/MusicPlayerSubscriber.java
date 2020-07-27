package com.cs446.group7.bruno.music.player;

import com.cs446.group7.bruno.music.BrunoTrack;

// Can include other changes to the player in the future
public interface MusicPlayerSubscriber {
    void onTrackChanged(BrunoTrack track);
}
