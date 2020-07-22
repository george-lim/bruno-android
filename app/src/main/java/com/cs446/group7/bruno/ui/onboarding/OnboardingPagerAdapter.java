package com.cs446.group7.bruno.ui.onboarding;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class OnboardingPagerAdapter extends FragmentStateAdapter {

    public OnboardingPagerAdapter(@NonNull FragmentActivity activity) {
        super(activity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case OnboardingTab.WELCOME:
                return new OnboardingWelcomeFragment();
            case OnboardingTab.RECORD:
                return new OnboardingRecordFragment();
            case OnboardingTab.AVATAR:
                return new OnboardingAvatarFragment();
            case OnboardingTab.PERMISSION:
                return new OnboardingPermissionFragment();
            case OnboardingTab.DONE:
                return new OnboardingDoneFragment();
            default:
                return new OnboardingWelcomeFragment();
        }
    }

    @Override
    public int getItemCount() {
        return OnboardingTab.NUM_TABS;
    }
}
