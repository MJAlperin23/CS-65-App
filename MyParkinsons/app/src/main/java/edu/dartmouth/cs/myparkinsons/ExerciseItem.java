package edu.dartmouth.cs.myparkinsons;

import java.util.Calendar;

/**
 * Created by Andrew on 2/10/15.
 */
public class ExerciseItem {
    private long id;
    private Calendar date;
    private int speechDoneCount;
    private int speechCorrectCount;
    private long exerciseTime;

    public ExerciseItem(Calendar date, int speechDoneCount, int speechCorrectCount, long time) {
        this.date = date;
        this.exerciseTime = time;
        this.speechCorrectCount = speechCorrectCount;
        this.speechDoneCount = speechDoneCount;
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
}
