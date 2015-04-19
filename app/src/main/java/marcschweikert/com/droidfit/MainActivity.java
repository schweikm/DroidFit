package marcschweikert.com.droidfit;

import android.app.Activity;
import android.os.Bundle;
import android.view.WindowManager;

import marcschweikert.com.database.Account;

/**
 * Created by Marc on 4/17/2015.
 */
public class MainActivity extends Activity {
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // get the account from the intent
        final Bundle bundle = getIntent().getExtras();
        final Account account = (Account) bundle.getSerializable("account");

        // make the title personalized
        if (null != account) {
            setTitle(getResources().getString(R.string.title_main) + " - " + account.getFirstName());
        }

        // hide the soft keyboard
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        // fragment takes over from here!
    }
}
