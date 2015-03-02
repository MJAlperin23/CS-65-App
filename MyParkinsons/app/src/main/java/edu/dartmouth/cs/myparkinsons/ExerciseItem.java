package edu.dartmouth.cs.myparkinsons;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by Andrew on 2/10/15.
 */
public class ExerciseItem {

    private Calendar date;
    private boolean speechDone;
    private long exerciseTime;

    public ExerciseItem(Calendar date, boolean speechDone, long time) {
        this.date = date;
        this.speechDone = speechDone;
        this.exerciseTime = time;
    }

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
