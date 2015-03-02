package edu.dartmouth.cs.myparkinsons;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by Cam on 3/1/2015.
 */
public class DbHelper extends SQLiteOpenHelper {
    public static final String TABLE = "exercise_items";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_DATE = "date";
    public static final String COLUMN_EXERCISE_TIME = "exercise_time";
    public static final String COLUMN_SPEECH_DONE = "speech_done";
    private static final String DATABASE_NAME = "exerciseItems.db";
    private static final int DATABASE_VERSION = 1;

    public static final String[] allColumns = {COLUMN_ID,COLUMN_DATE,COLUMN_EXERCISE_TIME,COLUMN_SPEECH_DONE};
    // Database creation sql statement
    private static final String DATABASE_CREATE = "create table if not exists "
            + TABLE + "("
            + COLUMN_ID + " integer primary key autoincrement, "
            + COLUMN_DATE + " datetime not null, "
            + COLUMN_EXERCISE_TIME + " integer, "
            + COLUMN_SPEECH_DONE + " integer not null );";

    public DbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DATABASE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(DbHelper.class.getName(),
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE);
        onCreate(db);
    }
}
