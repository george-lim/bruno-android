package com.cs446.group7.bruno.ui.onboarding;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.viewpager2.widget.ViewPager2;

import com.cs446.group7.bruno.MainActivity;
import com.cs446.group7.bruno.R;
import com.cs446.group7.bruno.capability.Capability;
import com.cs446.group7.bruno.preferencesstorage.PreferencesStorage;
import com.cs446.group7.bruno.utils.Callback;

public class OnboardingFragment extends Fragment {

    private ViewPager2 viewPager;
    private Button onboardingPrimaryAction;
    private View[] tabIndicators;
    private int currentTab;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_onboarding, container, false);
        setupOnboardingTabs(root);
//
//        onboardingPrimaryAction = root.findViewById(R.id.btn_onboarding_primary_action);
//        onboardingPrimaryAction.setOnClickListener(this::handleOnboardingPrimaryAction);
        return root;
    }

    private void setupOnboardingTabs(final View view) {
        viewPager = view.findViewById(R.id.onboarding_pager);
        OnboardingPagerAdapter adapter = new OnboardingPagerAdapter(getActivity());
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
//            if (position == OnboardingTab.AVATAR) {
//                onboardingPrimaryAction.setText("Get Started");
//            } else {
//                onboardingPrimaryAction.setText("Next");
//            }
            super.onPageSelected(position);
        }
    };

    private void handleOnboardingPrimaryAction(final View view) {
        switch (currentTab) {
            case OnboardingTab.WELCOME:
                handleMoveToRecordOnboarding(); break;
            case OnboardingTab.RECORD:
                viewPager.setCurrentItem(OnboardingTab.AVATAR); break;
            case OnboardingTab.AVATAR:
                finishOnBoardingProcess();
        }
    }

    private void handleMoveToRecordOnboarding() {
        final Capability[] REQUIRED_CAPABILITIES = { Capability.LOCATION, Capability.INTERNET };
        MainActivity.getCapabilityService().request(REQUIRED_CAPABILITIES, new Callback<Void, Void>() {
            @Override
            public void onSuccess(Void result) {
                viewPager.setCurrentItem(OnboardingTab.RECORD);
            }

            @Override
            public void onFailed(Void result) {
                viewPager.setCurrentItem(OnboardingTab.RECORD);
            }
        });
    }

    private void finishOnBoardingProcess() {
        PreferencesStorage storage = MainActivity.getPreferencesStorage();
        storage.setBoolean(PreferencesStorage.COMPLETED_ONBOARDING, true);

        NavController navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment);
        navController.navigate(R.id.action_fragmentonboarding_to_fragmenttoplevel);
    }
}