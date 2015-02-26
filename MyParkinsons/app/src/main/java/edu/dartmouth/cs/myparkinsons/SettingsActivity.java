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

import java.util.Calendar;


public class SettingsActivity extends Activity implements TimePickerDialog.OnTimeSetListener{

    public static final int SETTINGS_ACTIVITY_KEY = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getFragmentManager().beginTransaction()
				.replace(android.R.id.content, new PrefsFragment()).commit();

        PreferenceManager.setDefaultValues(this, R.xml.settings_preferences, false);
    }

    public static class PrefsFragment extends PreferenceFragment {
        private SwitchPreference storeDataSwitch;
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.settings_preferences);

        }

        @Override
        public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {

            if(preference.getKey().equals("delete_data_key")) {
                DialogFragment newFragment = MyAlertDialogFragment
                        .newInstance(R.string.delete_alert_dialog_title);
                newFragment.show(getFragmentManager(), "dialog");
            }

            if(preference.getKey().equals("time_select_key")) {
                DialogFragment timeFragment = new TimePickerFragment();
                timeFragment.show(getFragmentManager(), "timePicker");
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

    }

    private void doPositiveClick() {

    }


    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

    }
}







