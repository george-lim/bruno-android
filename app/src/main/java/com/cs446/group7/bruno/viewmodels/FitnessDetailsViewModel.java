package com.cs446.group7.bruno.viewmodels;

import android.content.Context;
import android.content.res.Resources;

import com.cs446.group7.bruno.R;
import com.cs446.group7.bruno.colourizedroute.ColourizedRoute;
import com.cs446.group7.bruno.models.FitnessModel;
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

import java.util.List;

public class FitnessDetailsViewModel {
    // MARK: - Private methods

    private Resources resources;
    private FitnessModel model;
    private FitnessDetailsViewModelDelegate delegate;

    // Mock data
    private List<RouteSegment> mockRouteSegments;
    private BrunoPlaylist mockPlaylist;

    // MARK: - Lifecycle methods

    public FitnessDetailsViewModel(final Context context,
                            final FitnessModel model,
                            final FitnessDetailsViewModelDelegate delegate) {
        this.resources = context.getResources();
        this.model = model;
        this.delegate = delegate;


        // I would expect that at this point, the playlist and colorized route can be fetched from model
        String googleMapsKey = resources.getString(R.string.google_maps_key);
        RouteGenerator routeGenerator = new MockRouteGeneratorImpl(context, googleMapsKey);
        PlaylistGenerator playlistGenerator = new MockPlaylistGeneratorImpl();

        routeGenerator.generateRoute(
                new OnRouteResponseCallback() {
                    @Override
                    public void onRouteReady(List<RouteSegment> routeSegments) {
                        mockRouteSegments = routeSegments;
                        if (mockPlaylist != null) {
                            setupUI();
                        }
                    }

                    @Override
                    public void onRouteError(RouteGeneratorException exception) {}
                },
                new LatLng(43.464951, -80.523911),
                2400,
                0);
        playlistGenerator.getPlaylist(new Callback<BrunoPlaylist, Exception>() {
            @Override
            public void onSuccess(BrunoPlaylist result) {
                mockPlaylist = result;
                if (mockRouteSegments != null) {
                    setupUI();
                }
            }

            @Override
            public void onFailed(Exception result) {}
        });
    }

    private void setupUI() {
        // TODO: Change these testing values with actual data implementations
        delegate.setupUI( 4000, 3900, 500);
        delegate.setupTracklist(mockPlaylist.getTracks());
        int[] colors = resources.getIntArray(R.array.colorRouteList);
        ColourizedRoute mockColourizedRoute = new ColourizedRoute(mockRouteSegments, colors, mockPlaylist);
        delegate.drawRoute(mockColourizedRoute);
    }
}
