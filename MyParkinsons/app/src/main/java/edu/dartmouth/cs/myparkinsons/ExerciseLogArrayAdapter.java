package edu.dartmouth.cs.myparkinsons;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.ViewFlipper;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by Andrew on 2/2/15.
 */
public class ExerciseLogArrayAdapter extends ArrayAdapter<ExerciseItem> {

    Context context;
    int layoutResourceId;
    public SharedPreferences settingData;
    List<ExerciseItem> data = null;

    private float lastX;


    public ExerciseLogArrayAdapter(Context context, int layoutResourceId, List<ExerciseItem> data) {
        super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        ExerciseItemHolder holder = null;
        CircleCardHolder circleHolder = null;

        settingData = context.getSharedPreferences(SettingsActivity.SHARED_PREFERENCES_KEY, SettingsActivity.MODE_PRIVATE);

//        if (row == null) {
        if (position == 0) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            row = inflater.inflate(R.layout.buttons_row, parent, false);

            CircleButton exerciseButton = (CircleButton) row.findViewById(R.id.exercise_button_id);
            CircleButton speechButton = (CircleButton) row.findViewById(R.id.speech_button_id);

            exerciseButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(context, ExerciseLogActivity.class);
                    context.startActivity(i);
                }
            });

            speechButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(context, SpeechActivity.class);
                    context.startActivity(i);
                }
            });
        } else if (position == 1) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            row = inflater.inflate(R.layout.circle_progress_row, parent, false);

            circleHolder = new CircleCardHolder();
            circleHolder.flipper = (ViewFlipper) row.findViewById(R.id.viewFlipper);
            circleHolder.bar1 = (CircleProgressBar) row.findViewById(R.id.custom_progressBar);
            circleHolder.bar2 = (CircleProgressBar) row.findViewById(R.id.custom_progressBar2);
            circleHolder.progress1 = (TextView) row.findViewById(R.id.percentView);
            circleHolder.progress2 = (TextView) row.findViewById(R.id.percentView2);

            circleHolder.bar2.setColor(0xFF0066FF);
            circleHolder.bar2.setStrokeWidth(50);
            circleHolder.progress2.setText("67%");
            circleHolder.progress2.setTextColor(0xFF0066FF);

            circleHolder.bar1.setColor(0xFF29A629);
            circleHolder.bar1.setStrokeWidth(50);
            circleHolder.progress1.setText("33%");
            circleHolder.progress1.setTextColor(0xFF29A629);


        } else {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            row = inflater.inflate(R.layout.history_row, parent, false);

            LineProgressBar lineBar = (LineProgressBar) row.findViewById(R.id.line_progressBar);
            CircleProgressBar circleBar = (CircleProgressBar) row.findViewById(R.id.custom_progressBar);

            lineBar.setColor(0xFF0066FF);
            lineBar.setStrokeWidth(10);

            circleBar.setColor(0xFF29A629);
            circleBar.setStrokeWidth(10);

//                holder = new ExerciseItemHolder();
//                holder.date = (TextView) row.findViewById(R.id.dateText);
//                holder.time = (TextView) row.findViewById(R.id.exerciseTimeText);
//                holder.speech = (TextView) row.findViewById(R.id.speechPercentText);
            //holder.didSpeech = (CheckBox)row.findViewById(R.id.speechDoneCheckBox);

//                ExerciseItem entry = getItem(position);
//                SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
//                Date theDate = entry.getDate().getTime();
//                String date = format.format(theDate);
//                holder.date.setText(date);
//
//                String time = String.format("%02d hrs, %02d min, %02d sec",
//                        TimeUnit.MILLISECONDS.toHours(entry.getExerciseTime()),
//                        TimeUnit.MILLISECONDS.toMinutes(entry.getExerciseTime()) -
//                                TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(entry.getExerciseTime())),
//                        TimeUnit.MILLISECONDS.toSeconds(entry.getExerciseTime()) -
//                                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(entry.getExerciseTime())));
//
//
//                holder.time.setText("Exercise Time: " + time);
//                holder.speech.setText(String.format("Speech: %d out of %d", entry.getSpeechCorrectCount(), entry.getSpeechDoneCount()));
//                //holder.didSpeech.setChecked(entry.isSpeechDone());

        }


        return row;
    }


    @Override
    public ExerciseItem getItem(int position) {
        return data.get(position);
    }

    static class ExerciseItemHolder {
        TextView date;
        TextView time;
        TextView speech;
    }


    static class CircleCardHolder {
        ViewFlipper flipper;
        CircleProgressBar bar1;
        CircleProgressBar bar2;
        TextView progress1;
        TextView progress2;
        CircleButton exerciseButton;
        CircleButton speechButton;
    }
}