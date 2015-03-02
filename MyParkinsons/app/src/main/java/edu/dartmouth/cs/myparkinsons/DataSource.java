package edu.dartmouth.cs.myparkinsons;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

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

    public DataSource(Context context){dbHelper = new DbHelper(context);};

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public long insert(ExerciseItem item){
        ContentValues values = new ContentValues();
        values.put(DbHelper.COLUMN_DATE, item.getDate().getTimeInMillis());
        values.put(DbHelper.COLUMN_EXERCISE_TIME, item.getExerciseTime());
        if (item.isSpeechDone()){
            values.put(DbHelper.COLUMN_SPEECH_DONE, 1);
        }else {
            values.put(DbHelper.COLUMN_SPEECH_DONE, 0);
        }

        long id = database.insert(DbHelper.TABLE,null,values);
        item.setId(id);
        return id;
    }
    public ExerciseItem fetchItemByIndex(long id){
        ExerciseItem item;
        Cursor cursor = database.query(DbHelper.TABLE, DbHelper.allColumns, DbHelper.COLUMN_ID + " = " +id,null,null,null,null);
        cursor.moveToFirst();
        item = cursorToExerciseItem(cursor);
        cursor.close();
        return item;
    }
    public List<ExerciseItem> fetchItems(){
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
    public void removeItem(long id){
        database.delete(DbHelper.TABLE,DbHelper.COLUMN_ID + " = " + id, null);
        close();
    }
    private ExerciseItem cursorToExerciseItem(Cursor cursor) {
        long id =  cursor.getLong(cursor.getColumnIndex(DbHelper.COLUMN_ID));
        long time = cursor.getLong(cursor.getColumnIndex(DbHelper.COLUMN_EXERCISE_TIME));
        int speech = cursor.getInt(cursor.getColumnIndex(DbHelper.COLUMN_SPEECH_DONE));
        String date = cursor.getString(cursor.getColumnIndex(DbHelper.COLUMN_DATE));
        //TODO save and store the correct date
        Calendar cal = Calendar.getInstance();
        boolean s;
        if (speech == 0){
            s = false;
        }
        else {
            s = true;
        }
        ExerciseItem item = new ExerciseItem(cal,s, time);
        item.setId(id);

        return item;
    }

}
