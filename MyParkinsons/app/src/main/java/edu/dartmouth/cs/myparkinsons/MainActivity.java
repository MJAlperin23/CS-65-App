package edu.dartmouth.cs.myparkinsons;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
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
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.Calendar;


public class MainActivity extends Activity implements ServiceConnection {


    private static final long MS_PER_DAY = 86400000;

    private Button exerciseButton;
    private Button speechButton;

    private ProgressBar progressBar;
    private TextView progressBarTextView;


    private Messenger serviceMessenger = null;
    boolean isBound;

    private final Messenger messenger = new Messenger(
            new IncomingMessageHandler());

    private ServiceConnection connection = this;


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


        startService(new Intent(MainActivity.this, TrackingService.class));
        doBindService();

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
