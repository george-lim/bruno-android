package com.cs446.group7.bruno.ui.onboarding;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.cs446.group7.bruno.R;

public class OnboardingRecordFragment extends Fragment {

    private OnboardingFragment onboardingFragment;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        onboardingFragment = (OnboardingFragment) this.getParentFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_onboarding_record, container, false);
        Button btnNext = view.findViewById(R.id.btn_tab1_next);
        btnNext.setOnClickListener(this::handleNext);
        return view;
    }

    private void handleNext(final View view) {
        onboardingFragment.moveToNextTab();
    }
}