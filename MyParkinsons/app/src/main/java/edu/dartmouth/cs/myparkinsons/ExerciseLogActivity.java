package edu.dartmouth.cs.myparkinsons;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import java.util.Calendar;
import java.util.Date;
import java.util.Random;


public class ExerciseLogActivity extends Activity {

    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercise_log);

        listView = (ListView)findViewById(R.id.exerciseListView);

        ExerciseItem[] items = new ExerciseItem[20];

        for (int i = 0; i < 20; i++) {
            Random rand = new Random();
            double walking = rand.nextDouble();
            double running = rand.nextDouble();
            int month = (rand.nextInt() % 12) + 1;
            int day = (rand.nextInt() % 28) + 1;
            int year = (rand.nextInt() % 2015) + 1;
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.MONTH, month);
            calendar.set(Calendar.DAY_OF_MONTH, day);
            calendar.set(Calendar.YEAR, year);
            Date date = calendar.getTime();
            boolean checked = (rand.nextBoolean());

            items[i] = new ExerciseItem(date, checked, walking, running);
        }

        ExerciseLogArrayAdapter adapter = new ExerciseLogArrayAdapter(this, R.layout.exercise_log_row, items);

        listView.setAdapter(adapter);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_exercise_log, menu);
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
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
