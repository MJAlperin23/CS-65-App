package edu.dartmouth.cs.myparkinsons;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.gcm.GoogleCloudMessaging;
//taken from GCMDemoAndroid from lecture notes 24

public class GCMIntentService extends IntentService {

    private static final String TAG = "GCM Intent Service";

    public GCMIntentService() {
        super("GcmIntentService");
    }
	

	@Override
	protected void onHandleIntent(Intent intent) {
        Bundle extras = intent.getExtras();
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
        // The getMessageType() intent parameter must be the intent you received
        // in your BroadcastReceiver.
        String messageType = gcm.getMessageType(intent);

        if (!extras.isEmpty()) {  // has effect of unparcelling Bundle
            /*
             * Filter messages based on message type. Since it is likely that GCM
             * will be extended in the future with new message types, just ignore
             * any message types you're not interested in, or that you don't
             * recognize.
             */
            if (GoogleCloudMessaging.
                    MESSAGE_TYPE_SEND_ERROR.equals(messageType)) {
                //handle send error in here
            } else if (GoogleCloudMessaging.
                    MESSAGE_TYPE_DELETED.equals(messageType)) {
                Log.d(TAG, "recieved GCM msg type Deleted");
                String message = (String) extras.get("message");


                //handle delete message on server in here
            } else if (GoogleCloudMessaging.
                    MESSAGE_TYPE_MESSAGE.equals(messageType)) {
                Log.d(TAG, "message received");
            	// If it's a regular GCM message, do some work.
            	String message = (String) extras.get("message");
                Toast.makeText(this, message,Toast.LENGTH_SHORT).show();

    			Intent i = new Intent();
    			i.setAction("GCM_NOTIFY");
    			i.putExtra("message", message);
    			sendBroadcast(i);
            }
        }
        
        // Release the wake lock provided by the WakefulBroadcastReceiver.
        GcmBroadcastReceiver.completeWakefulIntent(intent);
	}

}
