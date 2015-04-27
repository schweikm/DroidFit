package marcschweikert.com.droidfit;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;

import marcschweikert.com.database.Account;
import marcschweikert.com.utils.DateUtils;

/**
 * Created by Marc on 4/26/2015.
 */
public class NewActivityExecuteBehavior extends DroidFitActivityExecuteBehavior {
    public void doOnExecute(final Bundle savedInstanceState, final Activity androidActivity, final Account account) {
        Log.i(getClass().getSimpleName(), "creating activity for " + account.getEmail());

        // UI references
        final Spinner typeSpinner = (Spinner) androidActivity.findViewById(R.id.new_activity_spinner);
        final DatePicker date = (DatePicker) androidActivity.findViewById(R.id.new_activity_date);
        final EditText distance = (EditText) androidActivity.findViewById(R.id.new_activity_distance);
        final TimePicker duration = (TimePicker) androidActivity.findViewById(R.id.new_activity_duration);

        // cannot be null
        final String activityType = typeSpinner.getSelectedItem().toString();
        final String activityDate = DateUtils.formatDate(date.getYear(), date.getMonth() + 1, date.getDayOfMonth());
        final String activityDuration = DateUtils.formatTime(duration.getCurrentHour(), duration.getCurrentMinute(), 0);

        // may be null
        Double activityDistance = 0.0;
        try {
            activityDistance = Double.parseDouble(distance.getText().toString());
        } catch (final Exception e) {
            Log.e(getClass().getSimpleName(), "Failed to parse distance for create activity " + e.getMessage());
            return;
        }

        // create the activity instance
        final DroidFitActivity activity = DroidFitActivityFactory.createActivityByName(androidActivity, activityType);
        if (null == activity) {
            Log.e(getClass().getSimpleName(), "Failed to create activity instance!");
        }

        // then set the other attributes
        activity.setDate(DateUtils.convertStringToCalendar(activityDate));
        activity.setDistance(activityDistance);
        activity.setDuration(DateUtils.convertStringToCalendar(activityDuration));

        Log.i(getClass().getSimpleName(), activity.toString());
/*
        final DatabaseFacade helper = new DatabaseFacade(getApplicationContext());
        final DroidFitActivity activity = new DroidFitActivityRunning(getApplicationContext(),
                new GregorianCalendar(2015, 01, 01, 12, 00, 00),
                13.1,
                new GregorianCalendar(1970, 01, 01, 01, 58, 21));
        helper.insertActivity(myAccount, activity);
*/
    }
}
