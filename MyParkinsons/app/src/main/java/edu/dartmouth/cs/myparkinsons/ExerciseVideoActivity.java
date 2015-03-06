package edu.dartmouth.cs.myparkinsons;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;

import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;
import com.google.android.youtube.player.YouTubePlayer.Provider;

import android.widget.TextView;
import android.widget.Toast;

public class ExerciseVideoActivity extends YouTubeBaseActivity implements
        YouTubePlayer.OnInitializedListener {

    public static final String API_KEY = "AIzaSyCe6tORd9Ch4lx-9Ku5SQ476uS9OtZYsWA";
    public String videoID = "";
    public TextView videoDescripTextView;
    public TextView videoTitleTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercise_video);

        String videoDescrip = "";
        String videoTitle = "";

        Intent data = getIntent();
        videoID = data.getStringExtra("VIDEO_ID_STRING");
        videoTitle = data.getStringExtra("VIDEO_TITLE_STRING");
        videoDescrip = data.getStringExtra("VIDEO_DESCRIPTION_STRING");

        videoDescripTextView = (TextView) findViewById(R.id.videoDescripText);
        videoTitleTextView = (TextView) findViewById(R.id.videoTitleText);

        videoDescripTextView.setText(videoDescrip);
        videoTitleTextView.setText(videoTitle);

        YouTubePlayerView youTubePlayerView = (YouTubePlayerView) findViewById(R.id.youtubeplayerview1);
        youTubePlayerView.initialize(API_KEY, this);

    }

    @Override
    public void onInitializationFailure(Provider provider,
                                        YouTubeInitializationResult result) {
        Toast.makeText(getApplicationContext(),
                "onInitializationFailure()",
                Toast.LENGTH_LONG).show();
    }

    @Override
    public void onInitializationSuccess(Provider provider, YouTubePlayer player, boolean wasRestored) {
        if (!wasRestored) {
            player.cueVideo(videoID);
        }
    }

}
