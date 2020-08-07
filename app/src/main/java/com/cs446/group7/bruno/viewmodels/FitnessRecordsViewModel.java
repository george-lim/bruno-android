package com.cs446.group7.bruno.viewmodels;

import android.content.Context;
import android.os.Build;

import com.cs446.group7.bruno.models.FitnessModel;

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
        locale = getLocale(context);
        this.model = model;
        this.delegate = delegate;
        delegate.setupUI();
    }

    public void onResume() {
        updateAdapter();
    }

    // MARK: - Private methods

    @SuppressWarnings("deprecation")
    private Locale getLocale(final Context context) {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.N
                ? context.getResources().getConfiguration().getLocales().get(0)
                : context.getResources().getConfiguration().locale;
    }

    private void updateAdapter() {
        model.loadFitnessRecords();
        delegate.setAdapterData(model.getFitnessRecords());
        delegate.setAdapterLocale(locale);
    }
}
