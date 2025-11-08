package com.bruno.android.music.player;

import com.bruno.android.music.BrunoTrack;

// Can include other changes to the player in the future
public interface MusicPlayerSubscriber {
    // The player changed to a different track
    void onTrackChanged(BrunoTrack track);

    // The player has detected that the fallback playlist needs to be used
    void onFallback();
}
