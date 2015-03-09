package edu.dartmouth.cs.myparkinsons;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ViewFlipper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by Andrew on 2/2/15.
 */
public class ExerciseLogArrayAdapter extends ArrayAdapter<ExerciseItem> {

    private Context context;
    int layoutResourceId;
    List<ExerciseItem> data = null;


    public ExerciseLogArrayAdapter(Context c, int layoutResourceId, List<ExerciseItem> data) {
        super(c, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        context = c;
        this.data = data;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;

        if (position == 0) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            row = inflater.inflate(R.layout.buttons_row, parent, false);

            CircleButton exerciseButton = (CircleButton) row.findViewById(R.id.exercise_button_id);
            CircleButton speechButton = (CircleButton) row.findViewById(R.id.speech_button_id);

            // for debug/demo purposes
//            CircleButton fillButton = (CircleButton) row.findViewById(R.id.fill_button_id);
//            fillButton.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
////
//                    final DataSource dataSource = new DataSource(context);
//                    dataSource.open();
//                    List<ExerciseItem> entryList = ExerciseItem.generateItemList();
//                    for (ExerciseItem item : entryList) {
//                        dataSource.insert(item);
//                    }
//                    dataSource.close();
//
//                    AsyncTask uploadTask = new AsyncTask<Object, Void, Void>() {
//                        @Override
//                        protected Void doInBackground(Object... params) {
//                            try {
//                                List<ExerciseItem> eList = (List<ExerciseItem>) params[0];
//                                HistoryUploader.updateHistory(context, eList, getRegistrationId());
//                            } catch (Exception ex) {
//                                ex.printStackTrace();
//                            }
//                            return null;
//                        }
//                    };
//                    uploadTask.execute((Object) entryList);

                    // update history
//                    final
//                    dataSource.insert(ExerciseItem.generateItem());

//                    AsyncTask uploadTask = new AsyncTask<Object, Void, Void>() {
//                        @Override
//                        protected Void doInBackground(Object... params) {
//
//                            DataSource dataSource = new DataSource(context);
//                            dataSource.open();
//
//                            List<ExerciseItem> entryList = (List<ExerciseItem>) params[0];
//                            for (ExerciseItem item : entryList) {
//                                dataSource.insert(item);
//                                Log.d("ArrayAdapter", "Inserted item with id: " + item.getId());
//                                try {
//                                    Thread.sleep(1000);
//                                } catch (InterruptedException e) {
//                                    e.printStackTrace();
//                                }
//                            }
//
//                            dataSource.close();

//                        }
//                }
//            });

            exerciseButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(context, ExerciseVideoChoices.class);
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

            ViewFlipper flipper = (ViewFlipper) row.findViewById(R.id.viewFlipper);
            CircleProgressBar bar1 = (CircleProgressBar) row.findViewById(R.id.custom_progressBar);
            CircleProgressBar bar2 = (CircleProgressBar) row.findViewById(R.id.custom_progressBar2);
            TextView progress1 = (TextView) row.findViewById(R.id.percentView);
            TextView progress2 = (TextView) row.findViewById(R.id.percentView2);

            TextView minutesLabel = (TextView) row.findViewById(R.id.minutes_label);

            bar1.setProgressWithAnimation(0);
            bar2.setProgressWithAnimation(0);

            SharedPreferences settingData = context.getSharedPreferences(SettingsActivity.SHARED_PREFERENCES_KEY, Context.MODE_PRIVATE);

            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
            int goal = Integer.parseInt(prefs.getString("exercise_time_key", "60"));

            long time = settingData.getLong(SettingsActivity.EXERCISE_TIME_KEY, 0);
            long minutes = (long) (time * 1.66667e-5);

            if (minutes == 1) {
                minutesLabel.setText("Minute");
            } else {
                minutesLabel.setText("Minutes");
            }

            String text = String.format("%d/%d", minutes, goal);
            bar1.setColor(0xFF29A629);
            bar1.setStrokeWidth(50);
            progress1.setText(text);
            bar1.setProgressWithAnimation(minutes / (float) goal * 100);

            bar2.setColor(0xFF0066FF);
            bar2.setStrokeWidth(50);
            int correct = settingData.getInt(SettingsActivity.CORRECT_SPEECH_KEY, 0);
            int total = settingData.getInt(SettingsActivity.TOTAL_SPEECH_KEY, 0);
            float percent = (float) correct / (float) total;
            if (total == 0) {
                bar2.setProgressWithAnimation(0);
            } else {
                bar2.setProgressWithAnimation(percent * 100);

                text = String.format("%d/%d\ncorrect", correct, total);
                progress2.setText(text);

            }
        } else {


            ExerciseItem item = data.get((data.size() - 1) - position);

            if (item == null) {
                return row;
            }


            int goal = (int) item.getExerciseGoalTime();

            long exerciseMillis = item.getExerciseTime();
            long minutes = (long) (exerciseMillis * 1.66667e-5);
            int exerciseProgress = (int) (minutes / (float) goal * 100);
            int speechProgress = (int) (item.getSpeechPercent() * 100);

            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            row = inflater.inflate(R.layout.history_row, parent, false);

            LineProgressBar speechBar = (LineProgressBar) row.findViewById(R.id.speech_bar);
            LineProgressBar exerciseBar = (LineProgressBar) row.findViewById(R.id.exercise_bar);

            TextView speechText = (TextView) row.findViewById(R.id.history_speech_text);
            TextView exerciseText = (TextView) row.findViewById(R.id.history_exercise_text);

            TextView date = (TextView) row.findViewById(R.id.dateTextView);

            Calendar c1 = Calendar.getInstance(); // today
            c1.add(Calendar.DAY_OF_YEAR, -1); // yesterday

            Calendar c = item.getDate();


            if (c1.get(Calendar.YEAR) == c.get(Calendar.YEAR)
                    && c1.get(Calendar.DAY_OF_YEAR) == c.get(Calendar.DAY_OF_YEAR)) {
                date.setText("Yesterday");
            } else {
                SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy");
                date.setText(format.format(c.getTime()));
            }


            speechText.setText(String.format("%d/%d", item.getSpeechCorrectCount(), item.getSpeechDoneCount()));
            exerciseText.setText(String.format("%d/%d min", minutes, goal));

            speechBar.setColor(0xFF0066FF);
            speechBar.setStrokeWidth(10);
            speechBar.setProgressWithAnimation(speechProgress);

            exerciseBar.setColor(0xFF29A629);
            exerciseBar.setStrokeWidth(10);
            exerciseBar.setProgressWithAnimation(exerciseProgress);

        }


        return row;
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
        return PreferenceManager.getDefaultSharedPreferences(context);
    }
}



