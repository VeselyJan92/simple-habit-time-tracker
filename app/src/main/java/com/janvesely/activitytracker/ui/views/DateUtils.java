package com.janvesely.activitytracker.ui.views;

import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;
public abstract class DateUtils {
    public static final long DAY_LENGTH = 86400000;
    private static Long fixedLocalTime;
    private static Locale fixedLocale;
    private static TimeZone fixedTimeZone;



    public enum TruncateField {
        MONTH,
        WEEK_NUMBER,
        YEAR,
        QUARTER
    }

    public static long applyTimezone(long j) {
        TimeZone timezone = getTimezone();
        return j - ((long) timezone.getOffset(j - ((long) timezone.getOffset(j))));
    }

    public static String formatHeaderDate(GregorianCalendar gregorianCalendar) {
        Locale locale = getLocale();
        String num = Integer.toString(gregorianCalendar.get(5));
        String displayName = gregorianCalendar.getDisplayName(7, 1, locale);
        StringBuilder sb = new StringBuilder();
        sb.append(displayName);
        sb.append("\n");
        sb.append(num);
        return sb.toString();
    }

    private static GregorianCalendar getCalendar(long j) {
        GregorianCalendar gregorianCalendar = new GregorianCalendar(TimeZone.getTimeZone("GMT"), getLocale());
        gregorianCalendar.setTimeInMillis(j);
        return gregorianCalendar;
    }

    public static long getLocalTime() {
        Long l = fixedLocalTime;
        if (l != null) {
            return l.longValue();
        }
        TimeZone timezone = getTimezone();
        long time = new Date().getTime();
        return time + ((long) timezone.getOffset(time));
    }

    private static String[] getWeekdayNames(int i, int i2) {
        String[] strArr = new String[7];
        GregorianCalendar gregorianCalendar = new GregorianCalendar();
        gregorianCalendar.set(7, i2);
        for (int i3 = 0; i3 < strArr.length; i3++) {
            strArr[i3] = gregorianCalendar.getDisplayName(7, i, getLocale());
            gregorianCalendar.add(5, 1);
        }
        return strArr;
    }

    public static int[] getWeekdaySequence(int i) {
        return new int[]{((i - 1) % 7) + 1, (i % 7) + 1, ((i + 1) % 7) + 1, ((i + 2) % 7) + 1, ((i + 3) % 7) + 1, ((i + 4) % 7) + 1, ((i + 5) % 7) + 1};
    }

    public static int getFirstWeekdayNumberAccordingToLocale() {
        return new GregorianCalendar().getFirstDayOfWeek();
    }

    public static String[] getLongWeekdayNames(int i) {
        return getWeekdayNames(2, i);
    }

    public static String[] getShortWeekdayNames(int i) {
        return getWeekdayNames(1, i);
    }

    public static Timestamp getToday() {
        return new Timestamp(getStartOfToday());
    }

    public static long getStartOfDay(long j) {
        return (j / 86400000) * 86400000;
    }

    public static long getStartOfToday() {
        return getStartOfDay(getLocalTime());
    }

    public static long millisecondsUntilTomorrow() {
        return (getStartOfToday() + 86400000) - getLocalTime();
    }

    public static GregorianCalendar getStartOfTodayCalendar() {
        return getCalendar(getStartOfToday());
    }

    private static TimeZone getTimezone() {
        TimeZone timeZone = fixedTimeZone;
        if (timeZone != null) {
            return timeZone;
        }
        return TimeZone.getDefault();
    }

    public static void setFixedTimeZone(TimeZone timeZone) {
        fixedTimeZone = timeZone;
    }

    public static long removeTimezone(long j) {
        return j + ((long) getTimezone().getOffset(j));
    }

    public static void setFixedLocalTime(Long l) {
        fixedLocalTime = l;
    }

    public static void setFixedLocale(Locale locale) {
        fixedLocale = locale;
    }

    private static Locale getLocale() {
        Locale locale = fixedLocale;
        if (locale != null) {
            return locale;
        }
        return Locale.getDefault();
    }



    public static long getUpcomingTimeInMillis(int i, int i2) {
        GregorianCalendar startOfTodayCalendar = getStartOfTodayCalendar();
        startOfTodayCalendar.set(11, i);
        startOfTodayCalendar.set(12, i2);
        startOfTodayCalendar.set(13, 0);
        long timeInMillis = startOfTodayCalendar.getTimeInMillis();
        if (getLocalTime() > timeInMillis) {
            timeInMillis += 86400000;
        }
        return applyTimezone(timeInMillis);
    }
}
