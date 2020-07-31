package com.cs446.group7.bruno.ui.onboarding;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.cs446.group7.bruno.R;
import com.cs446.group7.bruno.ui.shared.FallbackPlaylistFragment;

public class OnboardingFallbackPlaylistWrapperFragment extends Fragment {

    private OnboardingFragment onboardingFragment;
    private FallbackPlaylistFragment fallbackPlaylistFragment;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        onboardingFragment = (OnboardingFragment) this.getParentFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_onboarding_fallback_playlist_wrapper, container, false);
        FragmentManager fragmentManager = getChildFragmentManager();
        Fragment f = fragmentManager.findFragmentByTag("tag_fallback_playlist");
        if (f instanceof FallbackPlaylistFragment) {
            fallbackPlaylistFragment = (FallbackPlaylistFragment) f;
        }
        Button btnSave = view.findViewById(R.id.btn_select);
        btnSave.setOnClickListener(this::handlePrimaryAction);
        return view;
    }

    private void handlePrimaryAction(final View view) {
        fallbackPlaylistFragment.saveSelectedPlaylist();
        onboardingFragment.moveToNextTab();
    }
}