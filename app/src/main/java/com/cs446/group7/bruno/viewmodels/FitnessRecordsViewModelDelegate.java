package com.cs446.group7.bruno.viewmodels;

import com.cs446.group7.bruno.dao.FitnessDetailsDAO;

import java.util.List;
import java.util.Locale;

public interface FitnessRecordsViewModelDelegate {
    void setupUI(final List<FitnessDetailsDAO> fitnessDetailsDAOList, final Locale locale);
}
