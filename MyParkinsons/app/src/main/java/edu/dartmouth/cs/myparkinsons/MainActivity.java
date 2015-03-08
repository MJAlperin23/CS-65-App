package edu.dartmouth.cs.myparkinsons;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.ServiceConnection;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.Parcelable;
import android.os.RemoteException;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Random;


public class MainActivity extends FragmentActivity implements ServiceConnection {


    private Messenger serviceMessenger = null;
    boolean isBound;
    private final Messenger messenger = new Messenger(
            new IncomingMessageHandler());

    private ServiceConnection connection = this;

    //http://www.learn-android-easily.com/2013/06/android-viewflipper-example.html
    private float lastX;
    private float lastY;

    private ExerciseLogArrayAdapter adapter;
    private List<ExerciseItem> list;


    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // set up periodic notification requests
        SharedPreferences prefs = getSharedPreferences(SettingsActivity.SHARED_PREFERENCES_KEY, MODE_PRIVATE);

        startService(new Intent(MainActivity.this, TrackingService.class));
        doBindService();

        list = new ArrayList<>();
        listView = (ListView) findViewById(R.id.card_listView);

        listView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                View view = getViewByPosition(1, listView);
                ViewFlipper viewFlipper = (ViewFlipper) view.findViewById(R.id.viewFlipper);


                switch (event.getAction()) {
                    // when user first touches the screen to swap
                    case MotionEvent.ACTION_DOWN: {
                        lastX = event.getX();
                        lastY = event.getY();
                        break;
                    }
                    case MotionEvent.ACTION_UP: {
                        float currentX = event.getX();
                        float currentY = event.getY();

                        if (Math.abs(currentY - lastY) > Math.abs(currentX - lastX)) {
                            return false;
                        }

                        // if left to right swipe on screen
                        if (lastX < currentX) {
                            // If no more View/Child to flip
                            if (viewFlipper.getDisplayedChild() == 0)
                                break;

                            // set the required Animation type to ViewFlipper
                            // The Next screen will come in form Left and current Screen will go OUT from Right
                            viewFlipper.setInAnimation(getApplicationContext(), R.anim.in_from_left);
                            viewFlipper.setOutAnimation(getApplicationContext(), R.anim.out_to_right);
                            // Show the next Screen
                            viewFlipper.showNext();

                            refreshExerciseView(view);

                        }

                        // if right to left swipe on screen
                        if (lastX > currentX) {
                            if (viewFlipper.getDisplayedChild() == 1)
                                break;
                            // set the required Animation type to ViewFlipper
                            // The Next screen will come in form Right and current Screen will go OUT from Left
                            viewFlipper.setInAnimation(getApplicationContext(), R.anim.in_from_right);
                            viewFlipper.setOutAnimation(getApplicationContext(), R.anim.out_to_left);
                            // Show The Previous Screen
                            viewFlipper.showPrevious();

                            refreshSpeechView(view);
                        }
                        break;
                    }
                }

                return false;
            }
        });

    }

    @Override
    protected void onResume() {

        if(list != null)
            list.clear();

        ExerciseItem[] items = new ExerciseItem[10];
        DataSource dataSource = new DataSource(this);
        dataSource.open();

        Calendar c = Calendar.getInstance();
        int currDay = c.get(Calendar.DAY_OF_YEAR);

        for (int i = 0; i < 10; i++) {
            Random rand = new Random();
            long walking = Math.abs(rand.nextLong()) % 3600000;

            c.set(Calendar.DAY_OF_YEAR, currDay - 10 + i);

            int totalSpeech = (Math.abs(rand.nextInt()) % 12) + 1;
            int totalCorrect = Math.abs(rand.nextInt()) % totalSpeech;


            items[i] = new ExerciseItem(c, totalSpeech, totalCorrect, walking);
  //          dataSource.insert(items[i]);
        }


        //Insert two null items because the first two cards in the list are not history stuff
        list.addAll(dataSource.fetchItems());
        list.add(new ExerciseItem(null, 0, 0, 0));
        list.add(new ExerciseItem(null, 0, 0, 0));

        dataSource.close();
        adapter = new ExerciseLogArrayAdapter(this, R.layout.exercise_log_row, list);
        listView.setAdapter(adapter);

        listView.invalidateViews();

        super.onResume();
    }

    private void refreshSpeechView(View view) {
        CircleProgressBar bar = (CircleProgressBar) view.findViewById(R.id.custom_progressBar2);
        TextView textView = (TextView) view.findViewById(R.id.percentView2);

        ImageView leftDot = (ImageView) view.findViewById(R.id.left_circle);
        ImageView rightDot = (ImageView) view.findViewById(R.id.right_circle);
        leftDot.setImageResource(R.drawable.light_circle);
        rightDot.setImageResource(R.drawable.dark_circle
        );

        bar.setProgress(0);

        SharedPreferences settingData = getSharedPreferences(SettingsActivity.SHARED_PREFERENCES_KEY, MODE_PRIVATE);
        int correct = settingData.getInt(SettingsActivity.CORRECT_SPEECH_KEY, 0);
        int total = settingData.getInt(SettingsActivity.TOTAL_SPEECH_KEY, 0);
        float percent = (float) correct / (float) total;
        System.out.println(percent);
        if (total == 0) {
            bar.setProgressWithAnimation(0);
        } else {
            bar.setProgressWithAnimation(percent * 100);
        }
        String text = String.format("%d/%d", correct, total);
        textView.setText(text);
    }

    public void refreshExerciseView(View view) {
        CircleProgressBar bar = (CircleProgressBar) view.findViewById(R.id.custom_progressBar);
        TextView textView = (TextView) view.findViewById(R.id.percentView);

        ImageView leftDot = (ImageView) view.findViewById(R.id.left_circle);
        ImageView rightDot = (ImageView) view.findViewById(R.id.right_circle);
        leftDot.setImageResource(R.drawable.dark_circle);
        rightDot.setImageResource(R.drawable.light_circle);

        TextView minutesLabel = (TextView) view.findViewById(R.id.minutes_label);

        bar.setProgress(0);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        int goal = Integer.parseInt(prefs.getString("exercise_time_key", "60"));

        SharedPreferences settingData = getSharedPreferences(SettingsActivity.SHARED_PREFERENCES_KEY, MODE_PRIVATE);

        long time = settingData.getLong(SettingsActivity.EXERCISE_TIME_KEY, 0);
        long minutes = (long) (time * 1.66667e-5);

        if (minutes == 1) {
            minutesLabel.setText("Minute");
        } else {
            minutesLabel.setText("Minutes");
        }

        String text = String.format("%d/%d", minutes, goal);
        textView.setText(text);
        bar.setProgressWithAnimation(minutes / (float) goal * 100);
    }

    public View getViewByPosition(int pos, ListView listView) {
        final int firstListItemPosition = listView.getFirstVisiblePosition();
        final int lastListItemPosition = firstListItemPosition + listView.getChildCount() - 1;

        if (pos < firstListItemPosition || pos > lastListItemPosition) {
            return listView.getAdapter().getView(pos, null, listView);
        } else {
            final int childIndex = pos - firstListItemPosition;
            return listView.getChildAt(childIndex);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_settings_access, menu);
        return true;
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();

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

    /**
     * Bind this Activity to TimerService
     */
    private void doBindService() {
        bindService(new Intent(this, TrackingService.class), connection,
                Context.BIND_AUTO_CREATE);
        isBound = true;
    }

    /**
     * Un-bind this Activity to TimerService
     */
    private void doUnbindService() {
        if (isBound) {
            // If we have received the service, and hence registered with it,
            // then now is the time to unregister.
            if (serviceMessenger != null) {
                try {
                    Message msg = Message.obtain(null,
                            TrackingService.MSG_UNREGISTER_CLIENT);
                    msg.replyTo = messenger;
                    serviceMessenger.send(msg);
                } catch (RemoteException e) {
                    // There is nothing special we need to do if the service has
                    // crashed.
                }
            }
            // Detach our existing connection.
            unbindService(connection);
            isBound = false;
        }
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        serviceMessenger = new Messenger(service);
        try {
            Message msg = Message.obtain(null, TrackingService.MSG_REGISTER_CLIENT);
            msg.replyTo = messenger;
            serviceMessenger.send(msg);
        } catch (RemoteException e) {
            // In this case the service has crashed before we could even do
            // anything with it
        }
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        // This is called when the connection with the service has been
        // unexpectedly disconnected - process crashed.
        serviceMessenger = null;
    }


    private class IncomingMessageHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {

            switch (msg.what) {
                case TrackingService.MSG_SET_TYPE_VALUE:
                    sendNewType(msg.getData().getDouble(TrackingService.TYPE_KEY));

                    break;
            }
        }
    }

    private void sendNewType(Double type) {


    }

}

