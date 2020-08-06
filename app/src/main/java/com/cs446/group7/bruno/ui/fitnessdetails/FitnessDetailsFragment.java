package com.cs446.group7.bruno.ui.fitnessdetails;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.cs446.group7.bruno.R;
import com.cs446.group7.bruno.location.Coordinate;
import com.cs446.group7.bruno.models.TrackSegment;
import com.cs446.group7.bruno.music.BrunoTrack;
import com.cs446.group7.bruno.routing.RouteSegment;
import com.cs446.group7.bruno.ui.AppbarFormatter;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.List;

public class FitnessDetailsFragment extends Fragment {

    // MARK: - Enums

    private enum Winner { YOU, BRUNO, TIE }

    // MARK: - UI components

    private GoogleMap map;
    private ImageView imgLeaderboardYouCrown;
    private ImageView imgLeaderboardBrunoCrown;
    private TextView txtLeaderboardYouTime;
    private TextView txtLeaderboardBrunoTime;
    private TextView txtStatsDistance;
    private TextView txtStatsSteps;
    private TextView txtStatsClock;
    private LinearLayout runTracklist;

    // MARK: - Lifecycle Methods

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_fitness_details, container, false);
        imgLeaderboardYouCrown = view.findViewById(R.id.image_view_leaderboard_you_crown);
        imgLeaderboardBrunoCrown = view.findViewById(R.id.image_view_leaderboard_bruno_crown);
        txtLeaderboardYouTime = view.findViewById(R.id.text_view_leaderboard_you_time);
        txtLeaderboardBrunoTime = view.findViewById(R.id.text_view_leaderboard_bruno_time);
        txtStatsDistance = view.findViewById(R.id.text_view_stats_distance);
        txtStatsSteps = view.findViewById(R.id.text_view_stats_steps);
        txtStatsClock = view.findViewById(R.id.text_view_stats_clock);
        runTracklist = view.findViewById(R.id.linear_layout_fitness_detail_tracklist);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        SupportMapFragment mapFragment = (SupportMapFragment)getChildFragmentManager()
                .findFragmentById(R.id.fitness_details_map);

        mapFragment.getMapAsync(googleMap -> {
            map = googleMap;
            mockViewModelResponsibilities();
        });
    }

    // MARK: - Private methods

    // MOCK - Populate fitness details with dummy data.
    private void mockViewModelResponsibilities() {
        setupUI("17:00",
                "15:00",
                "2.0 km",
                "420 steps",
                "17 min",
                "Aug 6 • 3:45 PM",
                Winner.BRUNO);

        List<BrunoTrack> tracks = new ArrayList<>();
        tracks.add(new BrunoTrack("testName", "testArtist", 280000));
        tracks.add(new BrunoTrack("testName 2", "testArtist", 280000));
        tracks.add(new BrunoTrack("testName 3", "testArtist", 280000));
        setupTracklist(tracks);

        RouteSegment mockSegment1 = new RouteSegment(
                new Coordinate(43.476861, -80.539940),
                new Coordinate(43.478633, -80.535248),
                60000L
        );
        RouteSegment mockSegment2 = new RouteSegment(
                new Coordinate(43.478633, -80.535248),
                new Coordinate(43.473752, -80.531724),
                80000L
        );
        RouteSegment mockSegment3 = new RouteSegment(
                new Coordinate(43.473752, -80.531724),
                new Coordinate(43.472029, -80.536262),
                60000L
        );
        RouteSegment mockSegment4 = new RouteSegment(
                new Coordinate(43.472029, -80.536262),
                new Coordinate(43.476861, -80.539940),
                80000L
        );

        List<RouteSegment> mockSegments = new ArrayList<>();
        mockSegments.add(mockSegment1);
        mockSegments.add(mockSegment2);
        mockSegments.add(mockSegment3);
        mockSegments.add(mockSegment4);

        List<TrackSegment> trackSegments = new ArrayList<>();
        trackSegments.add(new TrackSegment(mockSegments, -537719));
        drawRoute(trackSegments);
    }

    private void setupUI(final String leaderboardYourTime,
                         final String leaderboardBrunoTime,
                         final String statsDistance,
                         final String statsSteps,
                         final String statsClock,
                         final String appBarHeader,
                         final Winner winner) {
        txtLeaderboardYouTime.setText(leaderboardYourTime);
        txtLeaderboardBrunoTime.setText(leaderboardBrunoTime);
        txtStatsDistance.setText(statsDistance);
        txtStatsSteps.setText(statsSteps);
        txtStatsClock.setText(statsClock);

        AppbarFormatter.format((AppCompatActivity) getActivity(),
                getView(),
                R.id.appbar_fitness_details,
                appBarHeader,
                true);

        switch (winner) {
            case YOU:
                imgLeaderboardYouCrown.getDrawable().setTint(getResources().getColor(R.color.colorCrown, null));
                imgLeaderboardBrunoCrown.setVisibility(View.INVISIBLE);
                break;
            case BRUNO:
                imgLeaderboardYouCrown.setVisibility(View.INVISIBLE);
                imgLeaderboardBrunoCrown.getDrawable().setTint(getResources().getColor(R.color.colorCrown, null));
                break;
            case TIE:
                imgLeaderboardYouCrown.getDrawable().setTint(getResources().getColor(R.color.colorCrown, null));
                imgLeaderboardBrunoCrown.getDrawable().setTint(getResources().getColor(R.color.colorCrown, null));
                break;
        }
    }

    private void setupTracklist(final List<BrunoTrack> tracks) {
        int[] colors = getResources().getIntArray(R.array.colorRouteList);
        for (int i = 0; i < tracks.size(); i++) {
            BrunoTrack track = tracks.get(i);
            View vi = getLayoutInflater().inflate(R.layout.view_holder_fitness_details, null);
            ImageView musicNote = vi.findViewById(R.id.image_view_fitness_details_holder_music);
            musicNote.setColorFilter(colors[i % colors.length]);
            TextView songName = vi.findViewById(R.id.text_view_fitness_details_holder_song);
            songName.setText(track.getName());

            TextView artist = vi.findViewById(R.id.text_view_fitness_details_holder_artist);
            artist.setText(track.getArtists());
            runTracklist.addView(vi);
        }
    }

    private void drawRoute(final List<TrackSegment> trackSegments) {
        final float routeWidth = 14;
        LatLngBounds.Builder boundsBuilder = new LatLngBounds.Builder();

        for (TrackSegment trackSegment : trackSegments) {
            List<LatLng> trackSegmentLocations = trackSegment.getLatLngs();
            map.addPolyline(new PolylineOptions()
                    .addAll(trackSegmentLocations)
                    .color(trackSegment.getRouteColour())
                    .width(routeWidth));

            for (final LatLng location : trackSegmentLocations) {
                boundsBuilder.include(location);
            }
        }

        final LatLngBounds bounds = boundsBuilder.build();
        map.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 200));
        // disallow movement
        map.getUiSettings().setAllGesturesEnabled(false);
    }
}
