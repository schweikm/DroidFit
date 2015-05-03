package marcschweikert.com.droidfit;

import java.util.Calendar;

/**
 * Created by Marc on 4/23/2015.
 */
public class DroidFitActivitySwimming extends DroidFitActivity {
    public DroidFitActivitySwimming() {
        super();
    }

    public DroidFitActivitySwimming(final Calendar date,
                                    final Double distance,
                                    final Calendar duration) {
        super(date, distance, duration);
    }

    @Override
    public String getText() {
        return "Swimming";
    }
}
