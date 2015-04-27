package marcschweikert.com.utils;

import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;

/**
 * Created by Marc on 4/22/2015.
 */
public class DateUtils {
    public static String formatDate(final Integer year, final Integer month, final Integer day) {
        return formatDateTime(year, month, day, 0, 0, 0);
    }

    public static String formatTime(final Integer hours, final Integer minutes, final Integer seconds) {
        return formatDateTime(0, 0, 0, hours, minutes, seconds);
    }

    public static String formatTime(final Calendar calendar) {
        return formatDateTime(calendar);
    }

    public static String formatDateTime(final Calendar calendar) {
        return formatDateTime(calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH) + 1,       // 0 based!!
                calendar.get(Calendar.DAY_OF_MONTH),
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                calendar.get(Calendar.SECOND));
    }

    public static String formatDateTime(final Integer year, final Integer month, final Integer day, final Integer hours, final Integer minutes, final Integer seconds) {
        if (null == year || null == month || null == day || null == hours || null == minutes || null == seconds) {
            Log.e("DateUtils", "year, month, day, hours, minutes, or seconds is null");
            return null;
        }

        final String formattedDate = String.format("%04d-%02d-%02dT%02d:%02d:%02d", year, month, day, hours, minutes, seconds);
        return formattedDate;
    }

    public static Calendar convertStringToCalendar(final String dateStr) {
        if (null == dateStr) {
            Log.e("DateUtils", "date string is null");
            return null;
        }

        final SimpleDateFormat myFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US);
        Calendar cal = null;
        try {
            cal = new GregorianCalendar();
            cal.setTime(myFormatter.parse(dateStr));
        } catch (final Exception e) {
            Log.e("DateUtils", "Failed to convert " + dateStr + " to Calendar");
        }

        return cal;
    }
}
