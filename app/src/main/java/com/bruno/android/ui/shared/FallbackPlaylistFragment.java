package com.bruno.android.ui.shared;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bruno.android.R;
import com.bruno.android.music.playlist.PlaylistMetadata;
import com.bruno.android.viewmodels.FallbackPlaylistViewModel;
import com.bruno.android.viewmodels.FallbackPlaylistViewModelDelegate;

import java.util.List;

public class FallbackPlaylistFragment extends Fragment implements FallbackPlaylistViewModelDelegate {

    private FallbackPlaylistViewModel viewModel;
    private FallbackPlaylistsAdapter adapter;
    private LinearLayout playlistSelectionView;
    private LinearLayout noPlaylistsView;
    private LinearLayout spotifyErrorView;
    private TextView tvError;

    @SuppressWarnings("deprecation")
    private android.app.ProgressDialog progressDialog;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FallbackPlaylistAction wrapperFragment = (FallbackPlaylistAction) this.getParentFragment();
        viewModel = new FallbackPlaylistViewModel(
                requireActivity().getApplicationContext(),
                wrapperFragment,
                this
        );
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_fallback_playlist, container, false);
        playlistSelectionView = view.findViewById(R.id.layout_spotify_select_playlist);
        noPlaylistsView = view.findViewById(R.id.layout_spotify_no_playlist);
        spotifyErrorView = view.findViewById(R.id.layout_spotify_error);
        tvError = view.findViewById(R.id.tv_spotify_error_description);
        setupRecyclerView(view);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        viewModel.getUserPlaylistLibrary();
    }

    private void setupRecyclerView(final View view) {
        // Fallback playlist list
        RecyclerView fallbackPlaylistsList = view.findViewById(R.id.recycler_view_fallback_playlist);
        fallbackPlaylistsList.setHasFixedSize(true);
        fallbackPlaylistsList.setLayoutManager(
                new LinearLayoutManager(requireActivity().getApplicationContext())
        );
        // Data
        adapter = new FallbackPlaylistsAdapter(viewModel);
        fallbackPlaylistsList.setAdapter(adapter);
    }

    @Override
    public void showPlaylistSelectionView(final List<PlaylistMetadata> playlists, final int selectedIndex) {
        adapter.setPlaylists(playlists, selectedIndex);
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
        tvError.setText(errorText);

        playlistSelectionView.setVisibility(View.INVISIBLE);
        noPlaylistsView.setVisibility(View.INVISIBLE);
        spotifyErrorView.setVisibility(View.VISIBLE);
    }

    @Override
    public void quitApp() {
        if (getActivity() != null) {
            getActivity().finish();
        }
    }

    @Override
    @SuppressWarnings("deprecation")
    public void showProgressDialog() {
        if (getActivity() != null) {
            progressDialog = new android.app.ProgressDialog(getActivity());
            progressDialog.setMessage(getResources().getString(R.string.loading_diaglog_text));
            progressDialog.setIndeterminate(false);
            progressDialog.setCancelable(false);
            progressDialog.show();
        }
    }

    @Override
    public void dismissProgressDialog() {
        if (progressDialog == null) {
            return;
        }
        progressDialog.dismiss();
    }

    @Override
    public void showAlertDialog(final String title,
                                final String message,
                                final String positiveButtonText,
                                final DialogInterface.OnClickListener positiveButtonClickListener,
                                boolean isCancelable) {
        if (getActivity() != null) {
            new AlertDialog.Builder(getActivity())
                    .setTitle(title)
                    .setMessage(message)
                    .setPositiveButton(positiveButtonText, positiveButtonClickListener)
                    .setCancelable(isCancelable)
                    .create()
                    .show();
        }
    }
}
