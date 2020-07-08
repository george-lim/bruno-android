package com.cs446.group7.bruno.ui.fitnessrecords;

import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.cs446.group7.bruno.R;

public class FitnessRecordsAdapter extends RecyclerView.Adapter<FitnessRecordsAdapter.FitnessRecordViewHolder> {
    private int[] data;

    public FitnessRecordsAdapter(final int[] data) {
        this.data = data;
    }

    @NonNull
    @Override
    public FitnessRecordViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_holder_fitness_record, parent, false);
        return new FitnessRecordViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FitnessRecordViewHolder holder, int position) {
        holder.itemView.setOnClickListener(view -> {
            NavController navController = Navigation.findNavController(view);
            navController.navigate(R.id.action_fragmenttoplevel_to_fragmentfitnessdetails);
        });
        if (data[position] == 1) {
            Drawable runningIcon = holder.itemView.getResources().getDrawable(R.drawable.ic_running, null);
            int color = holder.itemView.getResources().getColor(R.color.colorPrimary, null);
            holder.icon.setImageDrawable(runningIcon);
            holder.icon.setColorFilter(color);
        } else {
            Drawable walkingIcon = holder.itemView.getResources().getDrawable(R.drawable.ic_walking, null);
            int color = holder.itemView.getResources().getColor(R.color.colorSecondary, null);
            holder.icon.setImageDrawable(walkingIcon);
            holder.icon.setColorFilter(color);
        }
    }

    @Override
    public int getItemCount() {
        return data.length;
    }

    public static class FitnessRecordViewHolder extends RecyclerView.ViewHolder {

        private ImageView icon;

        public FitnessRecordViewHolder(@NonNull View itemView) {
            super(itemView);
            icon = itemView.findViewById(R.id.record_icon);
        }
    }
}
