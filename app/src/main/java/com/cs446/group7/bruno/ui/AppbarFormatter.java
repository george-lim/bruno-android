package com.cs446.group7.bruno.ui;


import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.cs446.group7.bruno.R;

public class AppbarFormatter {
    /**
     * This method is intended to format custom toolbar and hook it up to the fragment.
     *
     * @param activity      the activity that hosts the inflated fragment
     * @param viewContainer the view for inflated fragment
     * @param appbarId      id of toolbar from xml
     * @param title         string display to the user on appbar
     * @param hasBackButton true if appbar contains back button
     */
    public static void format(final AppCompatActivity activity,
                              final View viewContainer,
                              final int appbarId,
                              final String title,
                              final boolean hasBackButton) {
        Toolbar toolbar = viewContainer.findViewById(appbarId).findViewById(R.id.toolbar);
        TextView toolbarText = viewContainer.findViewById(appbarId).findViewById(R.id.toolbar_text);
        toolbarText.setText(title);

        activity.setSupportActionBar(toolbar);
        ActionBar actionbar = activity.getSupportActionBar();

        if (actionbar == null) {
            return;
        }

        actionbar.setDisplayShowTitleEnabled(false);

        if (hasBackButton) {
            actionbar.setDisplayShowHomeEnabled(true);
            actionbar.setDisplayHomeAsUpEnabled(true);
            toolbar.setNavigationOnClickListener(view -> activity.onBackPressed());
        }
    }
}
