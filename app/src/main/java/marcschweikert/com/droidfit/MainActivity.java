package marcschweikert.com.droidfit;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.hardware.input.InputManager;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.util.Log;

import marcschweikert.com.database.Account;

/**
 * Created by Marc on 4/17/2015.
 */
public class MainActivity extends Activity {
    private Account myAccount;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // get the account from the intent
        final Bundle bundle = getIntent().getExtras();
        myAccount = (Account)bundle.getSerializable("account");

        Log.i(getClass().getSimpleName(), "MAIN VIEW for ->" + myAccount.getEmail() + "<-");

        // make it look pretty
        setTitle(getResources().getString(R.string.title_main) + " - " + myAccount.getFirstName());
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }
}
