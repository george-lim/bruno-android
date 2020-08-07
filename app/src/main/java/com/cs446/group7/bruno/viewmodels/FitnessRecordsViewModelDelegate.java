package com.cs446.group7.bruno.viewmodels;

import com.cs446.group7.bruno.persistence.FitnessRecord;

import java.util.List;
import java.util.Locale;

public interface FitnessRecordsViewModelDelegate {
    void setupUI();
    void setAdapterData(final List<FitnessRecord> data);
    void setAdapterLocale(final Locale locale);
}
