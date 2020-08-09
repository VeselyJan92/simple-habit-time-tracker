package com.janvesely.activitytracker.ui.views;

import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

public final class Timestamp {
    public static final long DAY_LENGTH = 86400000;
    public static final Timestamp ZERO = new Timestamp(0);
    private final long unixTime;

    public Timestamp(long j) {
        if (j >= 0) {
            if (j % 86400000 != 0) {
                j = (j / 86400000) * 86400000;
            }
            this.unixTime = j;
            return;
        }
        StringBuilder sb = new StringBuilder();
        sb.append("Invalid unix time: ");
        sb.append(j);
        throw new IllegalArgumentException(sb.toString());
    }

    public Timestamp(GregorianCalendar gregorianCalendar) {
        this(gregorianCalendar.getTimeInMillis());
    }

    public long getUnixTime() {
        return this.unixTime;
    }

    public int compare(Timestamp timestamp) {
        return Long.signum(this.unixTime - timestamp.unixTime);
    }


    public static Timestamp oldest(Timestamp timestamp, Timestamp timestamp2) {
        return timestamp.unixTime < timestamp2.unixTime ? timestamp : timestamp2;
    }

    public Timestamp minus(int i) {
        return plus(-i);
    }

    public Timestamp plus(int i) {
        return new Timestamp(this.unixTime + (((long) i) * 86400000));
    }

    public int daysUntil(Timestamp timestamp) {
        return (int) ((timestamp.unixTime - this.unixTime) / 86400000);
    }

    public boolean isNewerThan(Timestamp timestamp) {
        return compare(timestamp) > 0;
    }

    public boolean isOlderThan(Timestamp timestamp) {
        return compare(timestamp) < 0;
    }

    public Date toJavaDate() {
        return new Date(this.unixTime);
    }

    public GregorianCalendar toCalendar() {
        GregorianCalendar gregorianCalendar = new GregorianCalendar(TimeZone.getTimeZone("GMT"));
        gregorianCalendar.setTimeInMillis(this.unixTime);
        return gregorianCalendar;
    }

    public String toString() {
        return DateFormats.getCSVDateFormat().format(new Date(this.unixTime));
    }

    public int getWeekday() {
        return toCalendar().get(7) % 7;
    }

}
