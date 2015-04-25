package marcschweikert.com.droidfit;

import android.content.Context;
import android.util.Log;

import marcschweikert.com.database.DatabaseHelper;

/**
 * Created by Marc on 4/24/2015.
 */
public class DroidFitActivityFactory {
    public static DroidFitActivity createActivity(final Context context, final Integer id, final Integer type_id) {
        // not sure of the best way to do this
        // static strings would be nice but the methods are abstract ...
        DroidFitActivity activity = null;

        // let's get the text of the id passed in
        final DatabaseHelper helper = new DatabaseHelper(context);
        final String activity_text = helper.getActivityTypeString(type_id);

        if (null == activity_text || activity_text.isEmpty()) {
            Log.e("DroidFitActivityFactory", "Retrieved null activity text for id " + type_id);
            return null;
        }

        // cycling?
        activity = new DroidFitActivityCycling(context);
        if (activity.getText().equals(activity_text)) {
            return activity;
        }

        // running?
        activity = new DroidFitActivityRunning(context);
        if (activity.getText().equals(activity_text)) {
            return activity;
        }

        // swimming?
        activity = new DroidFitActivitySwimming(context);
        if (activity.getText().equals(activity_text)) {
            return activity;
        }

        Log.e("DroidFitActivityFactory", "Could not find activity to create!");
        return null;
    }
}
