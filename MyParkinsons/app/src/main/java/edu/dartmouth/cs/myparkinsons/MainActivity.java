package edu.dartmouth.cs.myparkinsons;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.ServiceConnection;
import android.location.Location;
import android.os.AsyncTask;
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

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Random;


// TODO: set up Google Play with API console
// TODO: set up server addressing

public class MainActivity extends FragmentActivity implements ServiceConnection {


    public static final String FIRST_TIME_KEY = "first_time";
    public static final String PROPERTY_REG_ID = "reg_id_field";
    public static final String PROPERTY_APP_VERSION = "app_version_field";
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    private static final String TAG = "MyP Main";

    private Messenger serviceMessenger = null;
    boolean isBound;
    private final Messenger messenger = new Messenger(
            new IncomingMessageHandler());

    private ServiceConnection connection = this;
    private DataSource dataSource;

    //http://www.learn-android-easily.com/2013/06/android-viewflipper-example.html
    private float lastX;
    private float lastY;

    private ExerciseLogArrayAdapter adapter;
    private List<ExerciseItem> list;

    // references for google play
    private Context context;
    private GoogleCloudMessaging gcm;
    public String regId;

    /**
     * Substitute you own sender ID here. This is the project number you got
     * from the API Console.
     */
    public String SENDER_ID = "893850931182";

    private IntentFilter mMessageIntentFilter;
    private BroadcastReceiver mMessageUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle extras = intent.getExtras();
            String msg = extras.getString("message");
//            long id = Long.getLong(msg);
            Log.d(TAG, "got request " + msg);

            // TODO: display prompt notification when doctor sends ping
            if (msg != null) {
                Log.d(TAG, "Received message: " + msg);
//                dataSource.open();
//                dataSource.deleteEntry(msg);
//                dataSource.close();
            }
        }
    };


    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // set up periodic notification requests
//        SharedPreferences prefs = getSharedPreferences(SettingsActivity.SHARED_PREFERENCES_KEY, MODE_PRIVATE);

        // set up intent filter
        mMessageIntentFilter = new IntentFilter();
        mMessageIntentFilter.addAction("GCM_NOTIFY");

        // set up references
        context = getApplicationContext();

        // check device for Play Services APK
        String services = Boolean.toString(checkPlayServices());
        Log.d(TAG, "Play services? " + services);
        if (checkPlayServices()) {
            gcm = GoogleCloudMessaging.getInstance(this);
            regId = getRegistrationId(context);
            if (regId.isEmpty()) {
                registerInBackground();
            }
            Log.d(TAG, "regId: " + regId);
        }

        if (!isMyServiceRunning(TrackingService.class)) {
            // set up service
            startService(new Intent(MainActivity.this, TrackingService.class));
            doBindService();
        }

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


    //http://stackoverflow.com/questions/600207/how-to-check-if-a-service-is-running-on-android
    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    @Override
    protected void onDestroy() {
        doUnbindService();
        super.onDestroy();
    }

    @Override
    protected void onResume() {

        registerReceiver(mMessageUpdateReceiver, mMessageIntentFilter);
        if (list != null)
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

            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);

            long goalTime = Long.parseLong(prefs.getString(SettingsActivity.EXERCISE_TIME_KEY, "60"));

            items[i] = new ExerciseItem(c, totalSpeech, totalCorrect, walking, goalTime);
//                      dataSource.insert(items[i]);
        }


        //Insert two null items because the first two cards in the list are not history stuff
        list.addAll(dataSource.fetchItems());
        list.add(new ExerciseItem(null, 0, 0, 0, 60));
        list.add(new ExerciseItem(null, 0, 0, 0, 60));

        dataSource.close();
        adapter = new ExerciseLogArrayAdapter(this, R.layout.exercise_log_row, list);
        listView.setAdapter(adapter);

        listView.invalidateViews();

        super.onResume();
    }

    private void uploadData() {

        dataSource = new DataSource(this);
        dataSource.open();
        List<ExerciseItem> entryList = dataSource.fetchItems();
        dataSource.close();

        AsyncTask uploadTask = new AsyncTask<List, Void, Void>() {
            @Override
            protected Void doInBackground(List... params) {

                List<ExerciseItem> entryList = (List<ExerciseItem>) params[0];
                HistoryUploader.updateHistory(context, entryList, regId);

                Void v = null;
                return v;
            }
        };

        uploadTask.execute(entryList);
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

        SharedPreferences sharedPreferences = getSharedPreferences(SettingsActivity.SHARED_PREFERENCES_KEY, MODE_PRIVATE);
        boolean isFirstTime = sharedPreferences.getBoolean(FIRST_TIME_KEY, true);

        if (isFirstTime) {
            Intent i = new Intent(this, WelcomeActivity.class);
            startActivity(i);
        }

        super.onPostResume();
    }

    @Override
    protected void onPause() {
        unregisterReceiver(mMessageUpdateReceiver);
        super.onPause();
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

    ////////// TAKEN FROM POSTACTIVITY FROM LECTURE NOTES: ADAPTED BY TDC ON 2/23

    /**
     * Check the device to make sure it has the Google Play Services APK. If it
     * doesn't, display a dialog that allows users to download the APK from the
     * Google Play Store or enable it in the device's system settings.
     */
    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil
                .isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                finish();
            }
            return false;
        }
        return true;
    }

    /**
     * Gets the current registration ID for application on GCM service.
     * <p>
     * If result is empty, the app needs to register.
     *
     * @return registration ID, or empty string if there is no existing
     *         registration ID.
     */
    private String getRegistrationId(Context context) {
        final SharedPreferences prefs = getGCMPreferences(context);
        String registrationId = prefs.getString(PROPERTY_REG_ID, "");
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
    private SharedPreferences getGCMPreferences(Context context) {
        // This sample app persists the registration ID in shared preferences,
        // but
        // how you store the regID in your app is up to you.
        return PreferenceManager.getDefaultSharedPreferences(context);
    }

    /**
     * @return Application's version code from the {@code PackageManager}.
     */
    private static int getAppVersion(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            // should never happen
            throw new RuntimeException("Could not get package name: " + e);
        }
    }

    /**
     * Registers the application with GCM servers asynchronously.
     * <p>
     * Stores the registration ID and app versionCode in the application's
     * shared preferences.
     */
    private void registerInBackground() {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                String msg = "";
              //  try {
                    if (gcm == null) {
                        Log.d("Registration", "attempting to get google cloud messaging instance");
                        gcm = GoogleCloudMessaging.getInstance(context);
                        Log.d("Registration", "got new GCM instance");
                    }
            //        regId = gcm.register(SENDER_ID);
                    msg = "Device registered, registration ID=" + regId;

                    // You should send the registration ID to your server over
                    // HTTP,
                    // so it can use GCM/HTTP or CCS to send messages to your
                    // app.
                    // The request to your server should be authenticated if
                    // your app
                    // is using accounts.
//                    ServerUtilities.sendRegistrationIdToBackend(context, regId);

                    // For this demo: we don't need to send it because the
                    // device
                    // will send upstream messages to a server that echo back
                    // the
                    // message using the 'from' address in the message.

                    // Persist the regID - no need to register again.
//                    storeRegistrationId(context, regId);
//              / } catch (IOException ex) {
//                    msg = "Error :" + ex.getMessage();
//                    // If there is an error, don't just keep trying to register.
//                    // Require the user to click a button again, or perform
//                    // exponential back-off.
//                }
                return msg;
            }

            @Override
            protected void onPostExecute(String msg) {
                Log.i(TAG, "gcm register msg: " + msg);

            }
        }.execute(null, null, null);
    }

    /**
     * Stores the registration ID and app versionCode in the application's
     * {@code SharedPreferences}.
     *
     * @param context
     *            application's context.
     * @param regId
     *            registration ID
     */
    private void storeRegistrationId(Context context, String regId) {
        final SharedPreferences prefs = getGCMPreferences(context);
        int appVersion = getAppVersion(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(PROPERTY_REG_ID, regId);
        editor.putInt(PROPERTY_APP_VERSION, appVersion);
        editor.commit();
    }



}

