package com.cs446.group7.bruno.music;

import java.util.List;

// Encapsulates playlist information
public interface BrunoPlaylist {
    String getId();
    String getName();
    List<BrunoTrack> getTracks();
}
