package marcschweikert.com.droidfit;

import android.app.Activity;
import android.os.Bundle;

/**
 * Created by Marc on 4/26/2015.
 */
public class NewActivityCreateBehavior extends DroidFitActivityCreateBehavior {
    @Override
    public boolean doOnCreate(final Bundle savedInstanceState, final Activity androidActivity) {
        // no special setup needed
        return true;
    }
}
