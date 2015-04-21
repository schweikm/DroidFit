package marcschweikert.com.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import marcschweikert.com.droidfit.DroidFitActivity;

/**
 * Database interface to store and retrieve username, hashed password combinations
 *
 * @author Marc Schweikert
 */
public final class DatabaseHelper extends SQLiteOpenHelper {
    /**
     * Database version - change to upgrade
     */
    private static final int DB_VERSION = 2;
    /**
     * Name of database
     */
    private static final String DB_NAME = "securedata";
    /**
     * Reference to SQLite database
     */
    private SQLiteDatabase theDatabase = null;

    /**
     * table name
     */
    private static final String PROFILE_TABLE = "user_profile";
    /**
     * id column
     */
    private static final String PROFILE_COL_ID = "profile_id";
    /**
     * first name column
     */
    private static final String PROFILE_COL_FIRST_NAME = "first_name";
    /**
     * last name column
     */
    private static final String PROFILE_COL_LAST_NAME = "last_name";
    /**
     * email column
     */
    private static final String PROFILE_COL_EMAIL = "user_email";
    /**
     * hashed password column
     */
    private static final String PROFILE_COL_PASS = "user_password";

    /**
     * Returns status of database operation
     */
    public enum DB_STATUS {
        /**
         * no password exists in DB
         */
        NO_PASSWORD,
        /**
         * password provided matches current
         */
        PASSWORD_MATCH,
        /**
         * password provided does not match
         */
        PASSWORD_NO_MATCH,
        /**
         * account successfully created
         */
        ACCOUNT_SUCCESSFUL,
        /**
         * account already exists
         */
        ACCOUNT_EXISTS,
        /**
         * account failed to be created
         */
        ACCOUNT_FAILED
    }

    /**
     * Default constructor for class. Passes application context to parent and
     * gets reference to database.
     *
     * @param context (Context) Application context
     */
    public DatabaseHelper(final Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        theDatabase = getWritableDatabase();
    }

    /**
     * Add user, pass to db
     *
     * @param account Account information
     * @return DB_STATUS
     */
    public DB_STATUS insertAccount(final Account account) {
        if (null == account) {
            Log.e(getClass().getSimpleName(), "Attempted to insert null account");
            return DB_STATUS.ACCOUNT_FAILED;
        }

        // if there is no existing password, insert it into the database
        final Account dbAccount = getUserAccount(account.getEmail());

        // is there an account already?
        if (null != dbAccount) {
            if (account.getEmail().equals(dbAccount.getEmail())) {
                return DB_STATUS.ACCOUNT_EXISTS;
            }

            // this should not be possible
            return DB_STATUS.ACCOUNT_FAILED;
        }

        Log.i(getClass().getName(), "Inserting password for :  " + account.getEmail());
        final ContentValues values = new ContentValues();
        values.put(PROFILE_COL_FIRST_NAME, account.getFirstName());
        values.put(PROFILE_COL_LAST_NAME, account.getLastName());
        values.put(PROFILE_COL_EMAIL, account.getEmail());
        values.put(PROFILE_COL_PASS, account.getHashedPassword());

        try {
            theDatabase.insertOrThrow(PROFILE_TABLE, null, values);
        } catch (final Exception e) {
            return DB_STATUS.ACCOUNT_FAILED;
        }

        return DB_STATUS.ACCOUNT_SUCCESSFUL;
    }

    /**
     * Get the password for specified user
     *
     * @param email email address
     * @return password or null if not found
     */
    public Account getUserAccount(final String email) {
        if (null == email || email.isEmpty()) {
            Log.e(getClass().getSimpleName(), "Attempted to retrieve null account");
            return null;
        }

        // query the database
        final Cursor cursor = theDatabase.query(PROFILE_TABLE,
                new String[]{PROFILE_COL_ID, PROFILE_COL_FIRST_NAME, PROFILE_COL_LAST_NAME, PROFILE_COL_EMAIL, PROFILE_COL_PASS},
                PROFILE_COL_EMAIL + " = ?", new String[]{email},
                null, null, null);

        Account account = null;
        try {
            if (cursor.moveToFirst()) {
                Log.i(getClass().getSimpleName(), "Found account for " + email);
                Integer id;
                try {
                    id = Integer.parseInt(cursor.getString(0));
                } catch (final Exception e) {
                    Log.e(getClass().getSimpleName(), "Failed to convert id " + cursor.getString(0) + " to int");
                    return null;
                }

                account = new Account(id,
                        cursor.getString(1),
                        cursor.getString(2),
                        cursor.getString(3),
                        cursor.getString(4));
            } else {
                Log.w(getClass().getSimpleName(), "No account found for:  " + email);
            }
        } catch (final SQLException e) {
            Log.e(getClass().getSimpleName(), "Unable to process SQL");
            Log.e(getClass().getSimpleName(), e.getMessage());
        } catch (final Exception e) {
            Log.e(getClass().getSimpleName(), "Unhandled exception SQL");
            Log.e(getClass().getSimpleName(), e.getMessage());
        }

        cursor.close();
        return account;
    }

    public List<DroidFitActivity> getUserActivities(final Account account) {
        if (null == account) {
            Log.e(getClass().getSimpleName(), "Attempted to query for null account");
            return null;
        }

        final String email = account.getEmail();
        final List<DroidFitActivity> list = new ArrayList<>();

        final DroidFitActivity activity1 = new DroidFitActivity(null, "Running", null, null, null);
        list.add(activity1);

        final DroidFitActivity activity2 = new DroidFitActivity(null, "Cycling", null, null, null);
        list.add(activity2);

        final DroidFitActivity activity3 = new DroidFitActivity(null, "Swimming", null, null, null);
        list.add(activity3);

        return list;
    }

    /**
     * Creates the database. It is only called when there is a new version
     * passed into the helper.
     */
    @Override
    public void onCreate(final SQLiteDatabase db) {
        final String sql = "CREATE TABLE " + PROFILE_TABLE + "(" +
                PROFILE_COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                PROFILE_COL_FIRST_NAME + " TEXT NOT NULL, " +
                PROFILE_COL_LAST_NAME + " TEXT NOT NULL, " +
                PROFILE_COL_EMAIL + " TEXT NOT NULL, " +
                PROFILE_COL_PASS + " TEXT NOT NULL);";

        try {
            db.beginTransaction();
            db.execSQL(sql);
            Log.i(getClass().getName(), "Table Created:" + PROFILE_TABLE);

            db.setTransactionSuccessful();
            db.endTransaction();

            Log.i(getClass().getName(), "Database Created:" + DB_VERSION);

        } catch (final SQLException e) {
            Log.e(getClass().getName(), "Could not create database");
        }
    }

    /**
     * Upgrades the database.
     */
    @Override
    public void onUpgrade(final SQLiteDatabase db, final int oldVersion, final int newVersion) {
        // *****************************************************************************
        // This method is called when there is a new version of the database
        // being used.
        // *****************************************************************************

        final String dropSQL = "DROP TABLE IF EXISTS " + PROFILE_TABLE + ";";

        try {
            db.beginTransaction();
            db.execSQL(dropSQL);

            Log.i(getClass().getName(), "Table dropped:" + PROFILE_TABLE);

            db.setTransactionSuccessful();
            db.endTransaction();
            onCreate(db);
        } catch (final SQLException e) {
            Log.e(getClass().getName(), "Upgrade failed:" + e.getMessage());
        } catch (final Exception e) {
            Log.e(getClass().getName(), "Upgrade failed - Uncaught error");
        }
    }
}
