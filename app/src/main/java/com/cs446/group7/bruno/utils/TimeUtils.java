package com.cs446.group7.bruno.utils;

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
