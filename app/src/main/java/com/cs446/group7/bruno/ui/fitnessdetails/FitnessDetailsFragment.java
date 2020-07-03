package com.cs446.group7.bruno.ui.fitnessdetails;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.cs446.group7.bruno.R;
import com.cs446.group7.bruno.ui.FragmentToolbar;

public class FitnessDetailsFragment extends Fragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_fitness_details, container, false);
        FragmentToolbar appbar = new FragmentToolbar.Builder()
                .withId(R.id.appbar_fitness_details)
                .withTitle("June 15 · 11:30 AM ")
                .withUpButton()
                .build();
        appbar.addToFragment((AppCompatActivity) getActivity(), view);
        return view;
    }
}