package com.cs446.group7.bruno.ui.settings;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.cs446.group7.bruno.R;
import com.cs446.group7.bruno.ui.AppbarFormatter;

public class SettingsFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        AppbarFormatter.format(
                (AppCompatActivity) getActivity(),
                view,
                R.id.appbar_settings,
                getResources().getString(R.string.title_settings),
                false);
        return view;
    }
}
