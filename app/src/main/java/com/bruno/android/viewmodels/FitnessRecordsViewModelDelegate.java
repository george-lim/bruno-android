package com.bruno.android.viewmodels;

import com.bruno.android.persistence.FitnessRecord;

import java.util.List;
import java.util.Locale;

public interface FitnessRecordsViewModelDelegate {
    void setupUI();
    void setAdapterData(final List<FitnessRecord> data);
    void setAdapterLocale(final Locale locale);
}
