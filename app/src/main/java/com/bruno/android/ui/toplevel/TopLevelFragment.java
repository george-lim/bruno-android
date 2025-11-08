package com.bruno.android.ui.toplevel;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.viewpager2.widget.ViewPager2;

import com.bruno.android.MainActivity;
import com.bruno.android.R;
import com.bruno.android.storage.PreferencesStorage;
import com.bruno.android.ui.fitnessrecords.FitnessRecordsFragment;
import com.bruno.android.ui.routeplanning.RoutePlanningFragment;
import com.bruno.android.ui.settings.SettingsFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class TopLevelFragment extends Fragment implements BottomNavigationView.OnItemSelectedListener {
    private ViewPager2 viewPager;
    private BottomNavigationView btmNav;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_top_level, container, false);
        setupBottomNavigation(root);
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        PreferencesStorage storage = MainActivity.getPreferencesStorage();
        boolean completedOnboarding = storage.getBoolean(
                PreferencesStorage.KEYS.COMPLETED_ONBOARDING,
                false
        );

        if (!completedOnboarding) {
            NavController navController = Navigation.findNavController(view);
            navController.navigate(R.id.action_fragmenttoplevel_to_fragmentonboarding);
        }
    }

    /**
     * This function sets up bottom navigation by using a non-swipable ViewPager to host tab fragments.
     * This functions also sets up listener such that ViewPager are sync with the bottom navigation bar.
     * ViewPager are setup to retain state between tab.
     */
    private void setupBottomNavigation(final View view) {
        viewPager = view.findViewById(R.id.main_screen_pager);
        btmNav = view.findViewById(R.id.bttm_nav);
        BottomNavPagerAdapter adaptor = new BottomNavPagerAdapter(this);
        adaptor.addFragment(new RoutePlanningFragment());
        adaptor.addFragment(new FitnessRecordsFragment());
        adaptor.addFragment(new SettingsFragment());
        viewPager.setAdapter(adaptor);
        viewPager.setUserInputEnabled(false);
        int numOfTabs = adaptor.getItemCount();
        viewPager.setOffscreenPageLimit(numOfTabs);
        btmNav.setOnItemSelectedListener(this);
        viewPager.registerOnPageChangeCallback(pageChangeCallback);
    }

    private final ViewPager2.OnPageChangeCallback pageChangeCallback = new ViewPager2.OnPageChangeCallback() {
        @Override
        public void onPageSelected(int position) {
            switch (position) {
                case BottomNavTab.MAP:
                    btmNav.setSelectedItemId(R.id.navigate_route_planning);
                    break;
                case BottomNavTab.FITNESS:
                    btmNav.setSelectedItemId(R.id.navigate_fitness_records);
                    break;
                case BottomNavTab.SETTINGS:
                    btmNav.setSelectedItemId(R.id.navigate_settings);
                    break;
            }
            super.onPageSelected(position);
        }
    };

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        int itemId = menuItem.getItemId();

        if (itemId == R.id.navigate_route_planning) {
            viewPager.setCurrentItem(BottomNavTab.MAP, false);
        } else if (itemId == R.id.navigate_fitness_records) {
            viewPager.setCurrentItem(BottomNavTab.FITNESS, false);
        } else if (itemId == R.id.navigate_settings) {
            viewPager.setCurrentItem(BottomNavTab.SETTINGS, false);
        } else {
            return false;
        }

        return true;
    }

    /**
     * We want the app to exit only if the app is currently on the map tab.
     * This handles back press behaviour when records or setting tab is selected.
     *
     * @return true if back press is handled, otherwise false.
     */
    public boolean onBackPress() {
        int pos = viewPager.getCurrentItem();
        if (pos != BottomNavTab.MAP) {
            viewPager.setCurrentItem(BottomNavTab.MAP, false);
            return true;
        }
        return false;
    }
}