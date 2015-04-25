package marcschweikert.com.droidfit;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import marcschweikert.com.database.Account;

/**
 * Created by Marc on 4/24/2015.
 */
public class NewActivityActivity extends Activity {
    private Account myAccount;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_activity);

        // get the account from the intent
        final Bundle bundle = getIntent().getExtras();
        myAccount = (Account) bundle.getSerializable("account");

        final Button submitButton = (Button) findViewById(R.id.new_activity_submit_button);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                createNewActivity();
            }
        });
    }

    private void createNewActivity() {
        Log.i(getClass().getSimpleName(), "creating activity for " + myAccount.getEmail());
/*
        final DatabaseHelper helper = new DatabaseHelper(getApplicationContext());
        final DroidFitActivity activity = new DroidFitActivityRunning(getApplicationContext(),
                new GregorianCalendar(2015, 01, 01, 12, 00, 00),
                13.1,
                new GregorianCalendar(1970, 01, 01, 01, 58, 21));
        helper.insertActivity(myAccount, activity);
*/
    }
}
