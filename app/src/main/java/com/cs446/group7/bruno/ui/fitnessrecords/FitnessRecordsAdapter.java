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
import com.cs446.group7.bruno.persistence.FitnessRecordData;
import com.cs446.group7.bruno.utils.TimeUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class FitnessRecordsAdapter extends RecyclerView.Adapter<FitnessRecordsAdapter.FitnessRecordsViewHolder> {

    // MARK: - Private members

    private List<FitnessRecordData> data;
    private Locale locale;

    // MARK: - ViewHolder class

    public static class FitnessRecordsViewHolder extends RecyclerView.ViewHolder {
        private ImageView icon;
        private TextView datetime;
        private TextView distance;
        private TextView duration;

        public FitnessRecordsViewHolder(@NonNull View itemView) {
            super(itemView);
            icon = itemView.findViewById(R.id.record_icon);
            datetime = itemView.findViewById(R.id.record_datetime);
            distance = itemView.findViewById(R.id.record_distance);
            duration = itemView.findViewById(R.id.record_duration);
        }

        public void populate(final FitnessRecordData fitnessRecordData, final Locale locale) {
            if (fitnessRecordData.isRun()) {
                Drawable runningIcon = itemView.getResources().getDrawable(R.drawable.ic_running, null);
                int color = itemView.getResources().getColor(R.color.colorPrimary, null);
                icon.setImageDrawable(runningIcon);
                icon.setColorFilter(color);
            } else {
                Drawable walkingIcon = itemView.getResources().getDrawable(R.drawable.ic_walking, null);
                int color = itemView.getResources().getColor(R.color.colorSecondary, null);
                icon.setImageDrawable(walkingIcon);
                icon.setColorFilter(color);
            }

            double distanceKilometer = fitnessRecordData.getRouteDistance() / 1000d;
            long durationSeconds = Math.round(fitnessRecordData.getUserDuration() / 1000d);
            String dateTimeText = TimeUtils.formatDateTime(
                    fitnessRecordData.getStartTime(),
                    TimeUtils.DATE_TIME_FORMAT,
                    locale
            );

            datetime.setText(dateTimeText);
            distance.setText(String.format(locale,"%.1f km", distanceKilometer));
            duration.setText(TimeUtils.formatDuration(durationSeconds));
        }
    }

    // MARK: - Lifecycle methods

    public FitnessRecordsAdapter() {
        this.data = new ArrayList<>();
        this.locale = null;
    }

    // MARK: - Public methods

    public void setData(final List<FitnessRecordData> data) {
        this.data = data;
        notifyDataSetChanged();
    }

    public void setLocale(final Locale locale) {
        this.locale = locale;
    }

    // MARK: - RecyclerView.ViewHolder methods

    @NonNull
    @Override
    public FitnessRecordsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.view_holder_fitness_record, parent, false);
        return new FitnessRecordsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FitnessRecordsViewHolder holder, int position) {
        holder.populate(data.get(position), locale);
        holder.itemView.setOnClickListener(view -> {
            Bundle bundle = new Bundle();
            bundle.putInt("recordIndex", position);
            NavController navController = Navigation.findNavController(view);
            navController.navigate(R.id.action_fragmenttoplevel_to_fragmentfitnessdetails, bundle);
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }
}
