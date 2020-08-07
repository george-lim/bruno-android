package com.cs446.group7.bruno.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class TimeUtils {
    // Format for the date time in the UI
    public static final String DATE_TIME_FORMAT = "MMM d • h:mm aa";

    public static String getDurationString(long seconds) {
        long hours = seconds / 3600;
        long minutes = (seconds % 3600) / 60;
        seconds = seconds % 60;

        if (hours > 0) {
            return hours + ":" + twoDigitString(minutes) + ":" + twoDigitString(seconds);
        } else {
            return twoDigitString(minutes) + ":" + twoDigitString(seconds);
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

    public static String formatDateTime(final Date dateTime, final String format, final Locale locale) {
        final SimpleDateFormat dateFormat = new SimpleDateFormat(format, locale);
        return dateFormat.format(dateTime);
    }

    private static String twoDigitString(long number) {
        if (number == 0) {
            return "00";
        }

        if (number / 10 == 0) {
            return "0" + number;
        }

        return String.valueOf(number);
    }
}
