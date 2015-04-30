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
    private NewAccountTask mAccountTask = null;

    // UI references.
    private EditText mFirstNameView;
    private EditText mLastNameView;
    private EditText mEmailView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mNewAccountFormView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_account);

        // Set up the login form.
        mFirstNameView = (EditText) findViewById(R.id.new_first_name);
        mLastNameView = (EditText) findViewById(R.id.new_last_name);
        mEmailView = (EditText) findViewById(R.id.new_email);
        mPasswordView = (EditText) findViewById(R.id.new_password);

        final Button mSubmitButton = (Button) findViewById(R.id.new_submit_button);
        mSubmitButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                createAccount();
            }
        });

        final Button cancelButton = (Button) findViewById(R.id.new_cancel_button);
        cancelButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mNewAccountFormView = findViewById(R.id.new_account_form);
        mProgressView = findViewById(R.id.new_account_progress);
    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    public void createAccount() {
        if (mAccountTask != null) {
            return;
        }

        // Reset errors.
        mFirstNameView.setError(null);
        mLastNameView.setError(null);
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        final String firstName = mFirstNameView.getText().toString();
        final String lastName = mLastNameView.getText().toString();
        final String email = mEmailView.getText().toString();
        final String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!ValidatorUtils.isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }

        // Check for a valid password
        if (TextUtils.isEmpty(password)) {
            mPasswordView.setError(getString(R.string.error_field_required));
            focusView = mPasswordView;
            cancel = true;
        } else if (!ValidatorUtils.isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid first name
        if (TextUtils.isEmpty(firstName)) {
            mFirstNameView.setError(getString(R.string.error_field_required));
            focusView = mFirstNameView;
            cancel = true;
        } else if (!ValidatorUtils.isNameValid(firstName)) {
            mFirstNameView.setError(getString(R.string.error_invalid_name));
            focusView = mFirstNameView;
            cancel = true;
        }

        // Check for a valid last name
        if (TextUtils.isEmpty(lastName)) {
            mLastNameView.setError(getString(R.string.error_field_required));
            focusView = mLastNameView;
            cancel = true;
        } else if (!ValidatorUtils.isNameValid(lastName)) {
            mLastNameView.setError(getString(R.string.error_invalid_name));
            focusView = mLastNameView;
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
            mAccountTask = new NewAccountTask(account);
            mAccountTask.execute((Void) null);
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

            mNewAccountFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mNewAccountFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mNewAccountFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mNewAccountFormView.setVisibility(show ? View.GONE : View.VISIBLE);
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
            mAccountTask = null;
            showProgress(false);

            if (success) {
                Log.i(getClass().getSimpleName(), "Account created ... returning to login screen");
                finish();
            } else {
                mPasswordView.setError(getString(R.string.error_account_failed));
                mPasswordView.requestFocus();
            }
        }

        @Override
        protected void onCancelled() {
            mAccountTask = null;
            showProgress(false);
        }
    }
}
