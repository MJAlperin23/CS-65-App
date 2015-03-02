package edu.dartmouth.cs.myparkinsons;

/**
 * Created by Andrew on 2/9/15.
 */

import android.app.Service;
import android.content.Context;
import android.content.Intent;
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
import android.os.RemoteException;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
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


    private SensorManager sensorManager;
    private Sensor accelerometer;

    private ArrayBlockingQueue<Double> accQueue;

    private WekaTask wekaTask;

    @Override
    public void onCreate() {
        super.onCreate();
        appContext = getApplicationContext();


        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);

        accQueue = new ArrayBlockingQueue<>(64);

        wekaTask = new WekaTask();

    }



    public Queue<Double> getQueue() {
        return accQueue;
    }

    public void reportClassification(Double value) {
        String string;
        if (value == 0) {
            string = "Standing";
        } else if (value == 1) {
            string = "Walking";
        } else {
            string = "Running";
        }
        Toast.makeText(getBaseContext(), string, Toast.LENGTH_SHORT).show();
        Log.d("TAG", string);

        sendActivityTypeForMap(value);
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
                accQueue = new ArrayBlockingQueue<Double>(64);
                accQueue.add(new Double(m));
            } else {
                ArrayBlockingQueue<Double> newBuf = new ArrayBlockingQueue<Double>(
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


    private void sendActivityTypeForMap(Double type) {

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
                    // registerTheReceiver();
                    break;
                case MSG_UNREGISTER_CLIENT:
                    //unregisterReciever();
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
            double[] im = new double[64];  //Globals.ACCELEROMETER_BLOCK_CAPACITY];

            double max = max(accBlock);

            ArrayList<Double> featVect = new ArrayList<>();

            FFT fft = new FFT(64);
            // Compute the re and im:
            // setting values of re and im by reference.
            fft.fft(re, im);

            for (int i = 0; i < re.length; i++) {
                // Compute each coefficient
                double mag = Math.sqrt(re[i] * re[i] + im[i]* im[i]);
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

            while(true) {
                if (isCancelled() == true) {
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
            reportClassification(values[0]);
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
    }

}
