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
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.cs446.group7.bruno.R;
import com.cs446.group7.bruno.models.FitnessModel;
import com.cs446.group7.bruno.models.RouteModel;
import com.cs446.group7.bruno.music.BrunoTrack;
import com.cs446.group7.bruno.ui.AppbarFormatter;
import com.cs446.group7.bruno.utils.TimeUtils;
import com.cs446.group7.bruno.viewmodels.FitnessDetailsViewModel;
import com.cs446.group7.bruno.viewmodels.FitnessDetailsViewModelDelegate;
import com.cs446.group7.bruno.viewmodels.OnRouteViewModel;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;

import java.util.List;

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
    private RecyclerView runTracklistList;

    // MARK: - Private members

    private FitnessDetailsViewModel viewModel;

    // MARK: - Lifecycle Methods

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_fitness_details, container, false);
        AppbarFormatter.format(
                (AppCompatActivity) getActivity(),
                view,
                R.id.appbar_fitness_details,
                "June 15 · 11:30 AM ",
                true);
        imgLeaderboardYouCrown = view.findViewById(R.id.image_view_leaderboard_you_crown);
        imgLeaderboardBrunoCrown = view.findViewById(R.id.image_view_leaderboard_bruno_crown);
        txtLeaderboardYouTime = view.findViewById(R.id.text_view_leaderboard_you_time);
        txtLeaderboardBrunoTime = view.findViewById(R.id.text_view_leaderboard_bruno_time);
        imgStatsSteps = view.findViewById(R.id.image_view_stats_steps);
        imgStatsClock = view.findViewById(R.id.image_view_stats_clock);
        txtStatsSteps = view.findViewById(R.id.text_view_stats_steps);
        txtStatsClock = view.findViewById(R.id.text_view_stats_clock);
        runTracklistList = view.findViewById(R.id.tracklist_view_fitness_details);
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
            viewModel = new FitnessDetailsViewModel(getActivity().getApplicationContext(), model, this);
        });
    }

    // MARK: - FitnessDetailsViewModelDelegate methods

    @Override
    public void setupUI(int youRunDuration,
                        int brunoRunDuration,
                        int stepCount,
                        int timeFromGoal) {
        txtLeaderboardYouTime.setText(TimeUtils.getDurationString(youRunDuration));
        txtLeaderboardBrunoTime.setText(TimeUtils.getDurationString(brunoRunDuration));
        txtStatsSteps.setText(String.format(getResources().getString(R.string.fitness_details_steps_placeholder), stepCount));
        txtStatsClock.setText(String.format(getResources().getString(R.string.fitness_details_clock_placeholder), timeFromGoal));
    }

    // MARK: - Private methods
    @Override
    public void setupTracklistListView(List<BrunoTrack> tracklistList) {
        FitnessDetailsAdapter adapter = new FitnessDetailsAdapter(tracklistList);
        runTracklistList.setAdapter(adapter);
    }

    @Override
    public void displayCrown(int youRunDuration, int brunoRunDuration) {
        if (youRunDuration < brunoRunDuration) {
            // You win
            imgLeaderboardYouCrown.setColorFilter(getResources().getColor(R.color.colorCrown));
            imgLeaderboardBrunoCrown.setVisibility(View.INVISIBLE);
        } else {
            // You lose
            imgLeaderboardYouCrown.setVisibility(View.INVISIBLE);
            imgLeaderboardBrunoCrown.setColorFilter(getResources().getColor(R.color.colorCrown));
        }
    }
}