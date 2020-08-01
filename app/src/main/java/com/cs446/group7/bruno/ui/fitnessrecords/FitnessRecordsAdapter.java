package com.cs446.group7.bruno.ui.fitnessrecords;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.cs446.group7.bruno.R;
import com.cs446.group7.bruno.dao.FitnessDetailsDAO;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

public class FitnessRecordsAdapter extends RecyclerView.Adapter<FitnessRecordsAdapter.FitnessRecordViewHolder> {
    private List<FitnessDetailsDAO> data;
    private Locale locale;

    public FitnessRecordsAdapter(final List<FitnessDetailsDAO> data, final Locale locale) {
        this.data = data;
        this.locale = locale;
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
            Bundle bundle = new Bundle();
            bundle.putInt("recordIndex", position);
            NavController navController = Navigation.findNavController(view);
            navController.navigate(R.id.action_fragmenttoplevel_to_fragmentfitnessdetails, bundle);
        });

        final FitnessDetailsDAO fitnessData = data.get(position);

        if (fitnessData.isRun()) {
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

        final long userDurationMinutes = Math.round(fitnessData.getUserDuration() / 1000d / 60);
        final double distanceKilometer = fitnessData.getRouteDistance() / 1000d;

        final String pattern = "MMM d • h:mm aa";
        final SimpleDateFormat dateFormat = new SimpleDateFormat(pattern, locale);
        final Date startTime = fitnessData.getStartTime();

        holder.datetime.setText(dateFormat.format(startTime));
        holder.distance.setText(String.format(locale,"%.1f km", distanceKilometer));

        if (userDurationMinutes < 60) {
            holder.duration.setText(String.format(locale, "%s min", userDurationMinutes));
        } else {
            holder.duration.setText(String.format(locale, "%.1f hours", (double)userDurationMinutes / 60));
        }
    }

    @Override
    public int getItemCount() {
        return data.size();
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
