package com.cs446.group7.bruno.viewmodels;

import android.content.Context;

import com.cs446.group7.bruno.models.FitnessModel;
import com.cs446.group7.bruno.utils.DateTimeUtils;

import java.util.Locale;

public class FitnessRecordsViewModel {

    // MARK: - Private members

    private Locale locale;
    private FitnessModel model;
    private FitnessRecordsViewModelDelegate delegate;

    // MARK: - Lifecycle methods

    public FitnessRecordsViewModel(final Context context,
                                   final FitnessModel model,
                                   final FitnessRecordsViewModelDelegate delegate) {
        locale = DateTimeUtils.getLocale(context.getResources());
        this.model = model;
        this.delegate = delegate;
        delegate.setupUI();
    }

    public void onResume() {
        updateAdapter();
    }

    // MARK: - Private methods

    private void updateAdapter() {
        model.loadFitnessRecords();
        delegate.setAdapterData(model.getFitnessRecords());
        delegate.setAdapterLocale(locale);
    }
}
