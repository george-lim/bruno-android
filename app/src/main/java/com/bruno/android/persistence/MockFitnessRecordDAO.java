package com.bruno.android.persistence;

import android.util.Log;

import com.bruno.android.models.PlaylistModel;
import com.bruno.android.models.RouteModel;
import com.bruno.android.music.BrunoPlaylist;
import com.bruno.android.music.playlist.MockPlaylistGeneratorImpl;
import com.bruno.android.music.playlist.PlaylistGenerator;
import com.bruno.android.routing.MockRouteGeneratorImpl;
import com.bruno.android.routing.OnRouteResponseCallback;
import com.bruno.android.routing.RouteGenerator;
import com.bruno.android.routing.RouteGeneratorException;
import com.bruno.android.routing.RouteSegment;
import com.bruno.android.utils.Callback;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Mock database that persists records in memory rather than the actual SQLite DB.
 * It contains some mock data that always exists.
 */
public class MockFitnessRecordDAO implements FitnessRecordDAO {

    private final String TAG = getClass().getSimpleName();
    private final int[] COLOURS = {-537719, -6234730, -7879170, -6188606, -1003060, -938359, -5719896, -5977857};

    private final List<FitnessRecordEntry> recordEntries;

    private FitnessRecordEntry makeEntry(final FitnessRecord data) {
        FitnessRecordEntry entry = new FitnessRecordEntry();
        try {
            entry.setRecordDataString(data.serialize());
        } catch (IOException e) {
            Log.e(TAG, "Failed to serialize fitness record", e);
        }
        return entry;
    }

    public MockFitnessRecordDAO() {
        recordEntries = new ArrayList<>();
        RouteGenerator routeGenerator = new MockRouteGeneratorImpl();
        PlaylistGenerator playlistGenerator = new MockPlaylistGeneratorImpl();

        routeGenerator.generateRoute(
                new OnRouteResponseCallback() {
                    @Override
                    public void onRouteReady(List<RouteSegment> routeSegments) {
                        playlistGenerator.discoverPlaylist(new Callback<>() {
                            @Override
                            public void onSuccess(BrunoPlaylist result) {

                                PlaylistModel model = new PlaylistModel();
                                model.setRouteSegments(routeSegments);
                                model.setRouteColours(COLOURS);
                                model.setPlaylist(result);

                                final FitnessRecord data = new FitnessRecord(
                                        RouteModel.Mode.WALK,
                                        new Date(),
                                        17 * 60 * 1000,
                                        15 * 60 * 1000,
                                        2000,
                                        420,
                                        result,
                                        model.getTrackSegments()
                                );

                                // dummy data
                                // Testing deserialize here
                                try {
                                    recordEntries.add(makeEntry(FitnessRecord.deserialize(data.serialize())));
                                } catch (IOException | ClassNotFoundException e) {
                                    Log.e(TAG, "Failed to load dummy record: " + e);
                                }

                                recordEntries.add(makeEntry(new FitnessRecord(
                                        RouteModel.Mode.WALK,
                                        new Date(1596332280000L),
                                        70 * 60 * 1000,
                                        73 * 60 * 1000,
                                        3456,
                                        1478,
                                        result,
                                        model.getTrackSegments()
                                )));

                                recordEntries.add(makeEntry(new FitnessRecord(
                                        RouteModel.Mode.RUN,
                                        new Date(1595794800000L),
                                        21 * 60 * 1000,
                                        25 * 60 * 1000,
                                        2470,
                                        690,
                                        result,
                                        model.getTrackSegments()
                                )));

                                recordEntries.add(makeEntry(new FitnessRecord(
                                        RouteModel.Mode.RUN,
                                        new Date(1592237332000L),
                                        134 * 60 * 1000,
                                        140 * 60 * 1000,
                                        6789,
                                        1234,
                                        result,
                                        model.getTrackSegments()
                                )));

                                recordEntries.add(makeEntry(new FitnessRecord(
                                        RouteModel.Mode.WALK,
                                        new Date(1584568760000L),
                                        59 * 60 * 1000,
                                        60 * 60 * 1000,
                                        1560,
                                        5433,
                                        result,
                                        model.getTrackSegments()
                                )));

                                recordEntries.add(makeEntry(new FitnessRecord(
                                        RouteModel.Mode.RUN,
                                        new Date(1581674932000L),
                                        34 * 1000,
                                        45 * 1000,
                                        100,
                                        57,
                                        result,
                                        model.getTrackSegments()
                                )));
                            }

                            @Override
                            public void onFailed(Exception result) {
                                Log.e(TAG, result.toString());
                            }
                        });

                    }

                    @Override
                    public void onRouteError(RouteGeneratorException exception) {
                        Log.e(TAG, exception.toString());
                    }
                },
                null,
                0,
                0
        );
    }

    @Override
    public void insert(FitnessRecordEntry... records) {
        recordEntries.addAll(Arrays.asList(records));
    }

    @Override
    public void deleteAll() {
        recordEntries.clear();
    }

    @Override
    public List<FitnessRecordEntry> getRecords() {
        return recordEntries;
    }
}
