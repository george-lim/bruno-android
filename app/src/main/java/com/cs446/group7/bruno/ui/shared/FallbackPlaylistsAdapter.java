package com.cs446.group7.bruno.ui.shared;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.cs446.group7.bruno.R;
import com.cs446.group7.bruno.music.playlist.PlaylistInfo;

public class FallbackPlaylistsAdapter extends RecyclerView.Adapter<FallbackPlaylistsAdapter.FallbackPlaylistViewHolder> {

    private PlaylistInfo[] playlists;
    private int positionSelected = 0;

    public FallbackPlaylistsAdapter(final PlaylistInfo[] playlists) {
        this.playlists = playlists;
    }

    @NonNull
    @Override
    public FallbackPlaylistViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_holder_fallback_playlist, parent, false);
        return new FallbackPlaylistViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FallbackPlaylistViewHolder holder, int position) {
        holder.radioButton.setChecked(position == positionSelected);
    }

    @Override
    public int getItemCount() {
        return playlists.length;
    }

    public class FallbackPlaylistViewHolder extends RecyclerView.ViewHolder {
        private RadioButton radioButton;
        private TextView playlistName;
        private TextView playlistNumTracks;

        public FallbackPlaylistViewHolder(@NonNull View itemView) {
            super(itemView);
            radioButton = itemView.findViewById(R.id.radio_button);
            playlistName = itemView.findViewById(R.id.playlist_title);
            playlistNumTracks = itemView.findViewById(R.id.playlist_num_tracks);
            // handle clicks
            itemView.setOnClickListener(this::handleItemSelected);
            radioButton.setOnClickListener(this::handleItemSelected);
        }

        private void handleItemSelected(final View view) {
            int copy = positionSelected;
            positionSelected = getAdapterPosition();
            notifyItemChanged(copy);
            notifyItemChanged(positionSelected);
        }
    }
}
