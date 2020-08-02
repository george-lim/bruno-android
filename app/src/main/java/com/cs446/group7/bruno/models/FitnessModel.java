package com.cs446.group7.bruno.models;

import android.util.Log;

import com.cs446.group7.bruno.MainActivity;
import com.cs446.group7.bruno.dao.FitnessRecordData;
import com.cs446.group7.bruno.music.BrunoPlaylist;
import com.cs446.group7.bruno.music.playlist.MockPlaylistGeneratorImpl;
import com.cs446.group7.bruno.music.playlist.PlaylistGenerator;
import com.cs446.group7.bruno.persistence.FitnessRecordDAO;
import com.cs446.group7.bruno.persistence.FitnessRecordEntry;
import com.cs446.group7.bruno.routing.MockRouteGeneratorImpl;
import com.cs446.group7.bruno.routing.OnRouteResponseCallback;
import com.cs446.group7.bruno.routing.RouteGenerator;
import com.cs446.group7.bruno.routing.RouteGeneratorException;
import com.cs446.group7.bruno.routing.RouteSegment;
import com.cs446.group7.bruno.utils.Callback;
import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import androidx.lifecycle.ViewModel;

public class FitnessModel extends ViewModel {

    private List<FitnessRecordData> fitnessRecordDataList;
    private int selectedIndex;

    // TODO: Remove all after when real data is given (this is Mock data)
    private List<RouteSegment> mockRouteSegments;
    private BrunoPlaylist mockPlaylist;

    public FitnessModel() {
        loadFitnessRecords();
    }

    private void loadFitnessRecords() {
        fitnessRecordDataList = new ArrayList<>();

        final FitnessRecordDAO fitnessRecordDAO = MainActivity.getPersistenceService().getFitnessRecordDAO();
        final List<FitnessRecordEntry> entries = fitnessRecordDAO.getRecords();

        for (final FitnessRecordEntry entry : entries) {
            try {
                fitnessRecordDataList.add(FitnessRecordData.deserialize(entry.getRecordDataString()));
            } catch (IOException | ClassNotFoundException e) {
                Log.e(getClass().getSimpleName(), "Failed to load record: " + e.toString());
            }
        }

        // TODO: get rid of this disgusting mock
        // I would expect that at this point, the playlist and colorized route can be fetched from model
        RouteGenerator routeGenerator = new MockRouteGeneratorImpl(null, null);
        PlaylistGenerator playlistGenerator = new MockPlaylistGeneratorImpl();

        final int[] colors =  { -537719, -6234730, -7879170,  -6188606, -1003060, -938359, -5719896, -5977857 };

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
                                model.setRouteColours(colors);
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
                                fitnessRecordDataList.add(data);

                                try {
                                    fitnessRecordDataList.add(
                                            FitnessRecordData.deserialize(
                                                    data.serialize()
                                            )
                                    );
                                } catch (IOException | ClassNotFoundException ignored) {}

                                fitnessRecordDataList.add(new FitnessRecordData(
                                        FitnessRecordData.Mode.RUN,
                                        new Date(1595790532000L),
                                        21 * 60 * 1000,
                                        25 * 60 * 1000,
                                        2470,
                                        690,
                                        mockPlaylist.getTracks(),
                                        model.getTrackSegments()
                                ));

                                fitnessRecordDataList.add(new FitnessRecordData(
                                        FitnessRecordData.Mode.RUN,
                                        new Date(1592237332000L),
                                        123 * 60 * 1000,
                                        140 * 60 * 1000,
                                        6789,
                                        1234,
                                        mockPlaylist.getTracks(),
                                        model.getTrackSegments()
                                ));

                                fitnessRecordDataList.add(new FitnessRecordData(
                                        FitnessRecordData.Mode.WALK,
                                        new Date(1584166132000L),
                                        60 * 60 * 1000,
                                        60 * 60 * 1000,
                                        1560,
                                        5433,
                                        mockPlaylist.getTracks(),
                                        model.getTrackSegments()
                                ));

                                fitnessRecordDataList.add(new FitnessRecordData(
                                        FitnessRecordData.Mode.RUN,
                                        new Date(1581674932000L),
                                        34 * 1000,
                                        45 * 1000,
                                        100,
                                        57,
                                        mockPlaylist.getTracks(),
                                        model.getTrackSegments()
                                ));
                            }

                            @Override
                            public void onFailed(Exception result) {}
                        });

                    }

                    @Override
                    public void onRouteError(RouteGeneratorException exception) {}
                },
                new LatLng(43.464951, -80.523911),
                2400,
                0
        );
    }

    public void setSelectedIndex(int index) {
        this.selectedIndex = index;
    }

    public FitnessRecordData getCurrentFitnessRecord() {
        return fitnessRecordDataList.get(selectedIndex);
    }

    public List<FitnessRecordData> getFitnessRecords() {
        return fitnessRecordDataList == null ? new ArrayList<>() : fitnessRecordDataList;
    }
}
