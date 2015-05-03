package marcschweikert.com.droidfit;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;

import java.util.List;

import marcschweikert.com.database.Account;
import marcschweikert.com.database.DatabaseFacade;

/**
 * Created by Marc on 4/24/2015.
 */
public class DroidFitActivityActivity extends Activity {
    private DroidFitActivityCreateBehavior myCreateBehavior;
    private DroidFitActivityExecuteBehavior myExecuteBehavior;
    private Account myAccount;
    private DroidFitActivity myActivity;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activity_layout);

        // get the account from the intent
        final Bundle bundle = getIntent().getExtras();
        myAccount = (Account) bundle.getSerializable("account");

        // get the activity from the intent
        myActivity = (DroidFitActivity) bundle.getSerializable("activity");

        // get the behaviors from the intent
        myCreateBehavior = (DroidFitActivityCreateBehavior) bundle.getSerializable("createBehavior");
        myExecuteBehavior = (DroidFitActivityExecuteBehavior) bundle.getSerializable("executeBehavior");


        //
        // common create setup
        //


        // populate the spinner
        final Spinner typeSpinner = (Spinner) findViewById(R.id.activity_spinner);
        final DatabaseFacade helper = new DatabaseFacade(getApplicationContext());
        final List<String> activityList = helper.getActivityTypes();
        final ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item, activityList);
        typeSpinner.setAdapter(adapter);

        // default to 0 distance
        final EditText distance = (EditText) findViewById(R.id.activity_distance);
        distance.setText("0.0");

        // remove am / pm from picker
        final TimePicker duration = (TimePicker) findViewById(R.id.activity_duration);
        duration.setIs24HourView(true);

        // also default to 00:00
        duration.setCurrentHour(0);
        duration.setCurrentMinute(0);

        // setup the "action" button
        final Button submitButton = (Button) findViewById(R.id.activity_submit_button);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                if (null != myExecuteBehavior) {
                    if (true == myExecuteBehavior.doOnExecute(bundle, DroidFitActivityActivity.this, myAccount, myActivity)) {
                        finish();
                    }
                }
            }
        });

        // setup the cancel button
        final Button cancelButton = (Button) findViewById(R.id.activity_cancel_button);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        //
        // behavior create setup
        //


        if (null != myCreateBehavior) {
            myCreateBehavior.doOnCreate(bundle, this, myAccount, myActivity);
        }

    }
}
