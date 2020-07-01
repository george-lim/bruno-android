package com.cs446.group7.bruno.ui;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.IdRes;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.cs446.group7.bruno.R;

public class FragmentToolbar {
    private int appbarId;
    private String title;
    private boolean hasUp;

    protected FragmentToolbar(@IdRes int appbarId, String title, boolean hasUp) {
        this.appbarId = appbarId;
        this.title = title;
        this.hasUp = hasUp;
    }

    public void addToFragment(AppCompatActivity activity) {
        Toolbar toolbar = activity.findViewById(appbarId).findViewById(R.id.toolbar);
        activity.setSupportActionBar(toolbar);
        ActionBar actionbar = activity.getSupportActionBar();

        actionbar.setDisplayShowTitleEnabled(false);
        TextView toolbarText = activity.findViewById(appbarId).findViewById(R.id.toolbar_text);
        toolbarText.setText(title);

        if (hasUp) {
            actionbar.setDisplayShowHomeEnabled(true);
            actionbar.setDisplayHomeAsUpEnabled(true);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    activity.onBackPressed();
                }
            });
        }
    }

    public static class Builder {
        private final int defaultInt = -1;
        private int appbarId = defaultInt;
        private String title = "";
        private boolean hasUp = false;

        public Builder withId(@IdRes int appbarId) {
            this.appbarId = appbarId;
            return this;
        }

        public Builder withTitle(String title) {
            this.title = title;
            return this;
        }

        public Builder withUpButton() {
            this.hasUp = true;
            return this;
        }

        public FragmentToolbar build() {
            if (appbarId == defaultInt) {
                throw new IllegalStateException();
            }
            return new FragmentToolbar(appbarId, title, hasUp);
        }
    }
}
