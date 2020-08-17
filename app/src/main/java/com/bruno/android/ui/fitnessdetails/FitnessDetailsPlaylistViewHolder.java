package com.bruno.android.ui.fitnessdetails;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bruno.android.R;
import com.bruno.android.music.BrunoTrack;

public class FitnessDetailsPlaylistViewHolder extends RecyclerView.ViewHolder {

    // MARK: - Private members

    private ImageView trackIcon;
    private TextView trackName;
    private TextView trackArtists;

    // MARK: - Lifecycle methods

    public FitnessDetailsPlaylistViewHolder(@NonNull View itemView) {
        super(itemView);
        trackIcon = itemView.findViewById(R.id.fitness_details_playlist_track_icon);
        trackName = itemView.findViewById(R.id.fitness_details_playlist_track_name);
        trackArtists = itemView.findViewById(R.id.fitness_details_playlist_track_artists);
    }

    // MARK: - Public methods

    public void populate(int musicNoteColour, final BrunoTrack track) {
        trackIcon.setColorFilter(musicNoteColour);
        trackName.setText(track.getName());
        trackArtists.setText(track.getArtists());
    }
}
