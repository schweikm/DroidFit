package marcschweikert.com.droidfit;

import android.content.Context;
import android.graphics.Bitmap;

import java.util.Calendar;

import marcschweikert.com.utils.DateUtils;

/**
 * Created by Marc on 4/18/2015.
 */
public abstract class DroidFitActivity {
    private Context myContext;
    private Calendar myDate;
    private Double myDistance;
    private Calendar myDuration;

    public DroidFitActivity(final Context context) {
        myContext = context;
    }

    public DroidFitActivity(final Context context,
                            final Calendar date,
                            final Double distance,
                            final Calendar duration) {
        myContext = context;
        myDate = date;
        myDistance = distance;
        myDuration = duration;
    }

    // abstract class methods
    public abstract String getText();

    public abstract Bitmap getImage();

    // provide access to application context to subclasses
    protected Context getContext() {
        return myContext;
    }

    public void setDate(final Calendar date) {
        myDate = date;
    }

    public Calendar getDate() {
        return myDate;
    }

    public void setDate(final String date) {
        myDate = DateUtils.convertStringToCalendar(date);
    }

    public Double getDistance() {
        return myDistance;
    }

    public void setDistance(final Double distance) {
        myDistance = distance;
    }

    public void setDuration(final Calendar duration) {
        myDuration = duration;
    }

    public Calendar getDuration() {
        return myDuration;
    }

    public void setDuration(final String duration) {
        myDuration = DateUtils.convertStringToCalendar(duration);
    }
}
