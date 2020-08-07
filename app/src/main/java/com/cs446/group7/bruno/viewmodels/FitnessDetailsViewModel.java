package com.cs446.group7.bruno.viewmodels;

import android.content.Context;
import android.content.res.Resources;
import android.os.Build;

import com.cs446.group7.bruno.R;
import com.cs446.group7.bruno.models.FitnessModel;
import com.cs446.group7.bruno.models.TrackSegment;
import com.cs446.group7.bruno.persistence.FitnessRecordData;
import com.cs446.group7.bruno.utils.TimeUtils;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import java.util.List;
import java.util.Locale;

public class FitnessDetailsViewModel {

    private FitnessModel model;
    private FitnessDetailsViewModelDelegate delegate;

    // MARK: - Lifecycle methods

    public FitnessDetailsViewModel(final Context context,
                                   final FitnessModel model,
                                   final FitnessDetailsViewModelDelegate delegate) {
        this.delegate = delegate;
        this.model = model;

        // Setup stats and tracks, map is setup later when the map UI is ready
        setupStats(context.getResources());
        setupTrackList();
    }

    // MARK: - Private methods

    @SuppressWarnings("deprecation")
    private Locale getLocale(final Resources resources) {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.N
                ? resources.getConfiguration().getLocales().get(0)
                : resources.getConfiguration().locale;
    }

    private void setupStats(final Resources resources) {
        final FitnessRecordData fitnessRecordData = model.getCurrentFitnessRecord();
        long userDuration = fitnessRecordData.getUserDuration() / 1000;
        long brunoDuration = fitnessRecordData.getExpectedDuration() / 1000;
        int stepCount = fitnessRecordData.getSteps();

        String leaderboardYouTimeText = TimeUtils.getDurationString(userDuration);
        String leaderboardBrunoTimeText = TimeUtils.getDurationString(brunoDuration);
        String statsDistanceText = String.format(
                getLocale(resources),
                "%.1f km",
                fitnessRecordData.getRouteDistance() / 1000
        );
        String statsStepsText = String.format(
                resources.getString(R.string.fitness_details_steps_placeholder),
                stepCount
        );
        String statsClockText = TimeUtils.formatDuration(userDuration);
        String appBarTitle = TimeUtils.formatDateTime(
                fitnessRecordData.getStartTime(),
                TimeUtils.DATE_TIME_FORMAT,
                getLocale(resources)
        );
        FitnessModel.Winner winner;

        if (userDuration < brunoDuration) {
            winner = FitnessModel.Winner.YOU;
        } else if (userDuration > brunoDuration) {
            winner = FitnessModel.Winner.BRUNO;
        } else {
            winner = FitnessModel.Winner.TIE;
        }

        delegate.setupUI(
                leaderboardYouTimeText,
                leaderboardBrunoTimeText,
                statsDistanceText,
                statsStepsText,
                statsClockText,
                appBarTitle,
                winner
        );
    }

    private void setupTrackList() {
        delegate.setupTracklist(model.getCurrentFitnessRecord().getTracksUserPlayed());
    }

    private void setupMap() {
        final List<TrackSegment> trackSegments = model.getCurrentFitnessRecord().getTrackSegments();
        float routeWidth = 14;
        delegate.drawRoute(trackSegments, routeWidth);
    }

    private void moveCamera() {
        final List<TrackSegment> trackSegments = model.getCurrentFitnessRecord().getTrackSegments();
        LatLngBounds.Builder boundsBuilder = new LatLngBounds.Builder();

        for (TrackSegment trackSegment : trackSegments) {
            List<LatLng> trackSegmentLatLngs = trackSegment.getLatLngs();
            for (LatLng location : trackSegmentLatLngs) {
                boundsBuilder.include(location);
            }
        }

        final LatLngBounds bounds = boundsBuilder.build();
        delegate.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 200));
    }

    // MARK: - Public methods
    public void onMapReady() {
        setupMap();
        moveCamera();
    }
}
