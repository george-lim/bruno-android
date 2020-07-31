package com.cs446.group7.bruno.ui.settings;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.cs446.group7.bruno.R;
import com.cs446.group7.bruno.ui.AppbarFormatter;
import com.cs446.group7.bruno.ui.shared.FallbackPlaylistFragment;

public class SettingsFallbackPlaylistWrapperFragment extends Fragment {

    private FallbackPlaylistFragment fallbackPlaylistFragment;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings_fallback_playlist_wrapper, container, false);
        AppbarFormatter.format(
                (AppCompatActivity) getActivity(),
                view,
                R.id.appbar_fallback_playlist,
                getResources().getString(R.string.settings_fallback_playlist_title),
                true);
        FragmentManager fragmentManager = getChildFragmentManager();
        Fragment f = fragmentManager.findFragmentByTag("tag_fallback_playlist");
        if (f instanceof FallbackPlaylistFragment) {
            fallbackPlaylistFragment = (FallbackPlaylistFragment) f;
        }
        Button btnSave = view.findViewById(R.id.btn_save);
        btnSave.setOnClickListener(this::handleSave);
        return view;
    }

    private void handleSave(final View view) {
        fallbackPlaylistFragment.saveSelectedPlaylist();
    }

}