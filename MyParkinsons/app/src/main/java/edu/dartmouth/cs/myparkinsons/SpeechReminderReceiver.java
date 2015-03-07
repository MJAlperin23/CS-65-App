package edu.dartmouth.cs.myparkinsons;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

/**
 * Created by tdc on 2/26/15
 *
 * Receiver for receiving Alarm Manager intent and firing a notification
 * to request the user to record speech.
 */
public class SpeechReminderReceiver extends BroadcastReceiver {

    public static final int N_ID = 7777;

    @Override
    public void onReceive(Context c, Intent i) {
        Log.d("onReceive()", "Received AlarmManager broadcast");

        String message = i.getStringExtra("message");
        String title = i.getStringExtra("title");
        SharedPreferences settingData = c.getSharedPreferences(SettingsActivity.SHARED_PREFERENCES_KEY, SettingsActivity.MODE_PRIVATE);
        // build notification intent
        Intent goIntent = new Intent(c, SpeechActivity.class);
        int SPEECH_REQUEST_CODE = 0;
        PendingIntent wrapIntent = PendingIntent.getActivity(
                c, SPEECH_REQUEST_CODE, goIntent, PendingIntent.FLAG_CANCEL_CURRENT
        );


        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(c);
        boolean allowNotification = prefs.getBoolean("speech_alert_toggle_switch", true);

        if(allowNotification) {
            // get notification builder
            NotificationCompat.Builder builder = new NotificationCompat.Builder(c);

            // set notification parameters
            builder.setContentTitle(title);
            builder.setContentText(message);
            builder.setSmallIcon(R.drawable.speech_pic);
            builder.setAutoCancel(true);
            builder.setContentIntent(wrapIntent);

            // send notification
            NotificationManager manager = (NotificationManager) c.getSystemService(Context.NOTIFICATION_SERVICE);
            manager.notify(N_ID, builder.build());

        }

    }

}
