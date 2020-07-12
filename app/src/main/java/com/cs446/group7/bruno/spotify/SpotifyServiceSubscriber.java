package com.cs446.group7.bruno.spotify;

import com.cs446.group7.bruno.music.BrunoTrack;

// Can include other changes to the player in the future
public interface SpotifyServiceSubscriber {
    void onTrackChanged(BrunoTrack track);
}
