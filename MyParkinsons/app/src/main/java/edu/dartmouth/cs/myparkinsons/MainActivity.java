package edu.dartmouth.cs.myparkinsons;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;


public class MainActivity extends Activity {


    private static final int SETTINGS_ACTIVITY_KEY = 1;

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

        SentenceMaker sentenceMaker = new SentenceMaker();
        sentenceMaker.generateRandomSentence(this);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent settingsActivity = new Intent(this, SettingsActivity.class);
            startActivityForResult(settingsActivity, SETTINGS_ACTIVITY_KEY);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
