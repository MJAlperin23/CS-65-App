package edu.dartmouth.cs.myparkinsons;

import java.util.Calendar;

/**
 * Created by Andrew on 2/10/15.
 */
public class ExerciseItem {
    private long id;
    private Calendar date;
    private boolean speechDone;
    private long exerciseTime;

    public ExerciseItem(Calendar date, boolean speechDone, long time) {
        this.date = date;
        this.speechDone = speechDone;
        this.exerciseTime = time;
    }
    public long getId () {return id;};
    public void setId(long id){this.id = id;};
    public Calendar getDate() {
        return date;
    }

    public boolean isSpeechDone() {
        return speechDone;
    }

    public long getExerciseTime() {
        return exerciseTime;
    }
}
