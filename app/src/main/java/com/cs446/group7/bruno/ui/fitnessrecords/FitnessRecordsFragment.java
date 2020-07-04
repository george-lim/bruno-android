package com.cs446.group7.bruno.ui.fitnessrecords;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.cs446.group7.bruno.R;
import com.cs446.group7.bruno.ui.FragmentToolbarFormatter;

public class FitnessRecordsFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_fitness_records, container, false);
        new FragmentToolbarFormatter((AppCompatActivity) getActivity(), view, R.id.appbar_fitness_records)
                .format(getResources().getString(R.string.title_fitness_records), false);

        Button toDetailsBtn = view.findViewById(R.id.button_to_details);
        toDetailsBtn.setOnClickListener(this::handleNavigateToFitnessDetailsClick);
        return view;
    }

    private void handleNavigateToFitnessDetailsClick(final View view) {
        NavController navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment);
        navController.navigate(R.id.action_fragmenttoplevel_to_fragmentfitnessdetails);
    }
}