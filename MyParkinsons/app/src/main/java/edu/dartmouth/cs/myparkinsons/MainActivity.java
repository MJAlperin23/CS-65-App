package edu.dartmouth.cs.myparkinsons;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.Calendar;


public class MainActivity extends Activity {


    private static final long MS_PER_DAY = 86400000;

    private Button exerciseButton;
    private Button speechButton;

    private ProgressBar progressBar;
    private TextView progressBarTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        exerciseButton = (Button)findViewById(R.id.exerciseButton);
        speechButton = (Button)findViewById(R.id.speechButton);

        progressBar = (ProgressBar)findViewById(R.id.progressBar);
        progressBarTextView = (TextView)findViewById(R.id.progressBarText);

        progressBar.setMax(100);
        progressBar.setProgress(40);

        progressBarTextView.setText("2 miles out of 5 mile goal");

        exerciseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), ExerciseLogActivity.class);
                startActivity(i);
            }
        });

        speechButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), SpeechActivity.class);
                startActivity(i);
            }
        });

        // set up periodic notification requests
        setReminder(0L, "Hey Mickey", "You so fine, you so fine you blow my mind");

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.action_settings) {
            Intent settingsActivity = new Intent(this, SettingsActivity.class);
            startActivityForResult(settingsActivity, SettingsActivity.SETTINGS_ACTIVITY_KEY);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void setReminder(long time, String title, String message) {

        Intent alarmIntent = new Intent(this, SpeechReminderReceiver.class);
        alarmIntent.putExtra("message", message);
        alarmIntent.putExtra("title", title);

        int NOTIFICATION_REQUEST_CODE = 0;
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, NOTIFICATION_REQUEST_CODE, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        Log.d("setReminder()", "Setting a reminder");

        //TODO: For demo set after 5 seconds.
        alarmManager.setRepeating(
                AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime() + 5 * 1000,
                MS_PER_DAY, pendingIntent
        );

    }
}
