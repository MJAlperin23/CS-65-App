package edu.dartmouth.cs.myparkinsons;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by Cam on 3/1/2015.
 */
public class DataSource {
    private SQLiteDatabase database;
    private DbHelper dbHelper;
    private static final String TAG = "DataSource";
    private Context appContext;
    private String regId;

    public DataSource(Context context){
        dbHelper = new DbHelper(context);
        appContext = context;
    };


    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
        regId = getRegistrationId();
    }

    public void close() {
        dbHelper.close();
    }

    public long insert(ExerciseItem item) {
        ContentValues values = new ContentValues();
        values.put(DbHelper.COLUMN_DATE, item.getDate().getTimeInMillis());
        values.put(DbHelper.COLUMN_EXERCISE_TIME, item.getExerciseTime());
        values.put(DbHelper.COLUMN_SPEECH_DONE, item.getSpeechDoneCount());
        values.put(DbHelper.COLUMN_SPEECH_CORRECT, item.getSpeechCorrectCount());
        values.put(DbHelper.COLUMN_EXERCISE_GOAL_TIME, item.getExerciseGoalTime());

        long id = database.insert(DbHelper.TABLE, null, values);
        item.setId(id);
        Log.d("DataSource", "Inserted item id: " + item.getId());

        // upload item to server
        AsyncTask uploadTask = new AsyncTask<Object, Void, Void>() {
            @Override
            protected Void doInBackground(Object... params) {
                ExerciseItem item = (ExerciseItem) params[0];
                try {
                    HistoryUploader.insertItem(appContext, item, regId);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                return null;
            }
        };

        uploadTask.execute((Object) item);
        return id;
    }

    public ExerciseItem fetchItemByIndex(long id) {
        ExerciseItem item;
        Cursor cursor = database.query(DbHelper.TABLE, DbHelper.allColumns, DbHelper.COLUMN_ID + " = " + id, null, null, null, null);
        cursor.moveToFirst();
        if (cursor.isAfterLast()) {
            return null;
        }
        item = cursorToExerciseItem(cursor);
        cursor.close();
        return item;
    }


    public List<ExerciseItem> fetchItems() {
        List<ExerciseItem> items = new ArrayList<ExerciseItem>();
        if (!database.isOpen()) {
            database = dbHelper.getWritableDatabase();
        }
        Cursor cursor = database.query(DbHelper.TABLE, null, null, null, null, null, null);
        cursor.moveToFirst();

        while (!cursor.isAfterLast()) {
            ExerciseItem item = cursorToExerciseItem(cursor);
            items.add(item);
            cursor.moveToNext();
        }
        cursor.close();
        return items;
    }

    public void removeItem(long id) {
        database.delete(DbHelper.TABLE, DbHelper.COLUMN_ID + " = " + id, null);
    }

    private ExerciseItem cursorToExerciseItem(Cursor cursor) {
        long id = cursor.getLong(cursor.getColumnIndex(DbHelper.COLUMN_ID));
        long time = cursor.getLong(cursor.getColumnIndex(DbHelper.COLUMN_EXERCISE_TIME));
        int speechTotal = cursor.getInt(cursor.getColumnIndex(DbHelper.COLUMN_SPEECH_DONE));
        int speechCorrect = cursor.getInt(cursor.getColumnIndex(DbHelper.COLUMN_SPEECH_CORRECT));
        long date = cursor.getLong(cursor.getColumnIndex(DbHelper.COLUMN_DATE));
        long goalTime = cursor.getLong(cursor.getColumnIndex(DbHelper.COLUMN_EXERCISE_GOAL_TIME));

        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(date);

        ExerciseItem item = new ExerciseItem(cal, speechTotal, speechCorrect, time, goalTime);
        item.setId(id);

        return item;
    }

    public void deleteAllData() {

        List<ExerciseItem> itemsList = fetchItems();

        open();
        for (ExerciseItem entry : itemsList) {
            removeItem(entry.getId());
        }
        close();
    }

    /**
     * Gets the current registration ID for application on GCM service.
     * <p>
     * If result is empty, the app needs to register.
     *
     * @return registration ID, or empty string if there is no existing
     *         registration ID.
     */
    private String getRegistrationId() {
        final SharedPreferences prefs = getGCMPreferences();
        String registrationId = prefs.getString(MainActivity.PROPERTY_REG_ID, "");
        if (registrationId.isEmpty()) {
            return "";
        }
        // Check if app was updated; if so, it must clear the registration ID
        // since the existing regID is not guaranteed to work with the new
        // app version.
//        int registeredVersion = prefs.getInt(PROPERTY_APP_VERSION,
//                Integer.MIN_VALUE);
//        int currentVersion = getAppVersion(context);
//        if (registeredVersion != currentVersion) {
//            return "";
//        }
        return registrationId;
    }

    /**
     * @return Application's {@code SharedPreferences}.
     */
    private SharedPreferences getGCMPreferences() {
        // This sample app persists the registration ID in shared preferences,
        // but
        // how you store the regID in your app is up to you.
        return PreferenceManager.getDefaultSharedPreferences(appContext);
    }
}
