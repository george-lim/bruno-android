package com.cs446.group7.bruno.viewmodels;

import com.cs446.group7.bruno.models.FitnessModel;
import com.cs446.group7.bruno.models.TrackSegment;
import com.cs446.group7.bruno.music.BrunoTrack;
import com.google.android.gms.maps.CameraUpdate;

import java.util.List;

public interface FitnessDetailsViewModelDelegate {
    void setupUI(final String leaderboardYouTimeText,
                 final String leaderboardBrunoTimeText,
                 final String statsDistanceText,
                 final String statsStepsText,
                 final String statsClockText,
                 final String appBarTitle,
                 final FitnessModel.Winner winner);
    void setupTracklist(final List<BrunoTrack> tracks);
    void drawRoute(final List<TrackSegment> trackSegments, float routeWidth);
    void moveCamera(final CameraUpdate cameraUpdate);
}