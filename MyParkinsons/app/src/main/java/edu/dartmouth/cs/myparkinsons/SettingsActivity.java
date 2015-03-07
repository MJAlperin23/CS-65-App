package edu.dartmouth.cs.myparkinsons;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.SystemClock;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.preference.SwitchPreference;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.sql.Time;
import java.util.Calendar;


public class SettingsActivity extends Activity implements TimePickerDialog.OnTimeSetListener {

    private static final long MS_PER_DAY = 86400000;
    public static final int SETTINGS_ACTIVITY_KEY = 1;
    public static final String ALLOW_TIME_ALERT_KEY = "allowtimealert_settingsactivity";
    public static final String TIME_OF_ALERT_KEY = "timeofalertkey_settingsactivity";
    public static final String SAVE_DATA_KEY = "timeofalertkey_settingsactivity";
    public static final String SHARED_PREFERENCES_KEY = "preferenceskey";
    public static final String ACCEL_ENABLED_KEY = "accelerometerenabledkey";

    //keys to save todays data in preferences. At midnight it'll move from preferences into the database
    public static final String CURRENT_DAY_KEY = "current_day_key";
    public static final String TOTAL_SPEECH_KEY = "total_speech_key";
    public static final String CORRECT_SPEECH_KEY = "correct_speech_key";
    public static final String EXERCISE_TIME_KEY = "exercise_time_key";

//    public SharedPreferences settingData;
//    public static SharedPreferences.Editor spEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new PrefsFragment()).commit();

//        settingData = getSharedPreferences(SHARED_PREFERENCES_KEY, MODE_PRIVATE);
//        spEdit = settingData.edit();

        PreferenceManager.setDefaultValues(this, R.xml.settings_preferences, false);
    }

    public static class PrefsFragment extends PreferenceFragment {

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.settings_preferences);

        }

        @Override
        public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {

            switch (preference.getKey()) {

                case "delete_data_key":
                    DialogFragment newFragment = MyAlertDialogFragment
                            .newInstance(R.string.delete_alert_dialog_title);
                    newFragment.show(getFragmentManager(), "dialog");
                    break;

                case "time_select_key":
                    DialogFragment timeFragment = new TimePickerFragment();
                    timeFragment.show(getFragmentManager(), "timePicker");
                    break;

            }

            return super.onPreferenceTreeClick(preferenceScreen, preference);
        }
    }


    public static class MyAlertDialogFragment extends DialogFragment {

        public static MyAlertDialogFragment newInstance(int title) {
            MyAlertDialogFragment frag = new MyAlertDialogFragment();
            Bundle args = new Bundle();
            args.putInt("title", title);
            frag.setArguments(args);
            return frag;
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            int title = getArguments().getInt("title");

            return new AlertDialog.Builder(getActivity())
                    .setIcon(R.drawable.alert_pic)
                    .setTitle(title)
                    .setMessage("All recorded data will be gone permanently.")
                    .setPositiveButton(R.string.alert_dialog_ok,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                                    int whichButton) {
                                    ((SettingsActivity) getActivity())
                                            .doPositiveClick();
                                }
                            })
                    .setNegativeButton(R.string.alert_dialog_cancel,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                                    int whichButton) {
                                    ((SettingsActivity) getActivity())
                                            .doNegativeClick();
                                }
                            }).create();
        }
    }

    private void doNegativeClick() {
        //nothing needs to be done here
    }

    //call helper to remove all entries from database
    private void doPositiveClick() {
        DataSource dbHelper = new DataSource(getApplicationContext());
        dbHelper.open();
        dbHelper.deleteAllData();
        dbHelper.close();
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR, hourOfDay);
        cal.set(Calendar.MINUTE, minute);

        if (cal.before(Calendar.getInstance())) {
            cal.add(Calendar.DAY_OF_YEAR, 1);
        }
        long timeSelectionMillis = cal.getTimeInMillis();

        setReminder(
            timeSelectionMillis,
            "MyParkinson's Speech Reminder",
            "Did You Do Your Speech Activity Today?"
        );
    }

    public void setReminder(long time, String title, String message) {

        long bootTime = Calendar.getInstance().getTimeInMillis() - SystemClock.elapsedRealtime();
        long targetRealtime = time - bootTime;

        Intent alarmIntent = new Intent(this, SpeechReminderReceiver.class);
        alarmIntent.putExtra("message", message);
        alarmIntent.putExtra("title", title);

        int NOTIFICATION_REQUEST_CODE = 0;
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, NOTIFICATION_REQUEST_CODE, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        Log.d("setReminder()", "Setting a reminder");

        alarmManager.cancel(pendingIntent);
        alarmManager.setRepeating(
                AlarmManager.ELAPSED_REALTIME, targetRealtime,
                MS_PER_DAY, pendingIntent
        );

    }

}







