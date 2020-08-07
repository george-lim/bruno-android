package com.cs446.group7.bruno.utils;

import android.content.res.Resources;
import android.os.Build;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DateTimeUtils {
    // Default date time format
    private static final String DATE_TIME_FORMAT = "MMM d • h:mm aa";

    @SuppressWarnings("deprecation")
    public static Locale getLocale(final Resources resources) {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.N
                ? resources.getConfiguration().getLocales().get(0)
                : resources.getConfiguration().locale;
    }

    public static String getDurationString(long seconds) {
        long hours = seconds / 3600;
        long minutes = (seconds % 3600) / 60;
        seconds = seconds % 60;

        if (hours > 0) {
            return hours + ":" + addTwoDigitPadding(minutes) + ":" + addTwoDigitPadding(seconds);
        } else {
            return addTwoDigitPadding(minutes) + ":" + addTwoDigitPadding(seconds);
        }
    }

    public static String formatDuration(final long seconds) {
        if (seconds < 60) {
            return String.format(Locale.getDefault(), "%s sec", seconds);
        }

        final long minutes = seconds / 60;

        if (minutes < 60) {
            return String.format(Locale.getDefault(), "%s min", minutes);
        }

        return String.format(Locale.getDefault(), "%.1f hr", (float)minutes / 60);
    }

    public static String formatDateTime(final Date dateTime, final Locale locale) {
        final SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_TIME_FORMAT, locale);
        return dateFormat.format(dateTime);
    }

    private static String addTwoDigitPadding(long number) {
        if (number == 0) {
            return "00";
        }

        if (number / 10 == 0) {
            return "0" + number;
        }

        return String.valueOf(number);
    }
}
