package com.cs446.group7.bruno.ui.settings;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.cs446.group7.bruno.R;
import com.cs446.group7.bruno.ui.FragmentToolbar;

public class SettingsFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        FragmentToolbar appbar = new FragmentToolbar.Builder()
                .withId(R.id.appbar_settings)
                .withTitle(getResources().getString(R.string.title_settings))
                .build();
        appbar.addToFragment((AppCompatActivity) getActivity(), view);
        return view;
    }
}