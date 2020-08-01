package com.cs446.group7.bruno.models;

import com.cs446.group7.bruno.dao.FitnessDetailsDAO;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import androidx.lifecycle.ViewModel;

public class FitnessModel extends ViewModel {

    private List<FitnessDetailsDAO> fitnessDetailsDAOList;
    private int selectedIndex;

    public FitnessModel() {
        loadWalkRunSessions();
    }

    private void loadWalkRunSessions() {
        // TODO: Replace with persistence service query
        fitnessDetailsDAOList = new ArrayList<>(); // PersistenceService.get(...)

        // dummy data
        fitnessDetailsDAOList.add(new FitnessDetailsDAO(
                FitnessDetailsDAO.Mode.WALK,
                new Date(),
                17 * 60 * 1000,
                15 * 60 * 1000,
                2000,
                420,
                null,
                null
        ));

        fitnessDetailsDAOList.add(new FitnessDetailsDAO(
                FitnessDetailsDAO.Mode.RUN,
                new Date(1595790532000L),
                21 * 60 * 1000,
                25 * 60 * 1000,
                2470,
                690,
                null,
                null
        ));

        fitnessDetailsDAOList.add(new FitnessDetailsDAO(
                FitnessDetailsDAO.Mode.RUN,
                new Date(1592237332000L),
                123 * 60 * 1000,
                140 * 60 * 1000,
                6789,
                1234,
                null,
                null
        ));

        fitnessDetailsDAOList.add(new FitnessDetailsDAO(
                FitnessDetailsDAO.Mode.WALK,
                new Date(1584166132000L),
                60 * 60 * 1000,
                60 * 60 * 1000,
                1560,
                5433,
                null,
                null
        ));
    }

    public void setSelectedIndex(int index) {
        this.selectedIndex = index;
    }

    public int getSelectedIndex() {
        return selectedIndex;
    }

    public List<FitnessDetailsDAO> getFitnessRecords() {
        return fitnessDetailsDAOList == null ? new ArrayList<>() : fitnessDetailsDAOList;
    }
}
