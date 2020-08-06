package com.cs446.group7.bruno.ui.fitnessrecords;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.cs446.group7.bruno.R;

public class FitnessRecordsAdapter extends RecyclerView.Adapter<FitnessRecordsAdapter.FitnessRecordViewHolder> {
    @NonNull
    @Override
    public FitnessRecordViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_holder_fitness_record, parent, false);
        return new FitnessRecordViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FitnessRecordViewHolder holder, int position) {
        holder.itemView.setOnClickListener(view -> {
            Bundle bundle = new Bundle();
            bundle.putInt("recordIndex", position);
            NavController navController = Navigation.findNavController(view);
            navController.navigate(R.id.action_fragmenttoplevel_to_fragmentfitnessdetails, bundle);
        });

        // MOCK: - Populate holder with dummy data.
        Drawable walkingIcon = holder.itemView.getResources().getDrawable(R.drawable.ic_walking, null);
        int color = holder.itemView.getResources().getColor(R.color.colorSecondary, null);
        holder.icon.setImageDrawable(walkingIcon);
        holder.icon.setColorFilter(color);

        holder.datetime.setText("Aug 6 • 3:45 PM");
        holder.distance.setText("2.0 km");
        holder.duration.setText("17 min");
    }

    @Override
    public int getItemCount() {
        // MOCK: - Populate item count with dummy value.
        return 10;
    }

    public static class FitnessRecordViewHolder extends RecyclerView.ViewHolder {
        private ImageView icon;
        private TextView datetime;
        private TextView distance;
        private TextView duration;

        public FitnessRecordViewHolder(@NonNull View itemView) {
            super(itemView);
            icon = itemView.findViewById(R.id.record_icon);
            datetime = itemView.findViewById(R.id.record_datetime);
            distance = itemView.findViewById(R.id.record_distance);
            duration = itemView.findViewById(R.id.record_duration);
        }
    }
}
