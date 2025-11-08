package com.bruno.android.persistence;

import com.bruno.android.MainActivity;
import com.bruno.android.storage.PreferencesStorage;

import java.util.List;

public class DynamicFitnessRecordDAO implements FitnessRecordDAO {
    private final FitnessRecordDAO fitnessRecordDAO;
    private final FitnessRecordDAO mockFitnessRecordDAO;

    public DynamicFitnessRecordDAO(final FitnessRecordDAO fitnessRecordDAO,
                                   final FitnessRecordDAO mockFitnessRecordDAO) {
        this.fitnessRecordDAO = fitnessRecordDAO;
        this.mockFitnessRecordDAO = mockFitnessRecordDAO;
    }

    private FitnessRecordDAO getFitnessRecordDAO() {
        boolean isUsingMock = MainActivity.getPreferencesStorage().getBoolean(
                PreferencesStorage.KEYS.MOCK_FITNESS_RECORD_DAO,
                false
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
