package com.cs446.group7.bruno.ui.onboarding;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.cs446.group7.bruno.R;
import com.cs446.group7.bruno.ui.shared.FallbackPlaylistAction;

public class OnboardingFallbackPlaylistWrapperFragment extends Fragment implements FallbackPlaylistAction {

    private OnboardingFragment onboardingFragment;
    private Button btnPrimaryAction;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        onboardingFragment = (OnboardingFragment) this.getParentFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_onboarding_fallback_playlist_wrapper, container, false);
        btnPrimaryAction = view.findViewById(R.id.btn_primary_action);
        btnPrimaryAction.setEnabled(false);
        return view;
    }

    @Override
    public void updatePrimaryAction(ActionType action, View.OnClickListener clickListener) {
        switch (action) {
            case SELECT_PLAYLIST:
                btnPrimaryAction.setText(getResources().getString(R.string.select_button));
                break;
            case NO_PLAYLIST:
                btnPrimaryAction.setText(getResources().getString(R.string.next_button));
                break;
            case QUIT:
                btnPrimaryAction.setText(getResources().getString(R.string.quit_button));
                break;
        }
        btnPrimaryAction.setOnClickListener(clickListener);
        btnPrimaryAction.setEnabled(true);
    }

    @Override
    public void onSelectPlaylistPressed() {
        onboardingFragment.moveToNextTab();
    }

    @Override
    public void onNoPlaylistPressed() {
        onboardingFragment.moveToNextTab();
    }
}