package com.cs446.group7.bruno.viewmodels;

import com.cs446.group7.bruno.music.BrunoTrack;

import java.util.List;

public interface FitnessDetailsViewModelDelegate {
    void setupUI(int youRunDuration,
                 int brunoRunDuration,
                 int stepCount,
                 int timeFromGoal);
    void setupTracklist(List<BrunoTrack> tracklist);
    void displayCrown(int youRunDuration, int brunoRunDuration);
}
