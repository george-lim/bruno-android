package com.cs446.group7.bruno.ui.onboarding;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.cs446.group7.bruno.R;

public class OnboardingFragment extends Fragment {

    private ViewPager2 viewPager;
    private View[] tabIndicators;
    private int currentTab;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_onboarding, container, false);
        setupOnboardingTabs(root);
        return root;
    }

    private void setupOnboardingTabs(final View view) {
        viewPager = view.findViewById(R.id.onboarding_pager);
        OnboardingPagerAdapter adapter = new OnboardingPagerAdapter(this);
        viewPager.setAdapter(adapter);

        tabIndicators = new View[OnboardingTab.NUM_TABS];
        tabIndicators[0] = view.findViewById(R.id.tab0_indicator);
        tabIndicators[1] = view.findViewById(R.id.tab1_indicator);
        tabIndicators[2] = view.findViewById(R.id.tab2_indicator);
        tabIndicators[3] = view.findViewById(R.id.tab3_indicator);
        tabIndicators[4] = view.findViewById(R.id.tab4_indicator);
        currentTab = OnboardingTab.WELCOME;
        viewPager.setCurrentItem(currentTab);
        viewPager.registerOnPageChangeCallback(pageChangeCallback);
    }

    private ViewPager2.OnPageChangeCallback pageChangeCallback = new ViewPager2.OnPageChangeCallback() {
        @Override
        public void onPageSelected(int position) {
            currentTab = position;
            for (int i = 0; i < OnboardingTab.NUM_TABS; i++) {
                if (i == position) {
                    tabIndicators[i].setSelected(true);
                } else {
                    tabIndicators[i].setSelected(false);
                }
            }
            super.onPageSelected(position);
        }
    };

    public void moveToNextTab() {
        switch (currentTab) {
            case OnboardingTab.WELCOME:
                viewPager.setCurrentItem(OnboardingTab.RECORD); break;
            case OnboardingTab.RECORD:
                viewPager.setCurrentItem(OnboardingTab.AVATAR); break;
            case OnboardingTab.AVATAR:
                viewPager.setCurrentItem(OnboardingTab.PERMISSION); break;
            case OnboardingTab.PERMISSION:
                viewPager.setCurrentItem(OnboardingTab.DONE); break;
        }
    }
}