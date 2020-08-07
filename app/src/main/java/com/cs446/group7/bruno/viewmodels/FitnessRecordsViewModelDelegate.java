package com.cs446.group7.bruno.viewmodels;

import com.cs446.group7.bruno.persistence.FitnessRecordData;

import java.util.List;
import java.util.Locale;

public interface FitnessRecordsViewModelDelegate {
    void setupUI();
    void setAdapterData(final List<FitnessRecordData> data);
    void setAdapterLocale(final Locale locale);
}
