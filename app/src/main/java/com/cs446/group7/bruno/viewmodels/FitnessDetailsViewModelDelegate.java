package com.cs446.group7.bruno.viewmodels;

import com.cs446.group7.bruno.colourizedroute.ColourizedRoute;
import com.cs446.group7.bruno.music.BrunoTrack;

import java.util.List;

public interface FitnessDetailsViewModelDelegate {
    void setupUI(int youRunDuration, int brunoRunDuration, int stepCount);
    void setupTracklist(final List<BrunoTrack> trackList);
    void drawRoute(final ColourizedRoute colourizedRoute);
}
