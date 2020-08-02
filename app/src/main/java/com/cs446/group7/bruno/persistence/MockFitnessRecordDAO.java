package com.cs446.group7.bruno.persistence;

import android.util.Log;

import com.cs446.group7.bruno.models.PlaylistModel;
import com.cs446.group7.bruno.music.BrunoPlaylist;
import com.cs446.group7.bruno.music.playlist.MockPlaylistGeneratorImpl;
import com.cs446.group7.bruno.music.playlist.PlaylistGenerator;
import com.cs446.group7.bruno.routing.MockRouteGeneratorImpl;
import com.cs446.group7.bruno.routing.OnRouteResponseCallback;
import com.cs446.group7.bruno.routing.RouteGenerator;
import com.cs446.group7.bruno.routing.RouteGeneratorException;
import com.cs446.group7.bruno.routing.RouteSegment;
import com.cs446.group7.bruno.utils.Callback;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class MockFitnessRecordDAO implements FitnessRecordDAO {

    private final String TAG = getClass().getSimpleName();
    private final int[] COLOURS =  { -537719, -6234730, -7879170,  -6188606, -1003060, -938359, -5719896, -5977857 };

    private List<RouteSegment> mockRouteSegments;
    private BrunoPlaylist mockPlaylist;
    private List<FitnessRecordEntry> recordEntries;

    private static FitnessRecordEntry makeEntry(final FitnessRecordData data) {
        FitnessRecordEntry entry = new FitnessRecordEntry();
        try {
            entry.setRecordDataString(data.serialize());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return entry;
    }

    public MockFitnessRecordDAO() {
        recordEntries = new ArrayList<>();
        RouteGenerator routeGenerator = new MockRouteGeneratorImpl(null, null);
        PlaylistGenerator playlistGenerator = new MockPlaylistGeneratorImpl();

        routeGenerator.generateRoute(
                new OnRouteResponseCallback() {
                    @Override
                    public void onRouteReady(List<RouteSegment> routeSegments) {
                        mockRouteSegments = routeSegments;
                        playlistGenerator.discoverPlaylist(new Callback<BrunoPlaylist, Exception>() {
                            @Override
                            public void onSuccess(BrunoPlaylist result) {
                                mockPlaylist = result;

                                PlaylistModel model = new PlaylistModel();
                                model.setRouteSegments(mockRouteSegments);
                                model.setRouteColours(COLOURS);
                                model.setPlaylist(mockPlaylist);

                                final FitnessRecordData data = new FitnessRecordData(
                                        FitnessRecordData.Mode.WALK,
                                        new Date(),
                                        17 * 60 * 1000,
                                        15 * 60 * 1000,
                                        2000,
                                        420,
                                        mockPlaylist.getTracks(),
                                        model.getTrackSegments()
                                );

                                // dummy data
                                // recordEntries.add(makeEntry(data));
                                // Testing deserialize here
                                try {
                                    recordEntries.add(makeEntry(FitnessRecordData.deserialize(data.serialize())));
                                } catch (IOException | ClassNotFoundException e) {
                                    Log.e(TAG, "Failed to load dummy record: " + e.toString());
                                }

                                recordEntries.add(makeEntry(new FitnessRecordData(
                                        FitnessRecordData.Mode.WALK,
                                        new Date(1596332280000L),
                                        70 * 60 * 1000,
                                        73 * 60 * 1000,
                                        3456,
                                        1478,
                                        mockPlaylist.getTracks(),
                                        model.getTrackSegments()
                                )));

                                recordEntries.add(makeEntry(new FitnessRecordData(
                                        FitnessRecordData.Mode.RUN,
                                        new Date(1595794800000L),
                                        21 * 60 * 1000,
                                        25 * 60 * 1000,
                                        2470,
                                        690,
                                        mockPlaylist.getTracks(),
                                        model.getTrackSegments()
                                )));

                                recordEntries.add(makeEntry(new FitnessRecordData(
                                        FitnessRecordData.Mode.RUN,
                                        new Date(1592237332000L),
                                        134 * 60 * 1000,
                                        140 * 60 * 1000,
                                        6789,
                                        1234,
                                        mockPlaylist.getTracks(),
                                        model.getTrackSegments()
                                )));

                                recordEntries.add(makeEntry(new FitnessRecordData(
                                        FitnessRecordData.Mode.WALK,
                                        new Date(1584568760000L),
                                        59 * 60 * 1000,
                                        60 * 60 * 1000,
                                        1560,
                                        5433,
                                        mockPlaylist.getTracks(),
                                        model.getTrackSegments()
                                )));

                                recordEntries.add(makeEntry(new FitnessRecordData(
                                        FitnessRecordData.Mode.RUN,
                                        new Date(1581674932000L),
                                        34 * 1000,
                                        45 * 1000,
                                        100,
                                        57,
                                        mockPlaylist.getTracks(),
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
    public void update(FitnessRecordEntry... records) {}

    @Override
    public void delete(FitnessRecordEntry... records) {
        recordEntries.removeAll(Arrays.asList(records));
    }

    @Override
    public List<FitnessRecordEntry> getRecords() {
        return recordEntries;
    }

    @Override
    public FitnessRecordEntry getRecordWithUID(int uid) {
        return null;
    }
}
