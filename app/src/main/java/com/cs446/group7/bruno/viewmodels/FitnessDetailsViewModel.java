package com.cs446.group7.bruno.viewmodels;

import android.content.Context;
import android.content.res.Resources;

import com.cs446.group7.bruno.persistence.FitnessRecordData;
import com.cs446.group7.bruno.models.FitnessModel;
import com.cs446.group7.bruno.settings.SettingsService;
import com.cs446.group7.bruno.utils.TimeUtils;

public class FitnessDetailsViewModel {

    private Resources resources;
    private FitnessModel model;
    private FitnessDetailsViewModelDelegate delegate;

    public FitnessDetailsViewModel(final Context context,
                            final FitnessModel model,
                            final FitnessDetailsViewModelDelegate delegate) {
        this.resources = context.getResources();
        this.model = model;
        this.delegate = delegate;

        setupUI();
    }

    private void setupUI() {
        final FitnessRecordData currentFitnessRecord = model.getCurrentFitnessRecord();
        final String dateTimeString = TimeUtils.formatDateTime(currentFitnessRecord.getStartTime(), SettingsService.DATE_TIME_FORMAT, resources.getConfiguration().locale);

        delegate.setupUI(
               dateTimeString,
                (int)(currentFitnessRecord.getUserDuration() / 1000d),
                (int)(currentFitnessRecord.getExpectedDuration() / 1000d),
                currentFitnessRecord.getSteps()
        );

        delegate.setupTracklist(currentFitnessRecord.getTracksUserPlayed());
        delegate.drawRoute(currentFitnessRecord.getTrackSegments());
    }
}
