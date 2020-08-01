package com.cs446.group7.bruno.models;

import com.cs446.group7.bruno.colourizedroute.ColourizedRoute;
import com.cs446.group7.bruno.dao.FitnessDetailsDAO;
import com.cs446.group7.bruno.music.BrunoPlaylist;
import com.cs446.group7.bruno.music.playlist.MockPlaylistGeneratorImpl;
import com.cs446.group7.bruno.music.playlist.PlaylistGenerator;
import com.cs446.group7.bruno.routing.MockRouteGeneratorImpl;
import com.cs446.group7.bruno.routing.OnRouteResponseCallback;
import com.cs446.group7.bruno.routing.RouteGenerator;
import com.cs446.group7.bruno.routing.RouteGeneratorException;
import com.cs446.group7.bruno.routing.RouteSegment;
import com.cs446.group7.bruno.utils.Callback;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import androidx.lifecycle.ViewModel;

public class FitnessModel extends ViewModel {

    private List<FitnessDetailsDAO> fitnessDetailsDAOList;
    private int selectedIndex;

    // TODO: Remove all after when real data is given (this is Mock data)
    private List<RouteSegment> mockRouteSegments;
    private BrunoPlaylist mockPlaylist;

    public FitnessModel() {
        loadWalkRunSessions();
    }

    private void loadWalkRunSessions() {
        // TODO: Replace with persistence service query
        fitnessDetailsDAOList = new ArrayList<>(); // PersistenceService.get(...)

        // I would expect that at this point, the playlist and colorized route can be fetched from model
        RouteGenerator routeGenerator = new MockRouteGeneratorImpl(null, null);
        PlaylistGenerator playlistGenerator = new MockPlaylistGeneratorImpl();

        // TODO: get rid of this disgusting mock
        final int[] colors =  { -537719, -6234730, -7879170,  -6188606, -1003060, -938359, -5719896, -5977857 };

        routeGenerator.generateRoute(
                new OnRouteResponseCallback() {
                    @Override
                    public void onRouteReady(List<RouteSegment> routeSegments) {
                        mockRouteSegments = routeSegments;
                        playlistGenerator.getPlaylist(new Callback<BrunoPlaylist, Exception>() {
                            @Override
                            public void onSuccess(BrunoPlaylist result) {
                                mockPlaylist = result;

                                final ColourizedRoute mockColourizedRoute = new ColourizedRoute(mockRouteSegments, colors, mockPlaylist);

                                // dummy data
                                fitnessDetailsDAOList.add(new FitnessDetailsDAO(
                                        FitnessDetailsDAO.Mode.WALK,
                                        new Date(),
                                        17 * 60 * 1000,
                                        15 * 60 * 1000,
                                        2000,
                                        420,
                                        mockPlaylist.getTracks(),
                                        mockColourizedRoute
                                ));

                                fitnessDetailsDAOList.add(new FitnessDetailsDAO(
                                        FitnessDetailsDAO.Mode.RUN,
                                        new Date(1595790532000L),
                                        21 * 60 * 1000,
                                        25 * 60 * 1000,
                                        2470,
                                        690,
                                        mockPlaylist.getTracks(),
                                        mockColourizedRoute
                                ));

                                fitnessDetailsDAOList.add(new FitnessDetailsDAO(
                                        FitnessDetailsDAO.Mode.RUN,
                                        new Date(1592237332000L),
                                        123 * 60 * 1000,
                                        140 * 60 * 1000,
                                        6789,
                                        1234,
                                        mockPlaylist.getTracks(),
                                        mockColourizedRoute
                                ));

                                fitnessDetailsDAOList.add(new FitnessDetailsDAO(
                                        FitnessDetailsDAO.Mode.WALK,
                                        new Date(1584166132000L),
                                        60 * 60 * 1000,
                                        60 * 60 * 1000,
                                        1560,
                                        5433,
                                        mockPlaylist.getTracks(),
                                        mockColourizedRoute
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

    public FitnessDetailsDAO getCurrentFitnessRecord() {
        return fitnessDetailsDAOList.get(selectedIndex);
    }

    public List<FitnessDetailsDAO> getFitnessRecords() {
        return fitnessDetailsDAOList == null ? new ArrayList<>() : fitnessDetailsDAOList;
    }
}
