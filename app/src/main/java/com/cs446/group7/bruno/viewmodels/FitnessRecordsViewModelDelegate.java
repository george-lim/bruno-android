package com.cs446.group7.bruno.viewmodels;

import com.cs446.group7.bruno.dao.FitnessSessionData;

import java.util.List;
import java.util.Locale;

public interface FitnessRecordsViewModelDelegate {
    void setupUI(final List<FitnessSessionData> fitnessSessionDataList, final Locale locale);
}
