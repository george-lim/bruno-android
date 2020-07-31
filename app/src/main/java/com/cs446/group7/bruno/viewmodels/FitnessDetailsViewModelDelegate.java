package com.cs446.group7.bruno.viewmodels;

import com.cs446.group7.bruno.music.BrunoTrack;

import java.util.List;

public interface FitnessDetailsViewModelDelegate {
    void setupUI(int youRunDuration,
                 int brunoRunDuration,
                 int stepCount,
                 int timeFromGoal);
    void setupTracklistListView(List<BrunoTrack> tracklistList);
    void displayCrown(int youRunDuration, int brunoRunDuration);
}
