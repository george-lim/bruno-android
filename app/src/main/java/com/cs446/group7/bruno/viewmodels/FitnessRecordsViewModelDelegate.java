package com.cs446.group7.bruno.viewmodels;

import com.cs446.group7.bruno.persistence.FitnessRecordData;

import java.util.List;
import java.util.Locale;

public interface FitnessRecordsViewModelDelegate {
    void setupUI(final List<FitnessRecordData> fitnessRecordDataList, final Locale locale);
}
