package com.cs446.group7.bruno.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class TimeUtils {
    public static String getDurationString(int seconds) {
        int hours = seconds / 3600;
        int minutes = (seconds % 3600) / 60;
        seconds = seconds % 60;

        if (hours > 0) {
            return hours + ":" + twoDigitString(minutes) + ":" + twoDigitString(seconds);
        } else {
            return twoDigitString(minutes) + ":" + twoDigitString(seconds);
        }
    }

    public static String formatDateTime(final Date dateTime, final String format, final Locale locale) {
        final SimpleDateFormat dateFormat = new SimpleDateFormat(format, locale);
        return dateFormat.format(dateTime);
    }

    private static String twoDigitString(int number) {
        if (number == 0) {
            return "00";
        }

        if (number / 10 == 0) {
            return "0" + number;
        }

        return String.valueOf(number);
    }
}
