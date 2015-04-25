package marcschweikert.com.droidfit;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.util.Calendar;

/**
 * Created by Marc on 4/23/2015.
 */
public class DroidFitActivitySwimming extends DroidFitActivity {
    public DroidFitActivitySwimming(final Context context) {
        super(context);
    }

    public DroidFitActivitySwimming(final Context context,
                                    final Calendar date,
                                    final Double distance,
                                    final Calendar duration) {
        super(context, date, distance, duration);
    }

    @Override
    public String getText() {
        return getContext().getResources().getString(R.string.activity_type_swimming);
    }

    @Override
    public Bitmap getImage() {
        return BitmapFactory.decodeResource(getContext().getResources(), R.drawable.swimming_logo);
    }
}
