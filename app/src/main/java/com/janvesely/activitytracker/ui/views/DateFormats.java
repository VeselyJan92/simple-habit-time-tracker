package com.janvesely.activitytracker.ui.views;

import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.TimeZone;

public class DateFormats {
    public static SimpleDateFormat fromSkeleton(String str, Locale locale) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(str, locale);
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        return simpleDateFormat;
    }

    public static SimpleDateFormat getBackupDateFormat() {
        return fromSkeleton("yyyy-MM-dd HHmmss", Locale.US);
    }

    public static SimpleDateFormat getCSVDateFormat() {
        return fromSkeleton("yyyy-MM-dd", Locale.US);
    }
}
