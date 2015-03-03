package edu.dartmouth.cs.myparkinsons;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
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
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ViewFlipper;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Random;


public class MainActivity extends FragmentActivity implements ServiceConnection {


//    private Button exerciseButton;
//    private Button speechButton;


    private Messenger serviceMessenger = null;
    boolean isBound;
//    public CircleProgressFragment cpf;
//    public SpeechCircleProgressFragment scpf;
    private final Messenger messenger = new Messenger(
            new IncomingMessageHandler());

    private ServiceConnection connection = this;

    //http://www.learn-android-easily.com/2013/06/android-viewflipper-example.html
//    private ViewFlipper viewFlipper;
    private float lastX;


    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


//        viewFlipper = (ViewFlipper) findViewById(R.id.viewFlipper);
//
//
//        if (savedInstanceState == null) {
//            getSupportFragmentManager().beginTransaction()
//                    .add(R.id.container, new CircleProgressFragment())
//                    .commit();
//            getSupportFragmentManager().beginTransaction()
//                    .add(R.id.container2, new SpeechCircleProgressFragment())
//                    .commit();
//        }
//        cpf = new CircleProgressFragment();
//        scpf = new SpeechCircleProgressFragment();

//        exerciseButton = (Button)findViewById(R.id.exerciseButton);
//        speechButton = (Button)findViewById(R.id.speechButton);


//        exerciseButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent i = new Intent(getApplicationContext(), ExerciseLogActivity.class);
//                startActivity(i);
//            }
//        });
//
//        speechButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent i = new Intent(getApplicationContext(), SpeechActivity.class);
//                startActivity(i);
//            }
//        });

        // set up periodic notification requests
        SharedPreferences prefs = getSharedPreferences(SettingsActivity.SHARED_PREFERENCES_KEY, MODE_PRIVATE);

        startService(new Intent(MainActivity.this, TrackingService.class));
        doBindService();

        ExerciseItem[] items = new ExerciseItem[10];

        for (int i = 0; i < 10; i++) {
            Random rand = new Random();
            long walking = Math.abs(rand.nextLong()) % 86400000;
            int month = (rand.nextInt() % 12) + 1;
            int day = (rand.nextInt() % 28) + 1;
            int year = (rand.nextInt() % 2015) + 1;
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.MONTH, month);
            calendar.set(Calendar.DAY_OF_MONTH, day);
            calendar.set(Calendar.YEAR, year);

            int totalSpeech = (Math.abs(rand.nextInt()) % 12) + 1;
            int totalCorrect = Math.abs(rand.nextInt()) % totalSpeech;

            items[i] = new ExerciseItem(calendar, totalSpeech, totalCorrect, walking);
        }
        List<ExerciseItem> list = Arrays.asList(items);
        ExerciseLogArrayAdapter adapter = new ExerciseLogArrayAdapter(this, R.layout.exercise_log_row, list);

        listView = (ListView) findViewById(R.id.card_listView);

        listView.setAdapter(adapter);

        listView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                    ListView listview1 = (ListView) v;
                    View view = getViewByPosition(0, listView);
                    ViewFlipper viewFlipper = (ViewFlipper)view.findViewById(R.id.viewFlipper);
                    switch (event.getAction()) {
                        // when user first touches the screen to swap
                        case MotionEvent.ACTION_DOWN: {
                            lastX = event.getX();
                            break;
                        }
                        case MotionEvent.ACTION_UP: {
                            float currentX = event.getX();

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
                                CircleProgressBar bar = (CircleProgressBar)view.findViewById(R.id.custom_progressBar);
                                bar.setProgress(0);
                                bar.setProgressWithAnimation(33);
                                //CircleProgressFragment.setCircleProgress(33);
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
                                CircleProgressBar bar = (CircleProgressBar)view.findViewById(R.id.custom_progressBar2);
                                bar.setProgress(0);
                                bar.setProgressWithAnimation(67);
                                //SpeechCircleProgressFragment.setCircleProgress(67);
                            }
                            break;
                        }
                    }

                    return false;

            }
        });

    }

    public View getViewByPosition(int pos, ListView listView) {
        final int firstListItemPosition = listView.getFirstVisiblePosition();
        final int lastListItemPosition = firstListItemPosition + listView.getChildCount() - 1;

        if (pos < firstListItemPosition || pos > lastListItemPosition ) {
            return listView.getAdapter().getView(pos, null, listView);
        } else {
            final int childIndex = pos - firstListItemPosition;
            return listView.getChildAt(childIndex);
        }
    }

//    @Override
//    public boolean onTouchEvent(MotionEvent touchevent)
//    {
//        super.onTouchEvent(touchevent);
//
//        switch (touchevent.getAction())
//        {
//            // when user first touches the screen to swap
//            case MotionEvent.ACTION_DOWN:
//            {
//                lastX = touchevent.getX();
//                break;
//            }
//            case MotionEvent.ACTION_UP:
//            {
//                float currentX = touchevent.getX();
//
//                // if left to right swipe on screen
//                if (lastX < currentX)
//                {
//                    // If no more View/Child to flip
//                    if (viewFlipper.getDisplayedChild() == 0)
//                        break;
//
//                    // set the required Animation type to ViewFlipper
//                    // The Next screen will come in form Left and current Screen will go OUT from Right
//                    viewFlipper.setInAnimation(this, R.anim.in_from_left);
//                    viewFlipper.setOutAnimation(this, R.anim.out_to_right);
//                    // Show the next Screen
//                    viewFlipper.showNext();
//                    CircleProgressFragment.setCircleProgress(33);
//                }
//
//                // if right to left swipe on screen
//                if (lastX > currentX)
//                {
//                    if (viewFlipper.getDisplayedChild() == 1)
//                        break;
//                    // set the required Animation type to ViewFlipper
//                    // The Next screen will come in form Right and current Screen will go OUT from Left
//                    viewFlipper.setInAnimation(this, R.anim.in_from_right);
//                    viewFlipper.setOutAnimation(this, R.anim.out_to_left);
//                    // Show The Previous Screen
//                    viewFlipper.showPrevious();
//                    SpeechCircleProgressFragment.setCircleProgress(67);
//                }
//                break;
//            }
//        }
//        return false;
//    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
//        CircleProgressFragment.setCircleProgress(33);
//        SpeechCircleProgressFragment.setCircleProgress(67);
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

//    /**
//     * fragement holding/controlling the circle progress bar on the main page
//     */
//    //TODO: write method to set progress bar
//    public static class CircleProgressFragment extends Fragment {
//
//        public static CircleProgressBar circleProgressBar;
//        public static TextView textView;
//
//
//        public CircleProgressFragment() {
//        }
//
//        @Override
//        public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                                 Bundle savedInstanceState) {
//            View rootView = inflater.inflate(R.layout.fragment_cirlce_progress, container, false);
//
//            circleProgressBar = (CircleProgressBar) rootView.findViewById(R.id.custom_progressBar);
//            circleProgressBar.setColor(0xFF29A629);
//            circleProgressBar.setStrokeWidth(50);
//
//            textView = (TextView) rootView.findViewById(R.id.percentView);
//            textView.setText("33%");
//            textView.setTextColor(0xFF29A629);
//
//            return rootView;
//        }
//
//        public static void setCircleProgress(int value) {
//            circleProgressBar.setProgress(0);
//            circleProgressBar.setProgressWithAnimation(value);
//        }
//    }
//
//    public static class SpeechCircleProgressFragment extends Fragment {
//
//        public static CircleProgressBar circleProgressBar;
//        public static TextView textView;
//
//        public SpeechCircleProgressFragment() {
//        }
//
//        @Override
//        public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                                 Bundle savedInstanceState) {
//            View rootView = inflater.inflate(R.layout.fragment_cirlce_progress, container, false);
//
//            circleProgressBar = (CircleProgressBar) rootView.findViewById(R.id.custom_progressBar);
//            circleProgressBar.setColor(0xFF0066FF);
//            circleProgressBar.setStrokeWidth(50);
//
//            textView = (TextView) rootView.findViewById(R.id.percentView);
//            textView.setText("67%");
//            textView.setTextColor(0xFF0066FF);
//
//
//            return rootView;
//        }
//
//
//
//        public static void setCircleProgress(int value) {
//            circleProgressBar.setProgress(0);
//            circleProgressBar.setProgressWithAnimation(value);
//        }
//    }




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
