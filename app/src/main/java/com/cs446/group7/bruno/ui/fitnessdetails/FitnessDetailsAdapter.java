package com.cs446.group7.bruno.ui.fitnessdetails;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.cs446.group7.bruno.R;
import com.cs446.group7.bruno.music.BrunoTrack;

import java.util.List;

public class FitnessDetailsAdapter extends RecyclerView.Adapter<FitnessDetailsAdapter.FitnessDetailsViewHolder> {
    private List<BrunoTrack> tracklist;

    public FitnessDetailsAdapter(List<BrunoTrack> tracklist) {
        this.tracklist = tracklist;
    }

    @NonNull
    @Override
    public FitnessDetailsAdapter.FitnessDetailsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_holder_fitness_details, parent, false);
        return new FitnessDetailsAdapter.FitnessDetailsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FitnessDetailsAdapter.FitnessDetailsViewHolder holder, int position) {
        int[] colors = holder.itemView.getResources().getIntArray(R.array.colorRouteList);
        holder.icon.setColorFilter(colors[position % colors.length]);
        holder.songName.setText(tracklist.get(position).name);
        holder.artist.setText(tracklist.get(position).artists.toString());
    }

    @Override
    public int getItemCount() {
        return tracklist.size();
    }

    public static class FitnessDetailsViewHolder extends RecyclerView.ViewHolder {
        private ImageView icon;
        private TextView songName;
        private TextView artist;

        public FitnessDetailsViewHolder(@NonNull View itemView) {
            super(itemView);
            icon = itemView.findViewById(R.id.image_view_fitness_details_holder_music);
            songName = itemView.findViewById(R.id.text_view_fitness_details_holder_song);
            artist = itemView.findViewById(R.id.text_view_fitness_details_holder_artist);
        }
    }
}
