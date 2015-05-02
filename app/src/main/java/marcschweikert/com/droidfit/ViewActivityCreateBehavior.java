package marcschweikert.com.droidfit;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.Calendar;

import marcschweikert.com.database.DatabaseFacade;

/**
 * Created by Marc on 4/30/2015.
 */
public class ViewActivityCreateBehavior extends DroidFitActivityCreateBehavior {
    @Override
    public boolean doOnCreate(final Bundle savedInstanceState, final Activity androidActivity) {
        // UI references
        final Spinner typeSpinner = (Spinner) androidActivity.findViewById(R.id.activity_spinner);
        final DatePicker date = (DatePicker) androidActivity.findViewById(R.id.activity_date);
        final EditText distance = (EditText) androidActivity.findViewById(R.id.activity_distance);
        final TimePicker duration = (TimePicker) androidActivity.findViewById(R.id.activity_duration);
        final Button submitButton = (Button) androidActivity.findViewById(R.id.activity_submit_button);

        // don't allow the user to edit fields
        typeSpinner.setEnabled(false);
        date.setEnabled(false);
        distance.setEnabled(false);
        duration.setEnabled(false);

        // or try to make changes
        submitButton.setEnabled(false);

        // get the activity ID from the intent
        final Bundle bundle = androidActivity.getIntent().getExtras();
        final Integer activity_id = bundle.getInt("activityID");

        // get the activity from the database
        final DatabaseFacade helper = new DatabaseFacade(androidActivity);
        final DroidFitActivity activity = helper.getActivityByID(activity_id);

        Log.d(getClass().getSimpleName(), "populating activity for view:  " + activity);

        // now set the UI fields with the data from the activity
        typeSpinner.setSelection(((ArrayAdapter<String>) typeSpinner.getAdapter()).getPosition(activity.getText()));
        date.updateDate(activity.getDate().get(Calendar.YEAR), activity.getDate().get(Calendar.MONTH), activity.getDate().get(Calendar.DAY_OF_MONTH));
        distance.setText(activity.getDistance().toString(), TextView.BufferType.NORMAL);
        duration.setCurrentHour(activity.getDuration().get(Calendar.HOUR_OF_DAY));
        duration.setCurrentMinute(activity.getDuration().get(Calendar.MINUTE));

        return true;
    }
}
