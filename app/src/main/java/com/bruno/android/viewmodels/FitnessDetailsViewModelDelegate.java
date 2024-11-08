package com.bruno.android.viewmodels;

import com.bruno.android.models.TrackSegment;
import com.bruno.android.music.BrunoTrack;
import com.google.android.gms.maps.CameraUpdate;

import java.util.List;

public interface FitnessDetailsViewModelDelegate {
    void setupUI(final String leaderboardUserTimeText,
                 final String leaderboardBrunoTimeText,
                 final String statsDistanceText,
                 final String statsStepsText,
                 final String statsClockText,
                 final String appBarTitle,
                 final FitnessDetailsViewModel.Winner winner,
                 final List<BrunoTrack> tracks);
    void drawRoute(final List<TrackSegment> trackSegments, float routeWidth);
    void moveCamera(final CameraUpdate cameraUpdate);
}
