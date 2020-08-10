package com.cs446.group7.bruno.ui.settings;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.cs446.group7.bruno.R;
import com.cs446.group7.bruno.ui.AppbarFormatter;
import com.cs446.group7.bruno.ui.shared.FallbackPlaylistAction;

public class SettingsFallbackPlaylistWrapperFragment extends Fragment implements FallbackPlaylistAction {

    private Button btnPrimaryAction;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings_fallback_playlist_wrapper, container, false);
        AppbarFormatter.format(
                (AppCompatActivity) requireActivity(),
                view,
                R.id.appbar_fallback_playlist,
                getResources().getString(R.string.settings_fallback_playlist_title),
                true);
        btnPrimaryAction = view.findViewById(R.id.btn_primary_action);
        btnPrimaryAction.setEnabled(false);
        return view;
    }

    @Override
    public void updatePrimaryAction(ActionType action, View.OnClickListener clickListener) {
        switch (action) {
            case SELECT_PLAYLIST:
                btnPrimaryAction.setText(getResources().getString(R.string.save_button));
                break;
            case NO_PLAYLIST:
                btnPrimaryAction.setText(getResources().getString(R.string.ok_button));
                break;
            case QUIT:
                btnPrimaryAction.setText(getResources().getString(R.string.quit_button));
                break;
        }
        btnPrimaryAction.setOnClickListener(clickListener);
        btnPrimaryAction.setEnabled(true);
    }

    @Override
    public void onSelectPlaylistPressed() {
        if (getActivity() != null) {
            getActivity().onBackPressed();
        }
    }

    @Override
    public void onNoPlaylistPressed() {
        if (getActivity() != null) {
            getActivity().onBackPressed();
        }
    }
}
