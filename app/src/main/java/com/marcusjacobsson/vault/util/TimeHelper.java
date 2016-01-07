package com.marcusjacobsson.vault.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

/**
 * Created by Marcus Jacobsson on 2015-10-11.
 */
public class TimeHelper {

    public static String makeTimeString(long milliseconds) {

        SimpleDateFormat sdfToday = new SimpleDateFormat("HH:mm", Locale.getDefault());
        SimpleDateFormat sdfMoreThanOneWeekAgo = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String timeString;
        boolean isToday, isThisWeek;

        isToday = checkIfToday(milliseconds);
        isThisWeek = checkIfThisWeek(milliseconds);

        if (isToday) {
            timeString = sdfToday.format(new Date(milliseconds));
        } else if (isThisWeek) {
            timeString = dayStringFormat(milliseconds);
        } else {
            timeString = sdfMoreThanOneWeekAgo.format(new Date(milliseconds));
        }

        return timeString;
    }

    private static boolean checkIfToday(long then) {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        return then > c.getTimeInMillis();
    }

    private static boolean checkIfThisWeek(long milliseconds) {

        Calendar oneWeekAgo = Calendar.getInstance();
        oneWeekAgo.add(Calendar.DATE, -7);
        Date oneWeekAgoDate = oneWeekAgo.getTime();

        Date date = new Date(milliseconds);

        return date.after(oneWeekAgoDate);
    }

    private static String dayStringFormat(long milliseconds) {
        GregorianCalendar cal = new GregorianCalendar();

        cal.setTime(new Date(milliseconds));

        int dow = cal.get(Calendar.DAY_OF_WEEK);

        switch (dow) {
            case Calendar.MONDAY:
                return "Monday";
            case Calendar.TUESDAY:
                return "Tuesday";
            case Calendar.WEDNESDAY:
                return "Wednesday";
            case Calendar.THURSDAY:
                return "Thursday";
            case Calendar.FRIDAY:
                return "Friday";
            case Calendar.SATURDAY:
                return "Saturday";
            case Calendar.SUNDAY:
                return "Sunday";
        }

        return "Unknown";
    }

}
