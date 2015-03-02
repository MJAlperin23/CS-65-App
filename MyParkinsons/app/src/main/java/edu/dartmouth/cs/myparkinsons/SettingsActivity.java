package edu.dartmouth.cs.myparkinsons;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
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

    public static final int SETTINGS_ACTIVITY_KEY = 1;
    public static final String ALLOW_TIME_ALERT_KEY = "allowtimealert_settingsactivity";
    public static final String TIME_OF_ALERT_KEY = "timeofalertkey_settingsactivity";
    public static final String SAVE_DATA_KEY = "timeofalertkey_settingsactivity";
    public static final String SHARED_PREFERENCES_KEY = "preferenceskey";
    public SharedPreferences settingData;
    public static SharedPreferences.Editor spEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new PrefsFragment()).commit();

        settingData = getSharedPreferences(SHARED_PREFERENCES_KEY, MODE_PRIVATE);
        spEdit = settingData.edit();

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

                case "speech_alert_toggle_switch":
                    spEdit.putBoolean(SettingsActivity.ALLOW_TIME_ALERT_KEY, ((SwitchPreference) preference).isChecked());
                    spEdit.commit();
                    break;

                case "store_data_toggle_switch":
                    spEdit.putBoolean(SettingsActivity.SAVE_DATA_KEY, ((SwitchPreference) preference).isChecked());
                    spEdit.commit();
                    break;

                //TODO: create method call to disable accel tracking service
                case "allow_accel_tracking_toggle":
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

    //TODO: method call to remove all things from database
    private void doPositiveClick() {

    }

    //TODO: call alarmmanager setup message
    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR, hourOfDay);
        cal.set(Calendar.MINUTE, minute);

        if (cal.before(Calendar.getInstance())) {
            cal.add(Calendar.DAY_OF_YEAR, 1);
        }
        long timeSelectionMillis = cal.getTimeInMillis();

        SharedPreferences settingData = getSharedPreferences(SHARED_PREFERENCES_KEY, MODE_PRIVATE);
        SharedPreferences.Editor spEdit = settingData.edit();
        spEdit.putLong(TIME_OF_ALERT_KEY, timeSelectionMillis);
        spEdit.commit();

    }


}







