package com.bruno.android.ui.fitnessdetails;

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

import com.bruno.android.R;
import com.bruno.android.models.FitnessModel;
import com.bruno.android.models.TrackSegment;
import com.bruno.android.music.BrunoTrack;
import com.bruno.android.ui.AppbarFormatter;
import com.bruno.android.viewmodels.FitnessDetailsViewModel;
import com.bruno.android.viewmodels.FitnessDetailsViewModelDelegate;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.List;

public class FitnessDetailsFragment extends Fragment implements FitnessDetailsViewModelDelegate {

    // MARK: - Public constants

    public static final String FITNESS_RECORD_INDEX = "FITNESS_RECORD_INDEX";

    // MARK: - UI components

    private GoogleMap map;
    private ImageView imgLeaderboardUserCrown;
    private ImageView imgLeaderboardBrunoCrown;
    private TextView txtLeaderboardUserTime;
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
        imgLeaderboardUserCrown = view.findViewById(R.id.image_view_leaderboard_user_crown);
        imgLeaderboardBrunoCrown = view.findViewById(R.id.image_view_leaderboard_bruno_crown);
        txtLeaderboardUserTime = view.findViewById(R.id.text_view_leaderboard_user_time);
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
        viewModel = new FitnessDetailsViewModel(
                requireActivity().getApplicationContext(),
                model,
                this,
                getArguments().getInt(FITNESS_RECORD_INDEX)
        );

        mapFragment.getMapAsync(googleMap -> {
            map = googleMap;
            map.getUiSettings().setAllGesturesEnabled(false);
            viewModel.onMapReady();
        });
    }

    // MARK: - Private methods

    // TODO: Use an adapter instead of manually adding view holders.
    private void setupTracklist(final List<BrunoTrack> tracks) {
        int[] routeColours = getResources().getIntArray(R.array.colorRouteList);
        int colourIndex = 0;

        for (BrunoTrack track : tracks) {
            View viewHolderItemView = getLayoutInflater()
                    .inflate(R.layout.view_holder_fitness_details, null);
            ImageView musicNote = viewHolderItemView
                    .findViewById(R.id.image_view_fitness_details_holder_music);

            musicNote.setColorFilter(routeColours[colourIndex]);
            colourIndex = (colourIndex + 1) % routeColours.length;

            TextView songName = viewHolderItemView
                    .findViewById(R.id.text_view_fitness_details_holder_song);
            songName.setText(track.getName());

            TextView artist = viewHolderItemView
                    .findViewById(R.id.text_view_fitness_details_holder_artist);
            artist.setText(track.getArtists());
            runTracklist.addView(viewHolderItemView);
        }
    }

    // MARK: - FitnessDetailsViewModelDelegate methods

    @Override
    public void setupUI(final String leaderboardUserTimeText,
                        final String leaderboardBrunoTimeText,
                        final String statsDistanceText,
                        final String statsStepsText,
                        final String statsClockText,
                        final String appBarTitle,
                        final FitnessDetailsViewModel.Winner winner,
                        final List<BrunoTrack> tracks) {
        txtLeaderboardUserTime.setText(leaderboardUserTimeText);
        txtLeaderboardBrunoTime.setText(leaderboardBrunoTimeText);
        txtStatsDistance.setText(statsDistanceText);
        txtStatsSteps.setText(statsStepsText);
        txtStatsClock.setText(statsClockText);

        AppbarFormatter.format((AppCompatActivity) requireActivity(),
                requireView(),
                R.id.appbar_fitness_details,
                appBarTitle,
                true);

        int colorCrown = getResources().getColor(R.color.colorCrown, null);

        switch (winner) {
            case USER:
                imgLeaderboardUserCrown.getDrawable().setTint(colorCrown);
                imgLeaderboardBrunoCrown.setVisibility(View.INVISIBLE);
                break;
            case BRUNO:
                imgLeaderboardUserCrown.setVisibility(View.INVISIBLE);
                imgLeaderboardBrunoCrown.getDrawable().setTint(colorCrown);
                break;
            case TIE:
                imgLeaderboardUserCrown.getDrawable().setTint(colorCrown);
                imgLeaderboardBrunoCrown.getDrawable().setTint(colorCrown);
                break;
        }

        setupTracklist(tracks);
    }

    @Override
    public void drawRoute(final List<TrackSegment> trackSegments, float routeWidth) {
        for (TrackSegment trackSegment : trackSegments) {
            List<LatLng> trackSegmentLatLngs = trackSegment.getLatLngs();
            map.addPolyline(new PolylineOptions()
                    .addAll(trackSegmentLatLngs)
                    .color(trackSegment.getRouteColour())
                    .width(routeWidth));
        }
    }

    @Override
    public void moveCamera(final CameraUpdate cameraUpdate) {
        map.moveCamera(cameraUpdate);
    }
}
