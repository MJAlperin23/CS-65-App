package edu.dartmouth.cs.myparkinsons;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;


public class ExerciseVideoChoices extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercise_video_choices);

        final String[] videoTitles = new String[] {"Exercise Video 1", "Exercise Video 2", "Exercise Video 3",
                                     "Exercise Video 4", "Exercise Video 5", "Exercise Video 6"};
        final String[] videoDescriptions = new String[]
                {"An introduction to the video series and a basic arm lift and hold exercise.",
                "A hand eye coordination and strength exercise using tennis balls adn other small exercise balls.",
                "This video instructs how to do a leg wall stretch exercise",
                "Another hand strength and coordination video, also includes work with small dumbbells.",
                "This video features a coordination and strength exercise using special equipment the man created.",
                "This exercise focuses on coordinating involving the picking up and sorting of small objects."};

        ListView videoOptions = (ListView)findViewById(R.id.videoListView);

        String[] videoKeys = new String[] {"Exercise One","Exercise Two", "Exercise Three",
                                "Exercise Four", "Exercise Five", "Exercise Six"};

        ArrayAdapter<String> videoKeyAdapter = new ArrayAdapter<String>(this,
                R.layout.videolistlayout, videoKeys);

        videoOptions.setAdapter(videoKeyAdapter);

        videoOptions.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                switch (position) {

                    case 0:
                        Intent i1 = new Intent(getApplicationContext(), ExerciseVideoActivity.class);
                        i1.putExtra("VIDEO_ID_STRING", "FW5fNun0mUc");
                        i1.putExtra("VIDEO_TITLE_STRING", videoTitles[0]);
                        i1.putExtra("VIDEO_DESCRIPTION_STRING", videoDescriptions[0]);
                        startActivity(i1);
                        break;

                    case 1:
                        Intent i2 = new Intent(getApplicationContext(), ExerciseVideoActivity.class);
                        i2.putExtra("VIDEO_ID_STRING", "gTXIpLAwD18");
                        i2.putExtra("VIDEO_TITLE_STRING", videoTitles[1]);
                        i2.putExtra("VIDEO_DESCRIPTION_STRING", videoDescriptions[1]);
                        startActivity(i2);
                        break;

                    case 2:
                        Intent i3 = new Intent(getApplicationContext(), ExerciseVideoActivity.class);
                        i3.putExtra("VIDEO_ID_STRING", "U7zj73ueut4");
                        i3.putExtra("VIDEO_TITLE_STRING", videoTitles[2]);
                        i3.putExtra("VIDEO_DESCRIPTION_STRING", videoDescriptions[2]);
                        startActivity(i3);
                        break;

                    case 3:
                        Intent i4 = new Intent(getApplicationContext(), ExerciseVideoActivity.class);
                        i4.putExtra("VIDEO_ID_STRING", "PmGoncQ4LTw");
                        i4.putExtra("VIDEO_TITLE_STRING", videoTitles[3]);
                        i4.putExtra("VIDEO_DESCRIPTION_STRING", videoDescriptions[3]);
                        startActivity(i4);
                        break;

                    case 4:
                        Intent i5 = new Intent(getApplicationContext(), ExerciseVideoActivity.class);
                        i5.putExtra("VIDEO_ID_STRING", "-W09V1_u6oQ");
                        i5.putExtra("VIDEO_TITLE_STRING", videoTitles[4]);
                        i5.putExtra("VIDEO_DESCRIPTION_STRING", videoDescriptions[4]);
                        startActivity(i5);
                        break;

                    case 5:
                        Intent i6 = new Intent(getApplicationContext(), ExerciseVideoActivity.class);
                        i6.putExtra("VIDEO_ID_STRING", "h7aZmGK5Sg4");
                        i6.putExtra("VIDEO_TITLE_STRING", videoTitles[5]);
                        i6.putExtra("VIDEO_DESCRIPTION_STRING", videoDescriptions[5]);
                        startActivity(i6);
                        break;
                }
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_exercise_video_choices, menu);
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
