package com.bruno.android.ui.settings;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.bruno.android.R;
import com.bruno.android.ui.AppbarFormatter;

public class CreditsFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_credits, container, false);
        AppbarFormatter.format(
                (AppCompatActivity) requireActivity(),
                view,
                R.id.appbar_credits,
                getResources().getString(R.string.credits),
                true);
        return view;
    }
}