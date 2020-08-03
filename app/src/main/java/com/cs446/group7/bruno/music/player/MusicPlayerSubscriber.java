package com.cs446.group7.bruno.music.player;

import com.cs446.group7.bruno.music.BrunoTrack;

// Can include other changes to the player in the future
public interface MusicPlayerSubscriber {
    // The player changed to a different track
    void onTrackChanged(BrunoTrack track);
    // The player has detected that the fallback playlist needs to be used
    void onFallback();
}
