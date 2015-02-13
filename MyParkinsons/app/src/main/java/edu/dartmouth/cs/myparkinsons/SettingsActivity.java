package edu.dartmouth.cs.myparkinsons;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TimePicker;


public class SettingsActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        getFragmentManager().beginTransaction().replace(android.R.id.content,
                new PrefsFragment()).commit();


    }

    public static class PrefsFragment extends PreferenceFragment {

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            addPreferencesFromResource(R.xml.settings_preferences);
        }

        @Override
        public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
            return super.onPreferenceTreeClick(preferenceScreen, preference);
        }
    }

// attempts to make a confirm delete dialog.... so far unsucsessful
//    public static void doPositiveClick() {
//        // Do stuff here.
//        Log.i("FragmentAlertDialog", "Positive click!");
//    }
//
//    public static void doNegativeClick() {
//        // Do stuff here.
//        Log.i("FragmentAlertDialog", "Negative click!");
//    }
//
//    public static class MyAlertDialogFragment extends DialogFragment {
//
//        public static MyAlertDialogFragment newInstance(int title) {
//            MyAlertDialogFragment frag = new MyAlertDialogFragment();
//            Bundle args = new Bundle();
//            args.putInt("title", title);
//            frag.setArguments(args);
//            return frag;
//        }
//
//        @Override
//        public Dialog onCreateDialog(Bundle savedInstanceState) {
//            int title = getArguments().getInt("title");
//
//            return new AlertDialog.Builder(getActivity()).setIcon(R.drawable.ic_launcher)
//                    .setTitle(title)
//                    .setPositiveButton(R.string.alert_dialog_delete,
//                            new DialogInterface.OnClickListener() {
//                                public void onClick(DialogInterface dialog, int whichButton) {
//                                    doPositiveClick();
//                                }
//                            })
//                    .setNegativeButton(R.string.alert_dialog_cancel,
//                            new DialogInterface.OnClickListener() {
//                                public void onClick(DialogInterface dialog, int whichButton) {
//                                    doNegativeClick();
//                                }
//                            }).create();
//        }
//    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_settings, menu);
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
