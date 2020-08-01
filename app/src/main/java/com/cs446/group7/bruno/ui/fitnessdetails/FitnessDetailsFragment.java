package com.cs446.group7.bruno.ui.fitnessdetails;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cs446.group7.bruno.R;
import com.cs446.group7.bruno.colourizedroute.ColourizedRoute;
import com.cs446.group7.bruno.colourizedroute.ColourizedRouteSegment;
import com.cs446.group7.bruno.models.FitnessModel;
import com.cs446.group7.bruno.music.BrunoTrack;
import com.cs446.group7.bruno.ui.AppbarFormatter;
import com.cs446.group7.bruno.utils.TimeUtils;
import com.cs446.group7.bruno.viewmodels.FitnessDetailsViewModel;
import com.cs446.group7.bruno.viewmodels.FitnessDetailsViewModelDelegate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

public class FitnessDetailsFragment extends Fragment implements FitnessDetailsViewModelDelegate {

    // MARK: - UI components

    private GoogleMap map;
    private ImageView imgLeaderboardYouCrown;
    private ImageView imgLeaderboardBrunoCrown;
    private TextView txtLeaderboardYouTime;
    private TextView txtLeaderboardBrunoTime;
    private ImageView imgStatsSteps;
    private ImageView imgStatsClock;
    private TextView txtStatsSteps;
    private TextView txtStatsClock;
    private LinearLayout runTracklist;

    // MARK: - Private members

    private FitnessDetailsViewModel viewModel;

    // MARK: - Lifecycle Methods

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_fitness_details, container, false);

        imgLeaderboardYouCrown = view.findViewById(R.id.image_view_leaderboard_you_crown);
        imgLeaderboardBrunoCrown = view.findViewById(R.id.image_view_leaderboard_bruno_crown);
        txtLeaderboardYouTime = view.findViewById(R.id.text_view_leaderboard_you_time);
        txtLeaderboardBrunoTime = view.findViewById(R.id.text_view_leaderboard_bruno_time);
        imgStatsSteps = view.findViewById(R.id.image_view_stats_steps);
        imgStatsClock = view.findViewById(R.id.image_view_stats_clock);
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

            FitnessModel model = new ViewModelProvider(requireActivity()).get(FitnessModel.class);
            model.setSelectedIndex(getArguments().getInt("recordIndex"));
            viewModel = new FitnessDetailsViewModel(getActivity().getApplicationContext(), model, this);
        });
    }

    // MARK: - FitnessDetailsViewModelDelegate methods

    @Override
    public void setupUI(final String dateTimeString, long yourRunDuration, long brunoDuration, int stepCount) {
        txtLeaderboardYouTime.setText(TimeUtils.getDurationString(yourRunDuration));
        txtLeaderboardBrunoTime.setText(TimeUtils.getDurationString(brunoDuration));
        txtStatsSteps.setText(String.format(getResources().getString(R.string.fitness_details_steps_placeholder), stepCount));
        txtStatsClock.setText(TimeUtils.formatDuration(yourRunDuration));

        AppbarFormatter.format((AppCompatActivity) getActivity(),
                getView(),
                R.id.appbar_fitness_details,
                dateTimeString,
                true);

        // Crown
        if (yourRunDuration < brunoDuration) {
            // You win
            imgLeaderboardYouCrown.setColorFilter(getResources().getColor(R.color.colorCrown, null));
            imgLeaderboardBrunoCrown.setVisibility(View.INVISIBLE);
        } else if (yourRunDuration > brunoDuration) {
            // You lose
            imgLeaderboardYouCrown.setVisibility(View.INVISIBLE);
            imgLeaderboardBrunoCrown.setColorFilter(getResources().getColor(R.color.colorCrown, null));
        } else { // Draw: rare, but possible case -> give both player crowns
            imgLeaderboardBrunoCrown.setColorFilter(getResources().getColor(R.color.colorCrown, null));
            imgLeaderboardYouCrown.setColorFilter(getResources().getColor(R.color.colorCrown, null));
        }
    }

    // MARK: - Private methods

    @Override
    public void setupTracklist(final List<BrunoTrack> trackList) {
        int[] colors = getResources().getIntArray(R.array.colorRouteList);
        for (int i = 0; i < trackList.size(); i++) {
            BrunoTrack track = trackList.get(i);
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

    @Override
    public void drawRoute(final ColourizedRoute colourizedRoute) {
        final float routeWidth = 14;
        LatLngBounds.Builder boundsBuilder = new LatLngBounds.Builder();

        for (ColourizedRouteSegment colourizedRouteSegment : colourizedRoute.getSegments()) {
            List<LatLng> colourizedRouteSegmentLocations = colourizedRouteSegment.getLocations();
            map.addPolyline(new PolylineOptions()
                    .addAll(colourizedRouteSegmentLocations)
                    .color(colourizedRouteSegment.getRouteColour())
                    .width(routeWidth));

            for (LatLng location : colourizedRouteSegmentLocations) {
                boundsBuilder.include(location);
            }
        }

        LatLngBounds bounds = boundsBuilder.build();
        map.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 200));
        // disallow movement
        map.getUiSettings().setAllGesturesEnabled(false);
    }
}
