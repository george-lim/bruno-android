package com.cs446.group7.bruno.ui.shared;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cs446.group7.bruno.R;
import com.cs446.group7.bruno.music.playlist.PlaylistInfo;

public class FallbackPlaylistFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_fallback_playlist, container, false);
        setup(view);
        return view;
    }

    private void setup(final View view) {
        // Fallback playlist list
        RecyclerView fallbackPlaylistsList = view.findViewById(R.id.recycler_view_fallback_playlist);
        fallbackPlaylistsList.setHasFixedSize(true);
        fallbackPlaylistsList.setLayoutManager(new LinearLayoutManager(getActivity()));
        // Data
        PlaylistInfo[] playlists = new PlaylistInfo[10];
        FallbackPlaylistsAdapter adapter = new FallbackPlaylistsAdapter(playlists);
        fallbackPlaylistsList.setAdapter(adapter);
    }

    public void saveSelectedPlaylist() {
        Log.d("borisg", "saving playlist");
    }
}