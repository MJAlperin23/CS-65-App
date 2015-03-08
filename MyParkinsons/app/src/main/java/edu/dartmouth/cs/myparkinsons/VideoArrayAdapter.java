package edu.dartmouth.cs.myparkinsons;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Mickey on 3/7/15.
 */
public class VideoArrayAdapter extends ArrayAdapter<String> {

    private Context context;
    int layoutResourceId;
    List<String> data = null;

    private ImageView videoImage;
    private TextView titleText;
    private TextView summaryText;


    public VideoArrayAdapter(Context c, int layoutResourceId, List<String> data) {
        super(c, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        context = c;
        this.data = data;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        LayoutInflater inflater = ((Activity) context).getLayoutInflater();
        row = inflater.inflate(R.layout.video_options_row, parent, false);

        final String[] videoTitles = new String[] {"Exercise 1: Arm Lift", "Exercise 2: Hand-Eye Coordination", "Exercise 3: Leg Wall Stretch",
                "Exercise 4: Arm Strength and Hand Coordination", "Exercise 5: Rolling Coordination and Strength", "Exercise 6: Small Object Coordination"};
        final String[] videoDescriptions = new String[]
                {"An introduction to the video series and a basic arm lift and hold exercise.",
                        "A hand eye coordination and strength exercise using tennis balls adn other small exercise balls.",
                        "This video instructs how to do a leg wall stretch exercise",
                        "Another hand strength and coordination video, also includes work with small dumbbells.",
                        "This video features a coordination and strength exercise using special equipment the man created.",
                        "This exercise focuses on coordinating involving the picking up and sorting of small objects."};

        videoImage = (ImageView) row.findViewById(R.id.video_preview_imageview);
        titleText = (TextView) row.findViewById(R.id.video_title_textview);
        summaryText = (TextView) row.findViewById(R.id.video_summary_textview);

        if(position == 0 ){
            videoImage.setImageResource(R.drawable.vid1);
            titleText.setText(videoTitles[0]);
            summaryText.setText(videoDescriptions[0]);
        }
        if(position == 1 ){
            videoImage.setImageResource(R.drawable.vid2);
            titleText.setText(videoTitles[1]);
            summaryText.setText(videoDescriptions[1]);
        }
        if(position == 2 ){
            videoImage.setImageResource(R.drawable.vid3);
            titleText.setText(videoTitles[2]);
            summaryText.setText(videoDescriptions[2]);
        }
        if(position == 3 ){
            videoImage.setImageResource(R.drawable.vid4);
            titleText.setText(videoTitles[3]);
            summaryText.setText(videoDescriptions[3]);
        }
        if(position == 4 ){
            videoImage.setImageResource(R.drawable.vid5);
            titleText.setText(videoTitles[4]);
            summaryText.setText(videoDescriptions[4]);
        }
        if(position == 5 ){
            videoImage.setImageResource(R.drawable.vid6);
            titleText.setText(videoTitles[5]);
            summaryText.setText(videoDescriptions[5]);
        }else {
        }


        return row;
    }
}