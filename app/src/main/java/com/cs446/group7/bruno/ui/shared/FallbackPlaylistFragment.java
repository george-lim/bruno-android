package com.cs446.group7.bruno.ui.shared;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

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
    private LinearLayout playlistSelectionView;
    private LinearLayout noPlaylistsView;
    private LinearLayout spotifyErrorView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new FallbackPlaylistViewModel(getActivity().getApplicationContext(), this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_fallback_playlist, container, false);
        playlistSelectionView = view.findViewById(R.id.layout_spotify_select_playlist);
        noPlaylistsView = view.findViewById(R.id.layout_spotify_no_playlist);
        spotifyErrorView = view.findViewById(R.id.layout_spotify_error);
        setupRecyclerView(view);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        // This call is needed to be in onResume because ViewPager used for onBoarding have a tendency to
        // create view in previous tab to enhance performance.
        // This also allows us to re-sync user playlist when they added a new on after seeing they have none.
        viewModel.getUserPlaylistLibrary();
    }

    private void setupRecyclerView(final View view) {
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

    @Override
    public void showPlaylistSelectionView(List<PlaylistMetadata> playlists) {
        adapter.setPlaylists(playlists);
        adapter.notifyDataSetChanged();

        playlistSelectionView.setVisibility(View.VISIBLE);
        noPlaylistsView.setVisibility(View.INVISIBLE);
        spotifyErrorView.setVisibility(View.INVISIBLE);
    }

    @Override
    public void showNoPlaylistsView() {
        playlistSelectionView.setVisibility(View.INVISIBLE);
        noPlaylistsView.setVisibility(View.VISIBLE);
        spotifyErrorView.setVisibility(View.INVISIBLE);
    }

    @Override
    public void showSpotifyErrorView(final String errorText) {
        TextView tvError = getView().findViewById(R.id.tv_spotify_error_description);
        tvError.setText(errorText);

        playlistSelectionView.setVisibility(View.INVISIBLE);
        noPlaylistsView.setVisibility(View.INVISIBLE);
        spotifyErrorView.setVisibility(View.VISIBLE);
    }
}