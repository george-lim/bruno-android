package com.cs446.group7.bruno.viewmodels;

import android.content.Context;
import android.content.res.Resources;

import com.cs446.group7.bruno.models.FitnessModel;

public class FitnessRecordsViewModel {
    private FitnessModel model;

    private Resources resources;
    private FitnessRecordsViewModelDelegate delegate;

    public FitnessRecordsViewModel(final Context context, final FitnessModel model, final FitnessRecordsViewModelDelegate delegate) {
        this.resources = context.getResources();
        this.model = model;
        this.delegate = delegate;
        setupUI();
    }

    private void setupUI() {
        delegate.setupUI(model.getFitnessRecords(), resources.getConfiguration().locale);
    }
}
