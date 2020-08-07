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
import androidx.lifecycle.ViewModelProvider;

import com.cs446.group7.bruno.R;
import com.cs446.group7.bruno.models.FitnessModel;
import com.cs446.group7.bruno.models.TrackSegment;
import com.cs446.group7.bruno.music.BrunoTrack;
import com.cs446.group7.bruno.ui.AppbarFormatter;
import com.cs446.group7.bruno.viewmodels.FitnessDetailsViewModel;
import com.cs446.group7.bruno.viewmodels.FitnessDetailsViewModelDelegate;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.List;

public class FitnessDetailsFragment extends Fragment implements FitnessDetailsViewModelDelegate {

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

    // MARK: - Private members

    private FitnessDetailsViewModel viewModel;

    // MARK: - Lifecycle methods

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


        FitnessModel model = new ViewModelProvider(requireActivity()).get(FitnessModel.class);
        model.setSelectedIndex(getArguments().getInt("recordIndex"));
        viewModel = new FitnessDetailsViewModel(
                getActivity().getApplicationContext(),
                model,
                this
        );

        mapFragment.getMapAsync(googleMap -> {
            map = googleMap;
            map.getUiSettings().setAllGesturesEnabled(false);
            viewModel.onMapReady();
        });

    }

    // MARK: - FitnessDetailsViewModelDelegate methods

    @Override
    public void setupUI(final String leaderboardYouTimeText,
                        final String leaderboardBrunoTimeText,
                        final String statsDistanceText,
                        final String statsStepsText,
                        final String statsClockText,
                        final String appBarTitle,
                        final FitnessModel.Winner winner) {
        txtLeaderboardYouTime.setText(leaderboardYouTimeText);
        txtLeaderboardBrunoTime.setText(leaderboardBrunoTimeText);
        txtStatsDistance.setText(statsDistanceText);
        txtStatsSteps.setText(statsStepsText);
        txtStatsClock.setText(statsClockText);

        AppbarFormatter.format((AppCompatActivity) getActivity(),
                getView(),
                R.id.appbar_fitness_details,
                appBarTitle,
                true);

        int colorCrown = getResources().getColor(R.color.colorCrown, null);

        switch (winner) {
            case YOU:
                imgLeaderboardYouCrown.getDrawable().setTint(colorCrown);
                imgLeaderboardBrunoCrown.setVisibility(View.INVISIBLE);
                break;
            case BRUNO:
                imgLeaderboardYouCrown.setVisibility(View.INVISIBLE);
                imgLeaderboardBrunoCrown.getDrawable().setTint(colorCrown);
                break;
            case TIE:
                imgLeaderboardYouCrown.getDrawable().setTint(colorCrown);
                imgLeaderboardBrunoCrown.getDrawable().setTint(colorCrown);
                break;
        }
    }

    @Override
    public void setupTracklist(final List<BrunoTrack> tracks) {
        int[] colors = getResources().getIntArray(R.array.colorRouteList);

        for (int i = 0; i < tracks.size(); i++) {
            BrunoTrack track = tracks.get(i);

            View view = getLayoutInflater().inflate(R.layout.view_holder_fitness_details, null);
            ImageView musicNote = view.findViewById(R.id.image_view_fitness_details_holder_music);
            musicNote.setColorFilter(colors[i % colors.length]);
            TextView songName = view.findViewById(R.id.text_view_fitness_details_holder_song);
            songName.setText(track.getName());

            TextView artist = view.findViewById(R.id.text_view_fitness_details_holder_artist);
            artist.setText(track.getArtists());
            runTracklist.addView(view);
        }
    }

    @Override
    public void drawRoute(final List<TrackSegment> trackSegments, float routeWidth) {
        for (TrackSegment trackSegment : trackSegments) {
            List<LatLng> trackSegmentLocations = trackSegment.getLatLngs();
            map.addPolyline(new PolylineOptions()
                    .addAll(trackSegmentLocations)
                    .color(trackSegment.getRouteColour())
                    .width(routeWidth));
        }
    }

    @Override
    public void moveCamera(final CameraUpdate cameraUpdate) {
        map.moveCamera(cameraUpdate);
    }
}
