package com.bruno.android.ui.shared;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bruno.android.R;
import com.bruno.android.music.playlist.PlaylistMetadata;
import com.bruno.android.viewmodels.FallbackPlaylistViewModel;

import java.util.ArrayList;
import java.util.List;

public class FallbackPlaylistsAdapter extends RecyclerView.Adapter<FallbackPlaylistsAdapter.FallbackPlaylistViewHolder> {

    private List<PlaylistMetadata> playlists;
    private final FallbackPlaylistViewModel viewModel;
    private int positionSelected = 0;

    public FallbackPlaylistsAdapter(final FallbackPlaylistViewModel viewModel) {
        this.playlists = new ArrayList<>();
        this.viewModel = viewModel;
    }

    @NonNull
    @Override
    public FallbackPlaylistViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_holder_fallback_playlist, parent, false);
        return new FallbackPlaylistViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FallbackPlaylistViewHolder holder, int position) {
        if (position == positionSelected) {
            viewModel.setCurrentPlaylistAsFallBack(playlists.get(position));
        }
        holder.radioButton.setChecked(position == positionSelected);
        holder.updateUI(playlists.get(position));
    }

    @Override
    public int getItemCount() {
        return playlists.size();
    }

    public void setPlaylists(final List<PlaylistMetadata> playlists, final int positionSelected) {
        this.playlists = playlists;
        this.positionSelected = positionSelected;
    }

    public class FallbackPlaylistViewHolder extends RecyclerView.ViewHolder {
        private final RadioButton radioButton;
        private final TextView playlistName;
        private final TextView playlistNumTracks;

        public FallbackPlaylistViewHolder(@NonNull View itemView) {
            super(itemView);
            radioButton = itemView.findViewById(R.id.radio_button);
            playlistName = itemView.findViewById(R.id.playlist_title);
            playlistNumTracks = itemView.findViewById(R.id.playlist_num_tracks);
            // handle clicks
            itemView.setOnClickListener(this::handleItemSelected);
            radioButton.setOnClickListener(this::handleItemSelected);
        }

        public void updateUI(PlaylistMetadata playlistInfo) {
            playlistName.setText(playlistInfo.getName());
            playlistNumTracks.setText(String.format("%s tracks", playlistInfo.getTrackCount()));
        }

        private void handleItemSelected(final View view) {
            int copy = positionSelected;
            positionSelected = getBindingAdapterPosition();
            notifyItemChanged(copy);
            notifyItemChanged(positionSelected);
        }
    }
}
