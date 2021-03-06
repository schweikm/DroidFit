package marcschweikert.com.droidfit;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.Calendar;

import marcschweikert.com.database.Account;

/**
 * Created by Marc on 4/30/2015.
 */
public class EditActivityCreateBehavior extends DroidFitActivityCreateBehavior {
    @Override
    public boolean doOnCreate(final Bundle savedInstanceState,
                              final Activity androidActivity,
                              final Account account,
                              final DroidFitActivity activity) {
        // UI references
        final Spinner typeSpinner = (Spinner) androidActivity.findViewById(R.id.activity_spinner);
        final DatePicker date = (DatePicker) androidActivity.findViewById(R.id.activity_date);
        final EditText distance = (EditText) androidActivity.findViewById(R.id.activity_distance);
        final TimePicker duration = (TimePicker) androidActivity.findViewById(R.id.activity_duration);

        // we can't change the activity type
        typeSpinner.setEnabled(false);

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
