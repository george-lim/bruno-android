package com.cs446.group7.bruno.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.cs446.group7.bruno.R;
import com.cs446.group7.bruno.ui.BottomNavPagerAdaptor;
import com.cs446.group7.bruno.ui.fitnessrecords.FitnessRecordsFragment;
import com.cs446.group7.bruno.ui.routeplanning.RoutePlanningFragment;
import com.cs446.group7.bruno.ui.settings.SettingsFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class TopLevelFragment extends Fragment implements BottomNavigationView.OnNavigationItemSelectedListener {
    private ViewPager2 viewPager;
    private BottomNavigationView bttmNav;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_top_level, container, false);
        setupBottomNavigation(root);
        return root;
    }

    private void setupBottomNavigation(View view) {
        viewPager = view.findViewById(R.id.main_screen_pager);
        bttmNav = view.findViewById(R.id.bttm_nav);
        BottomNavPagerAdaptor adaptor = new BottomNavPagerAdaptor(getActivity());
        adaptor.addFragment(new RoutePlanningFragment());
        adaptor.addFragment(new FitnessRecordsFragment());
        adaptor.addFragment(new SettingsFragment());
        viewPager.setAdapter(adaptor);
        viewPager.setUserInputEnabled(false);
        viewPager.setOffscreenPageLimit(3);
        bttmNav.setOnNavigationItemSelectedListener(this);
        viewPager.registerOnPageChangeCallback(pageChangeCallback);
    }

    ViewPager2.OnPageChangeCallback pageChangeCallback = new ViewPager2.OnPageChangeCallback() {
        @Override
        public void onPageSelected(int position) {
            switch (position) {
                case 0:
                    bttmNav.setSelectedItemId(R.id.navigate_route_planning);
                    break;
                case 1:
                    bttmNav.setSelectedItemId(R.id.navigate_fitness_records);
                    break;
                case 3:
                    bttmNav.setSelectedItemId(R.id.navigate_settings);
                    break;
            }
            super.onPageSelected(position);
        }
    };

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.navigate_route_planning:
                viewPager.setCurrentItem(0, false);
                break;
            case R.id.navigate_fitness_records:
                viewPager.setCurrentItem(1, false);
                break;
            case R.id.navigate_settings:
                viewPager.setCurrentItem(2, false);
                break;
        }
        return true;
    }
}