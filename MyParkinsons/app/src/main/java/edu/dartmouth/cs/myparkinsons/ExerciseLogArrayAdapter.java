package edu.dartmouth.cs.myparkinsons;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.SimpleDateFormat;

/**
 * Created by Andrew on 2/2/15.
 */
public class ExerciseLogArrayAdapter extends ArrayAdapter<ExerciseItem> {

    Context context;
    int layoutResourceId;
    ExerciseItem data[] = null;


    public ExerciseLogArrayAdapter(Context context, int layoutResourceId, ExerciseItem[] data) {
        super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        ExerciseItemHolder holder = null;

        if (row == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);

            holder = new ExerciseItemHolder();
            holder.date = (TextView) row.findViewById(R.id.dateText);
            holder.running = (TextView) row.findViewById(R.id.runningText);
            holder.walking = (TextView) row.findViewById(R.id.walkingText);
            holder.didSpeech = (CheckBox)row.findViewById(R.id.speechDoneCheckBox);

            row.setTag(holder);
        } else {
            holder = (ExerciseItemHolder) row.getTag();
        }

        ExerciseItem entry = data[position];
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
        String date = format.format(entry.getDate());
        holder.date.setText(date);
        holder.walking.setText(String.format("Walking: %.2f miles", entry.getWalkingMiles()));
        holder.running.setText(String.format("Running: %.2f miles", entry.getRunningMiles()));
        holder.didSpeech.setChecked(entry.isSpeechDone());

        return row;
    }



    static class ExerciseItemHolder
    {
        TextView date;
        TextView walking;
        TextView running;
        CheckBox didSpeech;
    }
}