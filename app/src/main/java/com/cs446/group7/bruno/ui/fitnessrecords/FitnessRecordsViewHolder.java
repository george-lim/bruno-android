package com.cs446.group7.bruno.ui.fitnessrecords;

import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.cs446.group7.bruno.R;
import com.cs446.group7.bruno.models.RouteModel;
import com.cs446.group7.bruno.persistence.FitnessRecord;
import com.cs446.group7.bruno.utils.DateTimeUtils;

import java.util.Locale;

public class FitnessRecordsViewHolder extends RecyclerView.ViewHolder {
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

    public void populate(final FitnessRecord fitnessRecord, final Locale locale) {
        if (fitnessRecord.getMode() == RouteModel.Mode.WALK) {
            Drawable walkingIcon = itemView.getResources().getDrawable(R.drawable.ic_walking, null);
            int color = itemView.getResources().getColor(R.color.colorSecondary, null);
            icon.setImageDrawable(walkingIcon);
            icon.setColorFilter(color);
        }
        else {
            Drawable runningIcon = itemView.getResources().getDrawable(R.drawable.ic_running, null);
            int color = itemView.getResources().getColor(R.color.colorPrimary, null);
            icon.setImageDrawable(runningIcon);
            icon.setColorFilter(color);
        }

        double distanceKilometer = fitnessRecord.getRouteDistance() / 1000d;
        long durationSeconds = Math.round(fitnessRecord.getUserDuration() / 1000d);
        String dateTimeText = DateTimeUtils.formatDateTime(fitnessRecord.getStartTime(), locale);

        datetime.setText(dateTimeText);
        distance.setText(String.format(locale,"%.1f km", distanceKilometer));
        duration.setText(DateTimeUtils.formatDuration(durationSeconds));
    }
}
