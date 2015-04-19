package marcschweikert.com.droidfit;

import android.graphics.Bitmap;

import java.util.Calendar;

/**
 * Created by Marc on 4/18/2015.
 */
public class DroidFitActivity {
    private Bitmap myImage;
    private String myText;
    private Calendar myDate;
    private Double myDistance;
    private Calendar myDuration;

    public DroidFitActivity(final Bitmap image,
                            final String text,
                            final Calendar date,
                            final Double distance,
                            final Calendar duration) {
        myImage = image;
        myText = text;
        myDate = date;
        myDistance = distance;
        myDuration = duration;
    }

    public Bitmap getImage() {
        return myImage;
    }

    public String getText() {
        return myText;
    }

    public Calendar getDate() {
        return myDate;
    }

    public Double getDistance() {
        return myDistance;
    }

    public Calendar getDuration() {
        return myDuration;
    }
}
