package marcschweikert.com.droidfit;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;

import marcschweikert.com.database.Account;
import marcschweikert.com.database.DatabaseFacade;
import marcschweikert.com.utils.DateUtils;

/**
 * Created by Marc on 4/26/2015.
 */
public class NewActivityExecuteBehavior extends DroidFitActivityExecuteBehavior {
    @Override
    public boolean doOnExecute(final Bundle savedInstanceState,
                               final Activity androidActivity,
                               final Account account,
                               final DroidFitActivity activity) {
        Log.i(getClass().getSimpleName(), "creating activity for " + account.getEmail());

        // UI references
        final Spinner typeSpinner = (Spinner) androidActivity.findViewById(R.id.activity_spinner);
        final DatePicker date = (DatePicker) androidActivity.findViewById(R.id.activity_date);
        final EditText distance = (EditText) androidActivity.findViewById(R.id.activity_distance);
        final TimePicker duration = (TimePicker) androidActivity.findViewById(R.id.activity_duration);

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
            distance.setError(androidActivity.getString(R.string.activity_error_no_distance));
            return false;
        }

        // create the activity instance
        final DroidFitActivity newActivity = DroidFitActivityFactory.createActivityByName(androidActivity, activityType);
        if (null == newActivity) {
            Log.e(getClass().getSimpleName(), "Failed to create activity instance!");
        }

        // then set the other attributes
        newActivity.setDate(DateUtils.convertStringToCalendar(activityDate));
        newActivity.setDistance(activityDistance);
        newActivity.setDuration(DateUtils.convertStringToCalendar(activityDuration));

        Log.d(getClass().getSimpleName(), "inserting activity for " + account.getEmail() + ":  " + newActivity.toString());

        final DatabaseFacade helper = new DatabaseFacade(androidActivity);
        return helper.insertActivity(account, newActivity);
    }
}
