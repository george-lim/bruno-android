package com.bruno.android.ui.fitnessdetails;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bruno.android.R;
import com.bruno.android.music.BrunoTrack;

import java.util.ArrayList;
import java.util.List;

public class FitnessDetailsPlaylistAdapter extends RecyclerView.Adapter<FitnessDetailsPlaylistViewHolder> {

    // MARK: - Private members

    private int[] trackColours;
    private List<BrunoTrack> data;

    // MARK: - Lifecycle methods

    public FitnessDetailsPlaylistAdapter(final int[] trackColours) {
        this.trackColours = trackColours;
        this.data = new ArrayList<>();
    }

    // MARK: - Public methods

    public void setData(final List<BrunoTrack> data) {
        this.data = data;
        notifyDataSetChanged();
    }

    // MARK: - RecyclerView.ViewHolder methods

    @NonNull
    @Override
    public FitnessDetailsPlaylistViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View viewHolderItemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.view_holder_fitness_details_playlist, parent, false);
        return new FitnessDetailsPlaylistViewHolder(viewHolderItemView);
    }

    @Override
    public void onBindViewHolder(@NonNull FitnessDetailsPlaylistViewHolder holder, int position) {
        holder.populate(trackColours[position % trackColours.length], data.get(position));
    }

    @Override
    public int getItemCount() {
        return data.size();
    }
}
