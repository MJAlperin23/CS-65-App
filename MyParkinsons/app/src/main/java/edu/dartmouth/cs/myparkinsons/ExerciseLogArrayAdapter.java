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
import java.util.Calendar;
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


    public ExerciseLogArrayAdapter(Context context, int layoutResourceId, List<ExerciseItem> data) {
        super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        CircleCardHolder circleHolder;
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
            }

            else if (position == 1) {
                LayoutInflater inflater = ((Activity) context).getLayoutInflater();
                row = inflater.inflate(R.layout.circle_progress_row, parent, false);

                circleHolder = new CircleCardHolder();
                circleHolder.flipper = (ViewFlipper) row.findViewById(R.id.viewFlipper);
                circleHolder.bar1 = (CircleProgressBar) row.findViewById(R.id.custom_progressBar);
                circleHolder.bar2 = (CircleProgressBar) row.findViewById(R.id.custom_progressBar2);
                circleHolder.progress1 = (TextView) row.findViewById(R.id.percentView);
                circleHolder.progress2 = (TextView) row.findViewById(R.id.percentView2);

                circleHolder.bar1.setProgressWithAnimation(0);
                circleHolder.bar2.setProgressWithAnimation(0);

                SharedPreferences settingData = context.getSharedPreferences(SettingsActivity.SHARED_PREFERENCES_KEY, Context.MODE_PRIVATE);

                long time = settingData.getLong(SettingsActivity.EXERCISE_TIME_KEY, 0);
                long minutes = (long) (time * 1.66667e-5);
                String text = String.format("%d/%d\nminutes", minutes, 60);
                circleHolder.progress1.setTextColor(0xFF29A629);
                circleHolder.bar1.setColor(0xFF29A629);
                circleHolder.bar1.setStrokeWidth(50);
                circleHolder.progress1.setText(text);
                circleHolder.bar1.setProgressWithAnimation((float) (minutes / 60. * 100));

                circleHolder.bar2.setColor(0xFF0066FF);
                circleHolder.bar2.setStrokeWidth(50);
                circleHolder.progress2.setTextColor(0xFF0066FF);
                int correct = settingData.getInt(SettingsActivity.CORRECT_SPEECH_KEY, 0);
                int total = settingData.getInt(SettingsActivity.TOTAL_SPEECH_KEY, 0);
                float percent = (float) correct / (float) total;
                if (total == 0) {
                    circleHolder.bar2.setProgressWithAnimation(0);
                } else {
                    circleHolder.bar2.setProgressWithAnimation(percent * 100);
                }
                text = String.format("%d/%d\ncorrect", correct, total);
                circleHolder.progress2.setText(text);




            } else {

                DataSource dataSource = new DataSource(context);
                dataSource.open();
                List<ExerciseItem> items = dataSource.fetchItems();
                ExerciseItem item = items.get(position - 1);
                dataSource.close();

                if (item == null)
                {
                    return row;
                }


                long exerciseMillis = item.getExerciseTime();
                long minutes = (long) (exerciseMillis * 1.66667e-5);
                int exerciseProgress = (int) (minutes / 60.0);
                int speechProgress = (int) (item.getSpeechPercent() * 100);

                LayoutInflater inflater = ((Activity) context).getLayoutInflater();
                row = inflater.inflate(R.layout.history_row, parent, false);

                LineProgressBar speechBar = (LineProgressBar)row.findViewById(R.id.speech_bar);
                LineProgressBar exerciseBar = (LineProgressBar)row.findViewById(R.id.exercise_bar);

                TextView date = (TextView) row.findViewById(R.id.dateTextView);

                Calendar c = item.getDate();
                SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy");
                date.setText(format.format(c.getTime()));


                speechBar.setColor(0xFF0066FF);
                speechBar.setStrokeWidth(10);
                speechBar.setProgressWithAnimation(exerciseProgress);

                exerciseBar.setColor(0xFF29A629);
                exerciseBar.setStrokeWidth(10);
                exerciseBar.setProgressWithAnimation(speechProgress);

            }

        return row;
    }


    static class CircleCardHolder
    {
        ViewFlipper flipper;
        CircleProgressBar bar1;
        CircleProgressBar bar2;
        TextView progress1;
        TextView progress2;
    }
}

