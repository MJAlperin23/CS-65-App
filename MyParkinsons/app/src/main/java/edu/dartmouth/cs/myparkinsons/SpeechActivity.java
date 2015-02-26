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
import android.util.Log;
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
    private Button recordButton;
    private Button replayButton;
    private TextView phrase;

    private Uri audioUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_speech);

        recordButton = (Button)findViewById(R.id.recordButton);
        replayButton = (Button)findViewById(R.id.playbackButton);

        phrase = (TextView)findViewById(R.id.phraseToRead);

        phrase.setText("This app helps improve my speech");
        recordButton.setText(R.string.buttonStart);

        replayButton.setText(R.string.buttonPlay);
        replayButton.setEnabled(false);

        checkVoiceRecognition();

        recordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = recordButton.getText().toString();

                if (text.equals(getString(R.string.buttonStart))) {
                    recordSpeech();
                } else if (text.equals(getString(R.string.newphrase))) {
                    generateNewPhrase();
                }
            }
        });

        replayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playBackAudio();

            }
        });


        SentenceMaker sentenceMaker = new SentenceMaker();
        sentenceMaker.generateRandomSentence(getApplicationContext(), phrase);
    }

    private void generateNewPhrase() {
        SentenceMaker sentenceMaker = new SentenceMaker();
        sentenceMaker.generateRandomSentence(getApplicationContext(), phrase);
        
        recordButton.setText(R.string.buttonStart);
    }

    private void checkVoiceRecognition() {
        PackageManager packageManager = getPackageManager();
        List<ResolveInfo> activities = packageManager.queryIntentActivities(new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH), 0);
        if (activities.size() == 0) {
            recordButton.setEnabled(false);
            recordButton.setText("Voice Recognition not available");
        }
    }

    private void recordSpeech() {
        Intent i = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);

        i.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, getClass().getPackage().getName());

        i.putExtra(RecognizerIntent.EXTRA_PROMPT, phrase.getText().toString());

        i.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_WEB_SEARCH);

        i.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 30);

        //provide audio url in the result
        i.putExtra("android.speech.extra.GET_AUDIO_FORMAT", "audio/AMR");
        i.putExtra("android.speech.extra.GET_AUDIO", true);

        startActivityForResult(i, VOICE_REC_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == VOICE_REC_CODE) {
            if (resultCode == RESULT_OK) {
                recordButton.setText(R.string.newphrase);

                ArrayList<String> results = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                boolean understood = false;
                for (String aPhrase : results) {
                    Log.d("LOG", aPhrase.toLowerCase());
                    String thePhrase = phrase.getText().toString().toLowerCase();
                    thePhrase = thePhrase.substring(0, thePhrase.length() - 1);
                    Log.d("LOG", thePhrase);
                    if (aPhrase.toLowerCase().equals(thePhrase)) {
                        Toast.makeText(getApplicationContext(), "Great job! We understood what you said!", Toast.LENGTH_SHORT).show();
                        understood = true;
                        break;
                    }
                }
                if (!understood) {
                    Toast.makeText(getApplicationContext(), "Try again. We couldn't understand you.", Toast.LENGTH_SHORT).show();
                }

                //http://stackoverflow.com/questions/23047433/record-save-audio-from-voice-recognition-intent
                audioUri = data.getData();

                if (audioUri != null) {
                    replayButton.setEnabled(true);
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

    public void playBackAudio() {

        //TODO Playback bar

        //make sure we have an audio file
        if (audioUri == null) {
            return;
        }

        MediaPlayer mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setDataSource(this, audioUri);
            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
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