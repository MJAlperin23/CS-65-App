package edu.dartmouth.cs.myparkinsons;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;

/**
 * Created by Andrew on 2/10/15.
 */
public class ExerciseItem {
    private long id;
    private Calendar date;
    private int speechDoneCount;
    private int speechCorrectCount;
    private long exerciseTime;
    private long exerciseGoalTime;

    public ExerciseItem(Calendar date, int speechDoneCount, int speechCorrectCount, long time, long exerciseGoalTime) {
        this.date = date;
        this.exerciseTime = time;
        this.speechCorrectCount = speechCorrectCount;
        this.speechDoneCount = speechDoneCount;
        this.exerciseGoalTime = exerciseGoalTime;
    }

    public long getExerciseGoalTime() {
        return exerciseGoalTime;
    }

    public void setExerciseGoalTime(long exerciseGoalTime) {
        this.exerciseGoalTime = exerciseGoalTime;
    }

    public void setCalendar(Calendar c) {
        this.date = c;
    }

    public long getId () {return id;};
    public void setId(long id){this.id = id;};
    public Calendar getDate() {
        return date;
    }

    public float getSpeechPercent() {
        return speechCorrectCount / (float)speechDoneCount;
    }

    public int getSpeechDoneCount() {
        return speechDoneCount;
    }

    public int getSpeechCorrectCount() {
        return speechCorrectCount;
    }

    public long getExerciseTime() {
        return exerciseTime;
    }

    public int getDayOfMonth() { return this.date.get(Calendar.DAY_OF_MONTH); }

    public int getMonthOfYear() { return this.date.get(Calendar.MONTH); }

    public int getYear() { return this.date.get(Calendar.YEAR); }

    public static List<ExerciseItem> generateItemList() {
        List<ExerciseItem> itemList = new ArrayList<>();
        Calendar c = Calendar.getInstance();
        for (int i = 10; i > 0; i--) {
            ExerciseItem item = generateItem(c, i);
            itemList.add(item);
        }

        return itemList;
    }

    public static ExerciseItem generateItem(Calendar c, int past) {
        ExerciseItem item = generateItem();
        int currDay = c.get(Calendar.DAY_OF_YEAR);
        int newDay = (currDay - past);
        Calendar newDate = Calendar.getInstance();
        newDate.set(Calendar.DAY_OF_YEAR, newDay);
        item.setCalendar(newDate);
        return item;
    }

    public static ExerciseItem generateItem() {
        Random rand = new Random();
        long walking = Math.abs(rand.nextLong()) % 3600000;
        Calendar c = Calendar.getInstance();
        int totalSpeech = (Math.abs(rand.nextInt()) % 12) + 1;
        int totalCorrect = Math.abs(rand.nextInt()) % totalSpeech;
        long goalTime = 60;
        return new ExerciseItem(c, totalSpeech, totalCorrect, walking, goalTime);
    }

}
