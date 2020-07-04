package com.cs446.group7.bruno.ui;


import android.view.View;
import android.widget.TextView;

import androidx.annotation.IdRes;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.cs446.group7.bruno.R;

public class FragmentToolbarFormatter {
    private AppCompatActivity activity;
    private View viewContainer;
    @IdRes
    private int appbarId;

    public FragmentToolbarFormatter(final AppCompatActivity activity, final View v, final int appbarId) {
        this.activity = activity;
        this.viewContainer = v;
        this.appbarId = appbarId;
    }

    /**
     * This method is intended to format custom toolbar and hook it up to the fragment.
     *
     * @param title         string displayed to user in appbar (max length 30)
     * @param hasBackButton whether toolbar will show a back button
     */
    public void format(final String title, final boolean hasBackButton) {
        Toolbar toolbar = viewContainer.findViewById(appbarId).findViewById(R.id.toolbar);
        TextView toolbarText = viewContainer.findViewById(appbarId).findViewById(R.id.toolbar_text);
        toolbarText.setText(title);

        activity.setSupportActionBar(toolbar);
        ActionBar actionbar = activity.getSupportActionBar();
        actionbar.setDisplayShowTitleEnabled(false);
        if (hasBackButton) {
            actionbar.setDisplayShowHomeEnabled(true);
            actionbar.setDisplayHomeAsUpEnabled(true);
            toolbar.setNavigationOnClickListener(view -> activity.onBackPressed());
        }
    }
}
