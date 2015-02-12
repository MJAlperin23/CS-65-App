package edu.dartmouth.cs.myparkinsons;

import java.util.Date;

/**
 * Created by Andrew on 2/10/15.
 */
public class ExerciseItem {

    private Date date;
    private boolean speechDone;
    private double walkingMiles;
    private double runningMiles;

    public ExerciseItem(Date date, boolean speechDone, double walkingMiles, double runningMiles) {
        this.date = date;
        this.speechDone = speechDone;
        this.walkingMiles = walkingMiles;
        this.runningMiles = runningMiles;
    }

    public Date getDate() {
        return date;
    }

    public boolean isSpeechDone() {
        return speechDone;
    }

    public double getWalkingMiles() {
        return walkingMiles;
    }

    public double getRunningMiles() {
        return runningMiles;
    }
}
