package marcschweikert.com.droidfit;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
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
public class LoginActivity extends Activity {
    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private UserLoginTask myAuthTask = null;

    // UI references.
    private EditText myEmailView;
    private EditText myPasswordView;
    private View myProgressView;
    private View myLoginFormView;
    private Button mySignInButton;
    private Button myNewAccountButton;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // UI references
        myEmailView = (EditText) findViewById(R.id.email);
        myPasswordView = (EditText) findViewById(R.id.password);
        mySignInButton = (Button) findViewById(R.id.login_button);
        myNewAccountButton = (Button) findViewById(R.id.new_account_button);

        mySignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(final View view) {
                attemptLogin();
            }
        });

        myNewAccountButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(final View v) {
                final Intent intent = new Intent(getApplicationContext(), NewAccountActivity.class);
                startActivity(intent);
            }
        });

        myLoginFormView = findViewById(R.id.login_form);
        myProgressView = findViewById(R.id.login_progress);
    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    public void attemptLogin() {
        if (myAuthTask != null) {
            return;
        }

        // Reset errors.
        myEmailView.setError(null);
        myPasswordView.setError(null);

        // Store values at the time of the login attempt.
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

        if (cancel) {
            // There was an error; don't attempt login and focus the first form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to perform the user login attempt.
            showProgress(true);

            // hash the password
            final String hashedPassword = CryptoFacade.getInstance().hashPassword(password);

            final Account account = new Account(null, null, email, hashedPassword);
            myAuthTask = new UserLoginTask(account);
            myAuthTask.execute((Void) null);
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

            myLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            myLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    myLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
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
            myLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

        private Account myAccount;

        UserLoginTask(final Account account) {
            myAccount = account;
        }

        @Override
        protected Boolean doInBackground(final Void... params) {
            if (null == myAccount) {
                Log.e(getClass().getSimpleName(), "Attempted to authenticate with null account!");
                return false;
            }

            Log.i(getClass().getSimpleName(), "Attempting to authenticate account for " + myAccount.getEmail());

            final DatabaseFacade helper = new DatabaseFacade(getApplicationContext());
            final Account dbAccount = helper.getUserAccount(myAccount.getEmail());

            if (null == dbAccount) {
                Log.i(getClass().getSimpleName(), "DatabaseFacade returned null account");
                return false;
            }

            if (!dbAccount.getHashedPassword().equals(myAccount.getHashedPassword())) {
                Log.i(getClass().getSimpleName(), "Passwords do not match");
                return false;
            }

            Log.i(getClass().getSimpleName(), "Passwords match");
            myAccount = dbAccount;
            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            myAuthTask = null;
            showProgress(false);

            if (success) {
                Log.d(getClass().getSimpleName(), "Launching main application for " + myAccount.getEmail());

                // null the entries
                myEmailView.setText("");
                myPasswordView.setText("");

                // pass the account to the main activity
                final Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                final Bundle bundle = new Bundle();
                bundle.putString("email", myAccount.getEmail());
                intent.putExtras(bundle);

                startActivity(intent);
            } else {
                myPasswordView.setError(getString(R.string.error_login_failed));
                myPasswordView.requestFocus();
            }
        }

        @Override
        protected void onCancelled() {
            myAuthTask = null;
            showProgress(false);
        }
    }
}
