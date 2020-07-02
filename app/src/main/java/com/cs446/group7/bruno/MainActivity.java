package com.cs446.group7.bruno;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.viewpager2.widget.ViewPager2;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    /**
     * We want the app to exit only if the app is currently on the map tab.
     * The setup of nav_graph will exit the app if it display TopLevelFragment.
     * Hence, overriding the back pressed behaviour to achieve desire result.
     */
    // TODO: encapsulate ViewPager logic in TopLevelFragment (yet to figure how)
    @Override
    public void onBackPressed() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        int id = navController.getCurrentDestination().getId();
        if (id == R.id.fragment_top_lvl) {
            ViewPager2 pager = findViewById(R.id.main_screen_pager);
            int pos = pager.getCurrentItem();
            if (pos != 0) {
                pager.setCurrentItem(0, false);
                return;
            }
        }
        super.onBackPressed();
    }
}