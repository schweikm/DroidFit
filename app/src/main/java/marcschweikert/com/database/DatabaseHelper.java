package marcschweikert.com.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.database.Cursor;
import android.database.SQLException;

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
        /** no password exists in DB */
        NO_PASSWORD,
        /** password provided matches current */
        PASSWORD_MATCH,
        /** password provided does not match */
        PASSWORD_NO_MATCH,
        /** account successfully created */
        ACCOUNT_SUCCESSFUL,
        /** account already exists */
        ACCOUNT_EXISTS,
        /** account failed to be created */
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
     * Add user, pass to db
     *
     * @param account        Account information
     * @return DB_STATUS
     */
    public DB_STATUS insertAccount(final Account account) {
        // if there is no existing password, insert it into the database
        final Account dbAccount = getUserAccount(account.getEmail());

        // is there an account already?
        if (null != dbAccount && account.getEmail().equals(dbAccount.getEmail())) {
                return DB_STATUS.ACCOUNT_EXISTS;
        }

         Log.i(getClass().getName(), "Inserting password for :  " + account.getEmail());
         final ContentValues values = new ContentValues();
         values.put(PROFILE_COL_FIRST_NAME, account.getFirstName());
         values.put(PROFILE_COL_LAST_NAME, account.getLastName());
         values.put(PROFILE_COL_EMAIL, account.getEmail());
         values.put(PROFILE_COL_PASS, account.getHashedPassword());

         try {
             theDatabase.insertOrThrow(PROFILE_TABLE, null, values);
         }
         catch(final Exception e) {
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
        final Cursor cursor = theDatabase.query(PROFILE_TABLE,
                                                new String[]{PROFILE_COL_ID, PROFILE_COL_FIRST_NAME, PROFILE_COL_LAST_NAME, PROFILE_COL_EMAIL, PROFILE_COL_PASS},
                                                PROFILE_COL_EMAIL + " = ?", new String[]{email},
                                                null, null, null);

        Account account = null;
        try {
            if (cursor.moveToFirst()) {
                Log.i(getClass().getSimpleName(), "Found account for " + email);
                Integer id = null;
                try {
                    id = Integer.parseInt(cursor.getString(0));
                }
                catch(final Exception e) {
                    Log.e(getClass().getSimpleName(), "");
                    return account;
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
