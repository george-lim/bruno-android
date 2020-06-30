package com.cs446.group7.bruno;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.cs446.group7.bruno.ui.BaseFragment;
import com.cs446.group7.bruno.ui.BottomNavPagerAdaptor;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    private ViewPager2 viewPager;
    private BottomNavigationView bttmNav;
    private List<BaseFragment> baseFragments = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setupBottomNavigation();
    }

    private void setupBottomNavigation() {
        viewPager = findViewById(R.id.main_screen_pager);
        bttmNav = findViewById(R.id.bttm_nav);
        BottomNavPagerAdaptor adaptor = new BottomNavPagerAdaptor(this);
        baseFragments.add(BaseFragment.newInstance(R.layout.content_route_base, R.id.nav_host_route));
        baseFragments.add(BaseFragment.newInstance(R.layout.content_fitness_base, R.id.nav_host_fitness));
        baseFragments.add(BaseFragment.newInstance(R.layout.content_setting_base, R.id.nav_host_settings));
        for (Fragment f: baseFragments) {
            adaptor.addFragment(f);
        }
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
                    bttmNav.setSelectedItemId(R.id.navigate_route_planning); break;
                case 1:
                    bttmNav.setSelectedItemId(R.id.navigate_fitness_records); break;
                case 3:
                    bttmNav.setSelectedItemId(R.id.navigate_settings); break;
            }
            super.onPageSelected(position);
        }
    };

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.navigate_route_planning:
                viewPager.setCurrentItem(0, false); break;
            case R.id.navigate_fitness_records:
                viewPager.setCurrentItem(1, false); break;
            case R.id.navigate_settings:
                viewPager.setCurrentItem(2, false); break;
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        BaseFragment curFragment = baseFragments.get(viewPager.getCurrentItem());
        boolean canNavigateUp = curFragment.onBackPressed();
        if (canNavigateUp) {
            return;
        } else if (viewPager.getCurrentItem() != 0) {
            viewPager.setCurrentItem(0, false);
        } else {
            super.onBackPressed();
        }
    }
}