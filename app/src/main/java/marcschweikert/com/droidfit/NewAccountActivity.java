package marcschweikert.com.droidfit;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import marcschweikert.com.database.Account;
import marcschweikert.com.database.DatabaseFacade;
import marcschweikert.com.security.CryptoFacade;
import marcschweikert.com.utils.ValidatorUtils;

/**
 * A login screen that offers login via email/password.
 */
public class NewAccountActivity extends Activity {
    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private NewAccountTask myAccountTask = null;

    // UI references.
    private EditText myFirstNameView;
    private EditText myLastNameView;
    private EditText myEmailView;
    private EditText myPasswordView;
    private View myProgressView;
    private View myNewAccountFormView;
    private Button mySubmitButton;
    private Button myCancelButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_account);

        // Set up the login form.
        myFirstNameView = (EditText) findViewById(R.id.new_first_name);
        myLastNameView = (EditText) findViewById(R.id.new_last_name);
        myEmailView = (EditText) findViewById(R.id.new_email);
        myPasswordView = (EditText) findViewById(R.id.new_password);
        myNewAccountFormView = findViewById(R.id.new_account_form);
        myProgressView = findViewById(R.id.new_account_progress);
        mySubmitButton = (Button) findViewById(R.id.new_submit_button);
        myCancelButton = (Button) findViewById(R.id.new_cancel_button);

        mySubmitButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                createAccount();
            }
        });

        myCancelButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    public void createAccount() {
        if (myAccountTask != null) {
            return;
        }

        // Reset errors.
        myFirstNameView.setError(null);
        myLastNameView.setError(null);
        myEmailView.setError(null);
        myPasswordView.setError(null);

        // Store values at the time of the login attempt.
        final String firstName = myFirstNameView.getText().toString();
        final String lastName = myLastNameView.getText().toString();
        final String email = myEmailView.getText().toString();
        final String password = myPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            myEmailView.setError(getString(R.string.error_field_required));
            focusView = myEmailView;
            cancel = true;
        } else if (!ValidatorUtils.isEmailValid(email)) {
            myEmailView.setError(getString(R.string.error_invalid_email));
            focusView = myEmailView;
            cancel = true;
        }

        // Check for a valid password
        if (TextUtils.isEmpty(password)) {
            myPasswordView.setError(getString(R.string.error_field_required));
            focusView = myPasswordView;
            cancel = true;
        } else if (!ValidatorUtils.isPasswordValid(password)) {
            myPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = myPasswordView;
            cancel = true;
        }

        // Check for a valid first name
        if (TextUtils.isEmpty(firstName)) {
            myFirstNameView.setError(getString(R.string.error_field_required));
            focusView = myFirstNameView;
            cancel = true;
        } else if (!ValidatorUtils.isNameValid(firstName)) {
            myFirstNameView.setError(getString(R.string.error_invalid_name));
            focusView = myFirstNameView;
            cancel = true;
        }

        // Check for a valid last name
        if (TextUtils.isEmpty(lastName)) {
            myLastNameView.setError(getString(R.string.error_field_required));
            focusView = myLastNameView;
            cancel = true;
        } else if (!ValidatorUtils.isNameValid(lastName)) {
            myLastNameView.setError(getString(R.string.error_invalid_name));
            focusView = myLastNameView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to perform the user login attempt.
            showProgress(true);

            // hash the password
            final String hashedPassword = CryptoFacade.getInstance().hashPassword(password);

            final Account account = new Account(firstName, lastName, email, hashedPassword);
            myAccountTask = new NewAccountTask(account);
            myAccountTask.execute((Void) null);
        }
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    public void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            myNewAccountFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            myNewAccountFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    myNewAccountFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            myProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            myProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    myProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            myProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            myNewAccountFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class NewAccountTask extends AsyncTask<Void, Void, Boolean> {

        private Account myAccount;

        NewAccountTask(final Account account) {
            myAccount = account;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            if (null == myAccount) {
                Log.e(getClass().getSimpleName(), "Attempted to create null account");
                return false;
            }

            final DatabaseFacade helper = new DatabaseFacade(getApplicationContext());
            final DatabaseFacade.DB_STATUS status = helper.insertAccount(myAccount);

            if (status == DatabaseFacade.DB_STATUS.ACCOUNT_SUCCESSFUL) {
                return true;
            }

            return false;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            myAccountTask = null;
            showProgress(false);

            if (success) {
                Log.i(getClass().getSimpleName(), "Account created ... returning to login screen");
                finish();
            } else {
                myPasswordView.setError(getString(R.string.error_account_failed));
                myPasswordView.requestFocus();
            }
        }

        @Override
        protected void onCancelled() {
            myAccountTask = null;
            showProgress(false);
        }
    }
}
