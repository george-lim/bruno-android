package com.cs446.group7.bruno.viewmodels;

import android.content.Context;
import android.content.res.Resources;

import com.cs446.group7.bruno.dao.FitnessDetailsDAO;
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
        final FitnessDetailsDAO currentFitnessRecord = model.getCurrentFitnessRecord();
        delegate.setupUI(
                TimeUtils.formatDateTime(currentFitnessRecord.getStartTime(), SettingsService.DATE_TIME_FORMAT, resources.getConfiguration().locale),
                (int)(currentFitnessRecord.getUserDuration() / 1000d),
                (int)(currentFitnessRecord.getExpectedDuration() / 1000d),
                currentFitnessRecord.getSteps()
        );

        delegate.setupTracklist(currentFitnessRecord.getTracks());
        delegate.drawRoute(currentFitnessRecord.getColourizedRoute());
    }
}
