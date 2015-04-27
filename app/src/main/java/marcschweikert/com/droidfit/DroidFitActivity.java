package marcschweikert.com.droidfit;

import android.content.Context;
import android.graphics.Bitmap;

import java.text.DecimalFormat;
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

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append("\n--------------------\n");
        builder.append("Type:  " + getText() + "\n");

        if (null != myDate) {
            builder.append("Date:  " + DateUtils.formatDateTime(myDate) + "\n");
        }

        if (null != myDistance) {
            final DecimalFormat format = new DecimalFormat("0.00");
            builder.append("Dist:  " + format.format(myDistance) + "\n");
        }

        if (null != myDuration) {
            builder.append("Dur :  " + DateUtils.formatTime(myDuration) + "\n");
        }

        builder.append("--------------------\n");

        return builder.toString();
    }

    // abstract class methods
    public abstract String getText();

    public abstract Bitmap getImage();

    // provide access to application context to subclasses
    protected Context getContext() {
        return myContext;
    }

    public Calendar getDate() {
        return myDate;
    }

    public void setDate(final Calendar date) {
        myDate = date;
    }

    public Double getDistance() {
        return myDistance;
    }

    public void setDistance(final Double distance) {
        myDistance = distance;
    }

    public Calendar getDuration() {
        return myDuration;
    }

    public void setDuration(final Calendar duration) {
        myDuration = duration;
    }
}
