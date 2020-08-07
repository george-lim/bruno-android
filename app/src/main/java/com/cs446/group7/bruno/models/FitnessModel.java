package com.cs446.group7.bruno.models;

import androidx.lifecycle.ViewModel;

import com.cs446.group7.bruno.location.Coordinate;
import com.cs446.group7.bruno.music.BrunoPlaylist;
import com.cs446.group7.bruno.music.BrunoPlaylistImpl;
import com.cs446.group7.bruno.music.BrunoTrack;
import com.cs446.group7.bruno.persistence.FitnessRecordData;
import com.cs446.group7.bruno.routing.RouteSegment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class FitnessModel extends ViewModel {

    public enum Winner { YOU, BRUNO, TIE }

    private List<FitnessRecordData> fitnessRecordDataList = new ArrayList<>();
    private int selectedIndex; // Indicates which record is selected

    private final String TAG = getClass().getSimpleName();

    // Loads past fitness data from DB
    public void loadFitnessRecords() {
        fitnessRecordDataList.clear();

        // MOCK: - Dummy data
        final List<BrunoTrack> mockTracks = new ArrayList<BrunoTrack>() {{
            add(new BrunoTrack("testName", "testArtist", 280000));
            add(new BrunoTrack("testName 2", "testArtist", 140000));
            add(new BrunoTrack("testName 3", "testArtist", 300000));
        }};

        final BrunoPlaylist mockPlaylist = new BrunoPlaylistImpl("1", "mockList", mockTracks);

        final List<RouteSegment> mockSegments1 = new ArrayList<RouteSegment>() {{
            add(new RouteSegment(
                    new Coordinate(43.476861, -80.539940),
                    new Coordinate(43.478633, -80.535248),
                    60000L
            ));
            add(new RouteSegment(
                    new Coordinate(43.478633, -80.535248),
                    new Coordinate(43.473752, -80.531724),
                    80000L
            ));
        }};

        final List<RouteSegment> mockSegments2 = new ArrayList<RouteSegment>() {{
            add(new RouteSegment(
                    new Coordinate(43.473752, -80.531724),
                    new Coordinate(43.472029, -80.536262),
                    60000L
            ));
            add(new RouteSegment(
                    new Coordinate(43.472029, -80.536262),
                    new Coordinate(43.476861, -80.539940),
                    80000L
            ));
        }};

        final List<TrackSegment> mockTrackSegments = new ArrayList<TrackSegment>() {{
            add(new TrackSegment(mockSegments1, -537719));
            add(new TrackSegment(mockSegments2, -6234730));
        }};

        fitnessRecordDataList.add(new FitnessRecordData(
                FitnessRecordData.Mode.WALK,
                new Date(1596332280000L),
                70 * 60 * 1000,
                73 * 60 * 1000,
                3456,
                1478,
                mockPlaylist,
                mockTrackSegments
        ));

        fitnessRecordDataList.add(new FitnessRecordData(
                FitnessRecordData.Mode.RUN,
                new Date(1595794800000L),
                25 * 60 * 1000,
                21 * 60 * 1000,
                2470,
                690,
                mockPlaylist,
                mockTrackSegments
        ));

        fitnessRecordDataList.add(new FitnessRecordData(
                FitnessRecordData.Mode.RUN,
                new Date(1592237332000L),
                134 * 60 * 1000,
                140 * 60 * 1000,
                6789,
                1234,
                mockPlaylist,
                mockTrackSegments
        ));

        fitnessRecordDataList.add(new FitnessRecordData(
                FitnessRecordData.Mode.WALK,
                new Date(1584568760000L),
                59 * 60 * 1000,
                60 * 60 * 1000,
                1560,
                5433,
                mockPlaylist,
                mockTrackSegments
        ));

        fitnessRecordDataList.add(new FitnessRecordData(
                FitnessRecordData.Mode.RUN,
                new Date(1581674932000L),
                45 * 1000,
                45 * 1000,
                100,
                57,
                mockPlaylist,
                mockTrackSegments
        ));

        // Sort descending by the date the of the exercise
        // the Date class already implements Comparable so less work for us
        Collections.sort(fitnessRecordDataList, (record1, record2) -> record2.getStartTime()
                .compareTo(record1.getStartTime()));
    }

    public void setSelectedIndex(final int index) {
        this.selectedIndex = index;
    }

    public FitnessRecordData getCurrentFitnessRecord() {
        return fitnessRecordDataList.get(selectedIndex);
    }

    public List<FitnessRecordData> getFitnessRecords() {
        return fitnessRecordDataList;
    }
}
