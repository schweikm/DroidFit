package marcschweikert.com.droidfit;

import android.content.Context;
import android.util.Log;

import marcschweikert.com.database.DatabaseFacade;

/**
 * Created by Marc on 4/24/2015.
 */
public class DroidFitActivityFactory {
    public static DroidFitActivity createActivityByID(final Context context, final Integer type_id) {
        DroidFitActivity activity = null;

        // let's get the text of the id passed in
        final DatabaseFacade helper = new DatabaseFacade(context);
        final String activity_text = helper.getActivityTypeString(type_id);

        if (null == activity_text || activity_text.isEmpty()) {
            Log.e("DroidFitActivityFactory", "Retrieved null activity text for id " + type_id);
            return null;
        }

        // now delegate to the other factory method
        return createActivityByName(context, activity_text);
    }

    public static DroidFitActivity createActivityByName(final Context context, final String type_str) {
        DroidFitActivity activity = null;

        if (context.getResources().getString(R.string.activity_type_cycling).equals(type_str)) {
            activity = new DroidFitActivityCycling();
        } else if (context.getResources().getString(R.string.activity_type_running).equals(type_str)) {
            activity = new DroidFitActivityRunning();
        } else if (context.getResources().getString(R.string.activity_type_swimming).equals(type_str)) {
            activity = new DroidFitActivityRunning();
        } else {
            Log.e("DroidFitActivityFactory", "Could not find activity to create!");
        }

        return activity;
    }
}
