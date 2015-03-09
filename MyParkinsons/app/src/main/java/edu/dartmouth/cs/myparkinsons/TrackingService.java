package edu.dartmouth.cs.myparkinsons;

/**
 * Created by Andrew on 2/9/15.
 */

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.PowerManager;
import android.os.RemoteException;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;


public class TrackingService extends Service implements SensorEventListener {
    public static final String TYPE_KEY = "TYPE_KEY";
    public static final int MSG_SET_TYPE_VALUE = 5;
    private Context appContext;
    private List<Messenger> clients = new ArrayList<>();

    private final Messenger messenger = new Messenger(new IncomingMessageHandler());

    public static final int MSG_REGISTER_CLIENT = 1;
    public static final int MSG_UNREGISTER_CLIENT = 2;
    private static final String TAG = "TrackingService";


    private SensorManager sensorManager;
    private Sensor accelerometer;

    private ArrayBlockingQueue<Double> accQueue;

    private WekaTask wekaTask;

    private long dailyExerciseTime;
    private long lastExerciseChangedTime;
    private boolean isExercising;

    public SharedPreferences settingData;
    public static SharedPreferences.Editor spEdit;

    public static final int N_ID = 7779;

    // receiver stuff
    private IntentFilter mMessageIntentFilter;
    private BroadcastReceiver mMessageUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context c, Intent intent) {
            Bundle extras = intent.getExtras();
            String msg = extras.getString("message");
//            String sender = extras.getString("from_name");

            // TODO: display prompt notification when doctor sends ping
            if (msg != null) {
                Log.d(TAG, "Received message: " + msg);
                // build intent
                // build notification intent
                Intent goIntent = new Intent(c, SpeechActivity.class);
                int SPEECH_REQUEST_CODE = 0;
                PendingIntent wrapIntent = PendingIntent.getActivity(
                        c, SPEECH_REQUEST_CODE, goIntent, PendingIntent.FLAG_CANCEL_CURRENT
                );

                // get notification builder
                NotificationCompat.Builder builder = new NotificationCompat.Builder(c);

                // set notification parameters
                builder.setContentTitle("Doctor Alert");
                builder.setContentText(msg);
                builder.setSmallIcon(R.drawable.speech_pic);
                builder.setAutoCancel(true);
                builder.setContentIntent(wrapIntent);

                // send notification
                NotificationManager manager = (NotificationManager) c.getSystemService(Context.NOTIFICATION_SERVICE);
                manager.notify(N_ID, builder.build());
            }
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        appContext = getApplicationContext();

        dailyExerciseTime = 0;
        lastExerciseChangedTime = Calendar.getInstance().getTimeInMillis();
        isExercising = false;

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);

        accQueue = new ArrayBlockingQueue<>(64);

        wekaTask = new WekaTask();

        settingData = getSharedPreferences(SettingsActivity.SHARED_PREFERENCES_KEY, MODE_PRIVATE);
        spEdit = settingData.edit();

        // set up intent filter
        mMessageIntentFilter = new IntentFilter();
        mMessageIntentFilter.addAction("GCM_NOTIFY");
        this.registerReceiver(mMessageUpdateReceiver, mMessageIntentFilter);

    }


    public Queue<Double> getQueue() {
        return accQueue;
    }

    public void reportClassification(Double value) {
        sendActivityType(value);
    }

    @Override
    public boolean onUnbind(Intent intent) {

        return super.onUnbind(intent);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        double m = Math.sqrt(event.values[0] * event.values[0]
                + event.values[1] * event.values[1] + event.values[2]
                * event.values[2]);
        try {
            accQueue.add(new Double(m));
        } catch (IllegalStateException e) {

            // Exception happens when reach the capacity.
            // Doubling the buffer. ListBlockingQueue has no such issue,
            // But generally has worse performance
            int size = accQueue.size();
            if (size == 0) {
                //Sometimes queue size is zero. Make it 64 in that case
                accQueue = new ArrayBlockingQueue<>(64);
                accQueue.add(new Double(m));
            } else {
                ArrayBlockingQueue<Double> newBuf = new ArrayBlockingQueue<>(
                        accQueue.size() * 2);

                accQueue.drainTo(newBuf);
                accQueue = newBuf;
                accQueue.add(new Double(m));
            }

        }

    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        sensorManager.unregisterListener(this);
        wekaTask.cancel(true);
        super.onTaskRemoved(rootIntent);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public void onDestroy() {
        sensorManager.unregisterListener(this);
        wekaTask.cancel(true);
        unregisterReceiver(mMessageUpdateReceiver);
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_FASTEST);
        wekaTask.execute();
        return START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return messenger.getBinder();
    }

    private void sendActivityType(Double type) {

        Iterator<Messenger> messengerIterator = clients.iterator();
        while (messengerIterator.hasNext()) {
            Messenger messenger = messengerIterator.next();
            try {
                Bundle bundle = new Bundle();
                bundle.putDouble(TYPE_KEY, type);
                Message message = Message.obtain(null, MSG_SET_TYPE_VALUE);
                message.setData(bundle);
                messenger.send(message);
            } catch (RemoteException e) {
                clients.remove(messenger);
            }
        }
    }

    /**
     * Handle incoming messages from MainActivity
     */
    private class IncomingMessageHandler extends Handler { // Handler of

        // incoming messages
        // from clients.
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_REGISTER_CLIENT:
                    clients.add(msg.replyTo);
                    break;
                case MSG_UNREGISTER_CLIENT:
                    clients.remove(msg.replyTo);
                    break;
                default:
                    super.handleMessage(msg);
            }
        }
    }

    private class WekaTask extends AsyncTask<Void, Double, Void> {


        private double[] arrayFromQueue() {
            double[] accBlock = new double[64];
            for (int i = 0; i < 64; i++) {
                try {
                    accBlock[i] = accQueue.take();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            return accBlock;
        }


        private ArrayList<Double> createFeatureVector() {

            double[] accBlock = arrayFromQueue();
            double[] re = accBlock;
            double[] im = new double[64];

            double max = max(accBlock);

            ArrayList<Double> featVect = new ArrayList<>();

            FFT fft = new FFT(64);
            // Compute the re and im:
            // setting values of re and im by reference.
            fft.fft(re, im);

            for (int i = 0; i < re.length; i++) {
                // Compute each coefficient
                double mag = Math.sqrt(re[i] * re[i] + im[i] * im[i]);
                // Adding the computed FFT coefficient to the
                // featVect
                featVect.add(Double.valueOf(mag));
                // Clear the field
                im[i] = .0;
            }

            // Finally, append max after frequency components
            featVect.add(Double.valueOf(max));

            return featVect;
        }

        private double max(double[] accBlock) {
            double largest = accBlock[0];
            for (int i = 1; i < accBlock.length; i++) {
                if (accBlock[i] > largest) {
                    largest = accBlock[i];
                }
            }
            return largest;
        }

        @Override
        protected Void doInBackground(Void... params) {

            while (true) {
                if (isCancelled()) {
                    return null;
                }

                if (accQueue.size() >= 64) {
                    ArrayList<Double> featureVector = createFeatureVector();
                    try {
                        double type = WekaClassifier.classify(featureVector.toArray());

                        publishProgress(type);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

        }

        @Override
        protected void onProgressUpdate(Double... values) {

            Calendar c = Calendar.getInstance();
            Calendar old = Calendar.getInstance();
            old.setTimeInMillis(lastExerciseChangedTime);

            if (c.get(Calendar.DAY_OF_YEAR) != old.get(Calendar.DAY_OF_YEAR)) {
                if (isExercising) {
                    long time = c.getTimeInMillis();
                    long difference = time - lastExerciseChangedTime;

                    dailyExerciseTime += difference;

                    long previousTime = settingData.getLong(SettingsActivity.EXERCISE_TIME_KEY, 0);
                    spEdit.putLong(SettingsActivity.EXERCISE_TIME_KEY, difference + previousTime);
                    long date = settingData.getLong(SettingsActivity.CURRENT_DAY_KEY, 0);
                    if (date == 0) {
                        Calendar d = Calendar.getInstance();
                        int day = d.get(Calendar.DAY_OF_MONTH);
                        int month = d.get(Calendar.MONTH);
                        int year = d.get(Calendar.YEAR);
                        long theDate = year * 10000 + month * 100 + day;
                        spEdit.putLong(SettingsActivity.CURRENT_DAY_KEY, theDate);

                    }
                    spEdit.commit();
                }
                //New day add to database and clear data from prefs
                int total = settingData.getInt(SettingsActivity.TOTAL_SPEECH_KEY, 0);
                int correct = settingData.getInt(SettingsActivity.CORRECT_SPEECH_KEY, 0);

                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(appContext);
                boolean allowDataInsert = prefs.getBoolean("store_data_toggle_switch", true);

                long goalTime = Long.parseLong(prefs.getString(SettingsActivity.EXERCISE_TIME_KEY, "60"));

                ExerciseItem item = new ExerciseItem(old, total, correct, dailyExerciseTime, goalTime);
                dailyExerciseTime = 0;
                spEdit.putLong(SettingsActivity.EXERCISE_TIME_KEY, 0);
                spEdit.putInt(SettingsActivity.CORRECT_SPEECH_KEY, 0);
                spEdit.putInt(SettingsActivity.TOTAL_SPEECH_KEY, 0);
                DataSource dataSource = new DataSource(appContext);

                lastExerciseChangedTime = c.getTimeInMillis();
                isExercising = false;
                if (allowDataInsert) {
                    dataSource.open();
                    dataSource.insert(item);
                    dataSource.close();
                    uploadData();
                }
            }

            double type = values[0];
            if (isExercising) {
                if (type == 0) {
                    System.out.println("Switched to not exercising!!");

                    long time = c.getTimeInMillis();
                    long difference = time - lastExerciseChangedTime;



                    dailyExerciseTime += difference;

                    long previousTime = settingData.getLong(SettingsActivity.EXERCISE_TIME_KEY, 0);
                    spEdit.putLong(SettingsActivity.EXERCISE_TIME_KEY, difference + previousTime);
                    long date = settingData.getLong(SettingsActivity.CURRENT_DAY_KEY, 0);
                    if (date == 0) {
                        Calendar d = Calendar.getInstance();
                        int day = d.get(Calendar.DAY_OF_MONTH);
                        int month = d.get(Calendar.MONTH);
                        int year = d.get(Calendar.YEAR);
                        long theDate = year * 10000 + month * 100 + day;
                        spEdit.putLong(SettingsActivity.CURRENT_DAY_KEY, theDate);

                    }
                    spEdit.commit();
                    isExercising = false;

                }
            } else {
                if (type == 1 || type == 2) {       //walking or running
                    System.out.println("Switched to exercising!!");
                    isExercising = true;
                    lastExerciseChangedTime = Calendar.getInstance().getTimeInMillis();
                }
            }

            //reportClassification(values[0]);
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
        }

        private void uploadData() {

            DataSource dataSource = new DataSource(appContext);
            dataSource.open();
            List<ExerciseItem> entryList = dataSource.fetchItems();
            dataSource.close();

            AsyncTask uploadTask = new AsyncTask<List, Void, Void>() {
                @Override
                protected Void doInBackground(List... params) {

                    List<ExerciseItem> entryList = (List<ExerciseItem>) params[0];
                    HistoryUploader.updateHistory(appContext, entryList, getRegistrationId(appContext));

                    Void v = null;
                    return v;
                }
            };

            uploadTask.execute(entryList);
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
        private SharedPreferences getGCMPreferences(Context context) {
            // This sample app persists the registration ID in shared preferences,
            // but
            // how you store the regID in your app is up to you.
            return PreferenceManager.getDefaultSharedPreferences(context);
        }
    }


}
