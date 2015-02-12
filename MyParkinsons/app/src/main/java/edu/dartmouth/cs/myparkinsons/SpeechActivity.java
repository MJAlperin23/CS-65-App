package edu.dartmouth.cs.myparkinsons;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;


//Voice recognition tutorial
//http://www.javacodegeeks.com/2012/08/android-voice-recognition-tutorial.html

public class SpeechActivity extends Activity {

    private static final int VOICE_REC_CODE = 0;
    private Button button;
    private TextView phrase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_speech);

        button = (Button)findViewById(R.id.button);
        phrase = (TextView)findViewById(R.id.phraseToRead);

        phrase.setText("This is a phrase to read to make my speech better");
        button.setText(R.string.buttonStart);

        checkVoiceRecognition();

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Button theButton = (Button) v.findViewById(R.id.button);
                String buttonText = button.getText().toString();

                if (buttonText.equals(getString(R.string.buttonStart))) {
                    button.setText("");
                    recordSpeech();
                } else if (buttonText.equals("")) {
                    button.setText(R.string.buttonStart);

                }



            }
        });
    }

    private void checkVoiceRecognition() {
        PackageManager packageManager = getPackageManager();
        List<ResolveInfo> activities = packageManager.queryIntentActivities(new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH), 0);
        if (activities.size() == 0) {
            button.setEnabled(false);
            button.setText("Voice Recognition not availible");
        }
    }

    private void recordSpeech() {
        Intent i = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);

        i.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, getClass().getPackage().getName());

        i.putExtra(RecognizerIntent.EXTRA_PROMPT, phrase.getText().toString());

        i.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);

        i.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 5);

        //provide audio url in the result
        i.putExtra("android.speech.extra.GET_AUDIO_FORMAT", "audio/AMR");
        i.putExtra("android.speech.extra.GET_AUDIO", true);

        startActivityForResult(i, VOICE_REC_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == VOICE_REC_CODE) {
            if (resultCode == RESULT_OK) {
                ArrayList<String> results = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                boolean understood = false;
                for (String aPhrase : results) {
                    if (aPhrase.toLowerCase().equals(phrase.getText().toString().toLowerCase())) {
                        phrase.setText("Great job! We understood what you said!");
                        break;
                    }
                }
                if (!understood) {
                    phrase.setText("Try again. We couldn't understand you.");
                }

                //http://stackoverflow.com/questions/23047433/record-save-audio-from-voice-recognition-intent
                Uri audioUri = data.getData();
//                ContentResolver contentResolver = getContentResolver();
//                try {
//                    InputStream filestream = contentResolver.openInputStream(audioUri);
//                } catch (FileNotFoundException e) {
//                    e.printStackTrace();
//                }
                // TODO: read audio file from inputstream

                MediaPlayer mediaPlayer = new MediaPlayer();
                try {
                    mediaPlayer.setDataSource(this, audioUri);
                    mediaPlayer.prepare();
                    mediaPlayer.start();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }

            //Result code for various error.
            else if(resultCode == RecognizerIntent.RESULT_AUDIO_ERROR){
                toast("Audio Error");
            }else if(resultCode == RecognizerIntent.RESULT_CLIENT_ERROR){
                toast("Client Error");
            }else if(resultCode == RecognizerIntent.RESULT_NETWORK_ERROR){
                toast("Network Error");
            }else if(resultCode == RecognizerIntent.RESULT_NO_MATCH){
                toast("No Match");
            }else if(resultCode == RecognizerIntent.RESULT_SERVER_ERROR){
                toast("Server Error");

            }

         }

    super.onActivityResult(requestCode, resultCode, data);
    }

    private void toast(String s) {
        Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_speech, menu);
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
