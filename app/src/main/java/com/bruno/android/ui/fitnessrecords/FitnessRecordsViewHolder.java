package com.bruno.android.ui.fitnessrecords;

import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bruno.android.R;
import com.bruno.android.models.RouteModel;
import com.bruno.android.persistence.FitnessRecord;
import com.bruno.android.utils.DateTimeUtils;

import java.util.Locale;

public class FitnessRecordsViewHolder extends RecyclerView.ViewHolder {
    private final ImageView icon;
    private final TextView datetime;
    private final TextView distance;
    private final TextView duration;

    public FitnessRecordsViewHolder(@NonNull View itemView) {
        super(itemView);
        icon = itemView.findViewById(R.id.record_icon);
        datetime = itemView.findViewById(R.id.record_datetime);
        distance = itemView.findViewById(R.id.record_distance);
        duration = itemView.findViewById(R.id.record_duration);
    }

    public void populate(final FitnessRecord fitnessRecord, final Locale locale) {
        if (fitnessRecord.getMode() == RouteModel.Mode.WALK) {

            Drawable walkingIcon = ResourcesCompat.getDrawable(itemView.getResources(), R.drawable.ic_walking, null);
            int color = itemView.getResources().getColor(R.color.colorSecondary, null);
            icon.setImageDrawable(walkingIcon);
            icon.setColorFilter(color);
        } else {
            Drawable runningIcon = ResourcesCompat.getDrawable(itemView.getResources(), R.drawable.ic_running, null);
            int color = itemView.getResources().getColor(R.color.colorPrimary, null);
            icon.setImageDrawable(runningIcon);
            icon.setColorFilter(color);
        }

        double distanceKilometer = fitnessRecord.getRouteDistance() / 1000d;
        long durationSeconds = Math.round(fitnessRecord.getUserDuration() / 1000d);
        String dateTimeText = DateTimeUtils.formatDateTime(fitnessRecord.getStartTime(), locale);

        datetime.setText(dateTimeText);
        distance.setText(String.format(locale, "%.1f km", distanceKilometer));
        duration.setText(DateTimeUtils.formatDuration(durationSeconds));
    }
}
