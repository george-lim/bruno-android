package com.cs446.group7.bruno.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class BaseFragment extends Fragment {
    private static String KEY_LAYOUT = "layout_key";
    private static String KEY_NAV_HOST = "nav_host_key";

    private final int defaultInt = -1;
    private int fragmentLayout = defaultInt;
    private int navHostId = defaultInt;

    /**
     * Use this factory method to create a new instance of
     * a fragment using the provided parameters.
     *
     * @param fragmentLayout Id value of fragment.
     * @param navHostId      Id value of navigation host.
     * @return A new instance of fragment inflated with fragmentLayout.
     */
    public static BaseFragment newInstance(@NonNull int fragmentLayout, @NonNull int navHostId) {
        BaseFragment fragment = new BaseFragment();
        Bundle args = new Bundle();
        args.putInt(KEY_LAYOUT, fragmentLayout);
        args.putInt(KEY_NAV_HOST, navHostId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            fragmentLayout = getArguments().getInt(KEY_LAYOUT);
            navHostId = getArguments().getInt(KEY_NAV_HOST);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (fragmentLayout == defaultInt) return null;
        return inflater.inflate(fragmentLayout, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
    }
}
