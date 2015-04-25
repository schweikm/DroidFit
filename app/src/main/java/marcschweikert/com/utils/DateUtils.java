package marcschweikert.com.utils;

import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * Created by Marc on 4/22/2015.
 */
public class DateUtils {
    private static final SimpleDateFormat myFormatter = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy", Locale.US);

    public static Calendar convertStringToCalendar(final String dateStr) {
        Calendar cal = null;
        try {
            cal = Calendar.getInstance();
            cal.setTime(myFormatter.parse(dateStr));
        } catch (final Exception e) {
            Log.e("DateUtils", "Failed to convert " + dateStr + " to Calendar");
        }

        return cal;
    }

    public static String converCalendarToString(final Calendar cal) {
        return myFormatter.format(cal.getTime());
    }
}
