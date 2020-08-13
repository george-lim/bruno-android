package com.cs446.group7.bruno.persistence;

import com.cs446.group7.bruno.MainActivity;
import com.cs446.group7.bruno.storage.PreferencesStorage;

import java.util.List;

public class DynamicFitnessRecordDAO implements FitnessRecordDAO {
    private FitnessRecordDAO fitnessRecordDAO;
    private FitnessRecordDAO mockFitnessRecordDAO;

    public DynamicFitnessRecordDAO(final FitnessRecordDAO fitnessRecordDAO,
                                   final FitnessRecordDAO mockFitnessRecordDAO) {
        this.fitnessRecordDAO = fitnessRecordDAO;
        this.mockFitnessRecordDAO = mockFitnessRecordDAO;
    }

    private FitnessRecordDAO getFitnessRecordDAO() {
        boolean isUsingMock = MainActivity.getPreferencesStorage().getBoolean(
                PreferencesStorage.KEYS.MOCK_FITNESS_RECORD_DAO,
                true
        );

        return isUsingMock ? mockFitnessRecordDAO : fitnessRecordDAO;
    }

    @Override
    public void insert(FitnessRecordEntry... records) {
        getFitnessRecordDAO().insert(records);
    }

    @Override
    public void deleteAll() {
        getFitnessRecordDAO().deleteAll();
    }

    @Override
    public List<FitnessRecordEntry> getRecords() {
        return getFitnessRecordDAO().getRecords();
    }
}
