package com.cs446.group7.bruno.ui.shared;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cs446.group7.bruno.R;
import com.cs446.group7.bruno.music.playlist.PlaylistMetadata;
import com.cs446.group7.bruno.viewmodels.FallbackPlaylistViewModel;
import com.cs446.group7.bruno.viewmodels.FallbackPlaylistViewModelDelegate;

import java.util.List;

public class FallbackPlaylistFragment extends Fragment implements FallbackPlaylistViewModelDelegate {

    private FallbackPlaylistViewModel viewModel;
    private FallbackPlaylistsAdapter adapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new FallbackPlaylistViewModel(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_fallback_playlist, container, false);
        setup(view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel.getUserPrivatePlaylist();
    }

    private void setup(final View view) {
        // Fallback playlist list
        RecyclerView fallbackPlaylistsList = view.findViewById(R.id.recycler_view_fallback_playlist);
        fallbackPlaylistsList.setHasFixedSize(true);
        fallbackPlaylistsList.setLayoutManager(new LinearLayoutManager(getActivity()));
        // Data
        adapter = new FallbackPlaylistsAdapter();
        fallbackPlaylistsList.setAdapter(adapter);
    }

    public void saveSelectedPlaylist() {
        Log.d("borisg", "saving playlist");
    }

    public void updatePlaylistData(final List<PlaylistMetadata> playlists) {
        adapter.setPlaylists(playlists);
        adapter.notifyDataSetChanged();
    }
}