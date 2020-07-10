package com.cs446.group7.bruno.spotify;

import com.cs446.group7.bruno.music.BrunoTrack;

public interface SpotifyServiceSubscriber {
    void onTrackChanged(BrunoTrack track);
    void onError(Exception exception);
}
