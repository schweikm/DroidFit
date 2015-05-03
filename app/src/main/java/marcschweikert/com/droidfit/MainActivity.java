package marcschweikert.com.droidfit;

import android.app.Activity;
import android.os.Bundle;
import android.view.WindowManager;

import marcschweikert.com.database.Account;
import marcschweikert.com.database.DatabaseFacade;

/**
 * Created by Marc on 4/17/2015.
 */
public class MainActivity extends Activity {
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // get the account from the intent
        final Bundle bundle = getIntent().getExtras();
        final String email = bundle.getString("email");

        // get the full account from the database
        final DatabaseFacade helper = new DatabaseFacade(getApplicationContext());
        final Account account = helper.getUserAccount(email);

        // make the title personalized
        if (null != account) {
            setTitle(getResources().getString(R.string.title_main) + " - " + account.getFirstName());

            // put the full account into the intent
            bundle.putSerializable("account", account);
            getIntent().putExtras(bundle);
        }

        // hide the soft keyboard
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        // fragment takes over from here!
        setContentView(R.layout.activity_main);
    }
}
