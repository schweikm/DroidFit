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
import marcschweikert.com.droidfit.DroidFitActivityFactory;
import marcschweikert.com.droidfit.R;
import marcschweikert.com.utils.DateUtils;

/**
 * Database interface to store and retrieve username, hashed password combinations
 *
 * @author Marc Schweikert
 */
public final class DatabaseFacade extends SQLiteOpenHelper {
    /**
     * Database version - change to upgrade
     */
    private static final int DB_VERSION = 17;
    /**
     * Name of database
     */
    private static final String DB_NAME = "secure_data";
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
     * activity type table
     */
    private static final String ACTIVITY_TYPE_TABLE = "activity_type";
    /**
     * activity type id
     */
    private static final String ACTIVITY_TYPE_COL_ID = "activity_type_id";
    /**
     * activity type text
     */
    private static final String ACTIVITY_TYPE_COL_TEXT = "activity_type_text";
    /**
     * activity table name
     */
    private static final String ACTIVITY_TABLE = "activities";
    /**
     * activity id
     */
    private static final String ACTIVITY_COL_ID = "activity_id";
    /**
     * activity foreign profile id
     */
    private static final String ACTIVITY_COL_USER_ID = "activity_user_id";
    /**
     * activity type
     */
    private static final String ACTIVITY_COL_TYPE_ID = "activity_type_id";
    /**
     * activity date
     */
    private static final String ACTIVITY_COL_DATE = "activity_date";
    /**
     * activity distance
     */
    private static final String ACTIVITY_COL_DISTANCE = "activity_distance";
    /**
     * activity duration
     */
    private static final String ACTIVITY_COL_DURATION = "activity_duration";

    /**
     * context for superclass
     */
    private Context myContext;


    /**
     * Default constructor for class. Passes application context to parent and
     * gets reference to database.
     *
     * @param context (Context) Application context
     */
    public DatabaseFacade(final Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        myContext = context;
    }


    ///////////////////////
    //  Account methods  //
    ///////////////////////


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


        try (final SQLiteDatabase db = getWritableDatabase()) {
            db.insertOrThrow(PROFILE_TABLE, null, values);
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
        final SQLiteDatabase db = getReadableDatabase();
        final Cursor cursor = db.query(PROFILE_TABLE,
                new String[]{PROFILE_COL_ID, PROFILE_COL_FIRST_NAME, PROFILE_COL_LAST_NAME, PROFILE_COL_EMAIL, PROFILE_COL_PASS},
                PROFILE_COL_EMAIL + " = ?", new String[]{email},
                null, null, null);

        if (cursor.getCount() > 1) {
            Log.w(getClass().getSimpleName(), "Found more than one account for " + email);
        }

        Account account = null;
        while (cursor.moveToNext()) {
            Log.i(getClass().getSimpleName(), "Found account for " + email);
            account = new Account(cursor.getString(1),
                    cursor.getString(2),
                    cursor.getString(3),
                    cursor.getString(4));

            account.setID(cursor.getInt(0));
        }

        db.close();
        cursor.close();
        return account;
    }


    ////////////////////////
    //  Activity methods  //
    ////////////////////////


    public boolean insertActivity(final Account account, final DroidFitActivity activity) {
        if (null == account || null == activity) {
            Log.e(getClass().getSimpleName(), "Attempted to insert activity for either null account or activity");
            return false;
        }


        final Integer type_id = getActivityTypeID(activity);
        if (null == type_id) {
            Log.e(getClass().getSimpleName(), "Retrieved null activity id for " + activity.getText());
            return false;
        }

        Log.i(getClass().getName(), "Inserting activity for :  " + account.getEmail());
        final ContentValues values = new ContentValues();
        values.put(ACTIVITY_COL_USER_ID, account.getID());
        values.put(ACTIVITY_COL_TYPE_ID, type_id);
        values.put(ACTIVITY_COL_DATE, DateUtils.formatDateTime(activity.getDate()));
        values.put(ACTIVITY_COL_DISTANCE, activity.getDistance());
        values.put(ACTIVITY_COL_DURATION, DateUtils.formatDateTime(activity.getDuration()));

        try (final SQLiteDatabase db = getWritableDatabase()) {
            db.insertOrThrow(ACTIVITY_TABLE, null, values);
        } catch (final Exception e) {
            return false;
        }

        return true;
    }

    public boolean updateActivity(final DroidFitActivity activity) {
        if (null == activity) {
            Log.e(getClass().getSimpleName(), "Attempted to update null activity");
            return false;
        }

        // updated attributes
        final ContentValues values = new ContentValues();
        values.put(ACTIVITY_COL_DATE, DateUtils.formatDateTime(activity.getDate()));
        values.put(ACTIVITY_COL_DISTANCE, activity.getDistance());
        values.put(ACTIVITY_COL_DURATION, DateUtils.formatDateTime(activity.getDuration()));

        // update the database
        try (final SQLiteDatabase db = getWritableDatabase()) {
            db.update(ACTIVITY_TABLE, values, ACTIVITY_COL_ID + " = ?",
                    new String[]{String.valueOf(activity.getID())});
        } catch (final Exception e) {
            return false;
        }

        return true;
    }

    public List<DroidFitActivity> getUserActivities(final Account account) {
        if (null == account) {
            Log.e(getClass().getSimpleName(), "Attempted to query for null account");
            return null;
        }

        final List<DroidFitActivity> list = new ArrayList<>();

        // query the database
        final SQLiteDatabase db = getReadableDatabase();
        final Cursor cursor = db.query(ACTIVITY_TABLE,
                new String[]{ACTIVITY_COL_ID, ACTIVITY_COL_TYPE_ID, ACTIVITY_COL_DATE, ACTIVITY_COL_DISTANCE, ACTIVITY_COL_DURATION},
                ACTIVITY_COL_USER_ID + " = ?", new String[]{(account.getID()).toString()},
                null, null, null);

        Log.i(getClass().getSimpleName(), "Found " + cursor.getCount() + " activities for " + account.getEmail());

        while (cursor.moveToNext()) {
            // not sure how to create the activity - time for a factory!
            final DroidFitActivity activity = DroidFitActivityFactory.createActivityByID(myContext, cursor.getInt(1));

            if (null == activity) {
                Log.e(getClass().getSimpleName(), "Activity factory returned null activity!");
                continue;
            }

            // now set the other attributes
            activity.setID(cursor.getInt(0));
            activity.setDate(DateUtils.convertStringToCalendar(cursor.getString(2)));
            activity.setDistance(cursor.getDouble(3));
            activity.setDuration(DateUtils.convertStringToCalendar(cursor.getString(4)));

            list.add(activity);
        }

        db.close();
        cursor.close();
        return list;
    }

    public DroidFitActivity getActivityByID(final Integer activity_id) {
        if (null == activity_id) {
            Log.e(getClass().getSimpleName(), "Attempted to query for null activity_id");
            return null;
        }

        // query the database
        final SQLiteDatabase db = getReadableDatabase();
        final Cursor cursor = db.query(ACTIVITY_TABLE,
                new String[]{ACTIVITY_COL_ID, ACTIVITY_COL_TYPE_ID, ACTIVITY_COL_DATE, ACTIVITY_COL_DISTANCE, ACTIVITY_COL_DURATION},
                ACTIVITY_COL_ID + " = ?", new String[]{(activity_id).toString()},
                null, null, null);

        DroidFitActivity activity = null;
        while (cursor.moveToNext()) {
            // not sure how to create the activity - time for a factory!
            activity = DroidFitActivityFactory.createActivityByID(myContext, cursor.getInt(1));

            if (null == activity) {
                Log.e(getClass().getSimpleName(), "Activity factory returned null activity!");
                continue;
            }

            // now set the other attributes
            activity.setID(cursor.getInt(0));
            activity.setDate(DateUtils.convertStringToCalendar(cursor.getString(2)));
            activity.setDistance(cursor.getDouble(3));
            activity.setDuration(DateUtils.convertStringToCalendar(cursor.getString(4)));
        }

        db.close();
        cursor.close();
        return activity;
    }


    /////////////////////////////
    //  Activity type methods  //
    /////////////////////////////


    public List<String> getActivityTypes() {
        // query the database
        final SQLiteDatabase db = getReadableDatabase();
        final Cursor cursor = db.query(ACTIVITY_TYPE_TABLE,
                new String[]{ACTIVITY_TYPE_COL_TEXT},
                ACTIVITY_TYPE_COL_TEXT + " IS NOT NULL", null, null, null, null);

        final List<String> activityTypes = new ArrayList<>();
        while (cursor.moveToNext()) {

            activityTypes.add(cursor.getString(0));
        }

        db.close();
        cursor.close();
        return activityTypes;
    }

    public String getActivityTypeString(final Integer type_id) {
        if (null == type_id) {
            Log.e(getClass().getSimpleName(), "Attempted to retrieve text of null activity");
            return null;
        }

        // query the database
        final SQLiteDatabase db = getReadableDatabase();
        final Cursor cursor = db.query(ACTIVITY_TYPE_TABLE,
                new String[]{ACTIVITY_TYPE_COL_TEXT},
                ACTIVITY_TYPE_COL_ID + " = ?", new String[]{type_id.toString()},
                null, null, null);

        if (cursor.getCount() > 1) {
            Log.w(getClass().getSimpleName(), "Found more than one activity type for " + type_id);
        }

        String type_text = null;
        while (cursor.moveToNext()) {
            type_text = cursor.getString(0);
        }

        db.close();
        cursor.close();
        return type_text;
    }

    public Integer getActivityTypeID(final DroidFitActivity activity) {
        if (null == activity) {
            Log.e(getClass().getSimpleName(), "Attempted to retrieve id of null activity");
            return null;
        }

        // query the database
        final SQLiteDatabase db = getReadableDatabase();
        final Cursor cursor = db.query(ACTIVITY_TYPE_TABLE,
                new String[]{ACTIVITY_TYPE_COL_ID},
                ACTIVITY_TYPE_COL_TEXT + " = ?", new String[]{activity.getText()},
                null, null, null);

        if (cursor.getCount() > 1) {
            Log.w(getClass().getSimpleName(), "Found more than one type id for " + activity.getText());
        }

        Integer id = null;
        while (cursor.moveToNext()) {
            try {
                id = Integer.parseInt(cursor.getString(0));
            } catch (final Exception e) {
                Log.e(getClass().getSimpleName(), "Failed to convert id " + cursor.getString(0) + " to int");
                return null;
            }
        }

        db.close();
        cursor.close();
        return id;
    }


    ////////////////////////////////
    //  SQLiteOpenHelper methods  //
    ////////////////////////////////


    /**
     * Creates the database. It is only called when there is a new version
     * passed into the helper.
     */
    @Override
    public void onCreate(final SQLiteDatabase db) {
        //
        // user_profile
        //
        final String sql1 = "CREATE TABLE " + PROFILE_TABLE + "(" +
                PROFILE_COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                PROFILE_COL_FIRST_NAME + " TEXT NOT NULL, " +
                PROFILE_COL_LAST_NAME + " TEXT NOT NULL, " +
                PROFILE_COL_EMAIL + " TEXT NOT NULL, " +
                PROFILE_COL_PASS + " TEXT NOT NULL);";
        if (true == executeSQL(db, sql1)) {
            Log.i(getClass().getSimpleName(), "Table created: " + PROFILE_TABLE);
        }


        //
        // activity_type
        //
        final String sql2 = "CREATE TABLE " + ACTIVITY_TYPE_TABLE + "(" +
                ACTIVITY_TYPE_COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                ACTIVITY_TYPE_COL_TEXT + " TEXT NOT NULL);";
        if (true == executeSQL(db, sql2)) {
            Log.i(getClass().getSimpleName(), "Table created: " + ACTIVITY_TYPE_TABLE);
        }

        // populate with data
        try {
            final ContentValues values1 = new ContentValues();
            values1.put(ACTIVITY_TYPE_COL_TEXT, myContext.getResources().getString(R.string.activity_type_cycling));
            db.insertOrThrow(ACTIVITY_TYPE_TABLE, null, values1);

            final ContentValues values2 = new ContentValues();
            values2.put(ACTIVITY_TYPE_COL_TEXT, myContext.getResources().getString(R.string.activity_type_running));
            db.insertOrThrow(ACTIVITY_TYPE_TABLE, null, values2);

            final ContentValues values3 = new ContentValues();
            values3.put(ACTIVITY_TYPE_COL_TEXT, myContext.getResources().getString(R.string.activity_type_swimming));
            db.insertOrThrow(ACTIVITY_TYPE_TABLE, null, values3);
        } catch (final Exception e) {
            Log.e(getClass().getSimpleName(), "Failed to populate table: " + ACTIVITY_TYPE_TABLE);
            Log.e(getClass().getSimpleName(), e.getMessage());
        }


        //
        // activity
        //
        final String sql3 = "CREATE TABLE " + ACTIVITY_TABLE + "(" +
                ACTIVITY_COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                ACTIVITY_COL_USER_ID + " INTEGER NOT NULL, " +
                ACTIVITY_COL_TYPE_ID + " INTEGER NOT NULL, " +
                ACTIVITY_COL_DATE + " TEXT NOT NULL, " +
                ACTIVITY_COL_DISTANCE + " REAL NOT NULL, " +
                ACTIVITY_COL_DURATION + " TEXT NOT NULL, " +
                "FOREIGN KEY(" + ACTIVITY_COL_USER_ID + ") REFERENCES " + PROFILE_TABLE + "(" + PROFILE_COL_ID + ")," +
                "FOREIGN KEY(" + ACTIVITY_COL_TYPE_ID + ") REFERENCES " + ACTIVITY_TYPE_TABLE + "(" + ACTIVITY_TYPE_COL_ID + "));";
        if (true == executeSQL(db, sql3)) {
            Log.i(getClass().getSimpleName(), "Table created: " + ACTIVITY_TABLE);
        }

        Log.i(getClass().getName(), "Database Created:" + DB_VERSION);
    }

    private boolean executeSQL(final SQLiteDatabase db, final String sql) {
        try {
            db.beginTransaction();
            db.execSQL(sql);
            db.setTransactionSuccessful();
            db.endTransaction();
        } catch (final SQLException e) {
            Log.e(getClass().getName(), e.getMessage());
            return false;
        }

        return true;
    }

    /**
     * Upgrades the database.
     */
    @Override
    public void onUpgrade(final SQLiteDatabase db, final int oldVersion, final int newVersion) {
        // *****************************************************************************
        // This method is called when there is a new version of the database being used.
        // *****************************************************************************

        try {
            db.beginTransaction();
            db.execSQL("DROP TABLE IF EXISTS " + ACTIVITY_TABLE + ";");
            db.execSQL("DROP TABLE IF EXISTS " + ACTIVITY_TYPE_TABLE + ";");
            db.execSQL("DROP TABLE IF EXISTS " + PROFILE_TABLE + ";");

            Log.i(getClass().getName(), "Tables dropped");

            db.setTransactionSuccessful();
            db.endTransaction();

            onCreate(db);
        } catch (final SQLException e) {
            Log.e(getClass().getName(), "Upgrade failed:" + e.getMessage());
        } catch (final Exception e) {
            Log.e(getClass().getName(), "Upgrade failed - Uncaught error");
        }
    }

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
}
