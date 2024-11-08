package com.bruno.android.viewmodels;

import android.content.Context;
import android.content.res.Resources;

import com.bruno.android.R;
import com.bruno.android.models.FitnessModel;
import com.bruno.android.models.TrackSegment;
import com.bruno.android.persistence.FitnessRecord;
import com.bruno.android.utils.DateTimeUtils;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import java.util.List;
import java.util.Locale;

public class FitnessDetailsViewModel {

    // MARK: - Enums

    public enum Winner { USER, BRUNO, TIE }

    // MARK: - Private members

    private FitnessRecord fitnessRecord;
    private FitnessDetailsViewModelDelegate delegate;

    // MARK: - Lifecycle methods

    public FitnessDetailsViewModel(final Context context,
                                   final FitnessModel model,
                                   final FitnessDetailsViewModelDelegate delegate,
                                   int fitnessRecordIndex) {
        Resources resources = context.getResources();
        this.fitnessRecord = model.getFitnessRecord(fitnessRecordIndex);
        this.delegate = delegate;
        setupUI(resources);
    }

    public void onMapReady() {
        List<TrackSegment> trackSegments = fitnessRecord.getTrackSegments();
        drawRoute(trackSegments);
        moveCamera(trackSegments);
    }

    // MARK: - Private methods

    private void setupUI(final Resources resources) {
        Locale locale = DateTimeUtils.getLocale(resources);
        long userDuration = fitnessRecord.getUserDuration() / 1000;
        long brunoDuration = fitnessRecord.getExpectedDuration() / 1000;
        int stepCount = fitnessRecord.getSteps();

        String leaderboardUserTimeText = DateTimeUtils.getDurationString(userDuration);
        String leaderboardBrunoTimeText = DateTimeUtils.getDurationString(brunoDuration);
        String statsDistanceText = String.format(
                locale,
                "%.1f km",
                fitnessRecord.getRouteDistance() / 1000
        );
        String statsStepsText = String.format(
                resources.getString(R.string.fitness_details_steps_placeholder),
                stepCount
        );
        String statsClockText = DateTimeUtils.formatDuration(userDuration);
        String appBarTitle = DateTimeUtils.formatDateTime(
                fitnessRecord.getStartTime(),
                locale
        );
        Winner winner;

        if (userDuration < brunoDuration) {
            winner = Winner.USER;
        } else if (userDuration > brunoDuration) {
            winner = Winner.BRUNO;
        } else {
            winner = Winner.TIE;
        }

        delegate.setupUI(
                leaderboardUserTimeText,
                leaderboardBrunoTimeText,
                statsDistanceText,
                statsStepsText,
                statsClockText,
                appBarTitle,
                winner,
                fitnessRecord.getPlaylistTracks()
        );
    }

    private void drawRoute(final List<TrackSegment> trackSegments) {
        float routeWidth = 14;
        delegate.drawRoute(trackSegments, routeWidth);
    }

    private void moveCamera(final List<TrackSegment> trackSegments) {
        LatLngBounds.Builder boundsBuilder = new LatLngBounds.Builder();

        for (TrackSegment trackSegment : trackSegments) {
            List<LatLng> trackSegmentLatLngs = trackSegment.getLatLngs();
            for (LatLng latLng : trackSegmentLatLngs) {
                boundsBuilder.include(latLng);
            }
        }

        final LatLngBounds bounds = boundsBuilder.build();
        delegate.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 200));
    }
}
