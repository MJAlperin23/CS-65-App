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
import java.util.Date;

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
            holder.time = (TextView) row.findViewById(R.id.exerciseTimeText);
            holder.didSpeech = (CheckBox)row.findViewById(R.id.speechDoneCheckBox);

            row.setTag(holder);
        } else {
            holder = (ExerciseItemHolder) row.getTag();
        }

        ExerciseItem entry = data[position];
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
        Date theDate = entry.getDate().getTime();
        String date = format.format(theDate);
        holder.date.setText(date);
        holder.time.setText(String.format("Exercise Time: %d minutes", entry.getExerciseTime()));
        holder.didSpeech.setChecked(entry.isSpeechDone());

        return row;
    }



    static class ExerciseItemHolder
    {
        TextView date;
        TextView time;
        CheckBox didSpeech;
    }
}