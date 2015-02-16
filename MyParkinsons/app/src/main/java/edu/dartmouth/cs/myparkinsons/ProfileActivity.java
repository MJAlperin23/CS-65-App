package edu.dartmouth.cs.myparkinsons;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;


public class ProfileActivity extends Activity {

    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int CROP_PICTURE_CODE = 2;
    private static final int SELECT_PICTURE_GALLERY = 3;

    private static final String PREFERENCES_KEY = "PREFERENCES_KEY";
    private static final String NAME_KEY = "NAME_KEY";
    private static final String EMAIL_KEY = "EMAIL_KEY";
    private static final String AGE_KEY = "AGE_KEY";
    private static final String CITY_KEY = "CITY_KEY";
    private static final String STATE_KEY = "STATE_KEY";
    private static final String RADIO_GROUP_KEY = "RADIO_GROUP_KEY";
    private static final String URI_KEY = "URI_KEY";

    private static final String IMAGE_UNSPECIFIED = "image/*";
    private static final int CAMERA_ITEM = 0;
    private static final int GALLERY_ITEM = 1;
    private static final String CHANGE_PIC_TAG = "CHANGE_PIC_DIALOG";


    private Button changePicButton;
    private Button saveButton;
    private Button cancelButton;

    private EditText nameText;
    private EditText emailText;
    private EditText ageText;
    private EditText cityText;
    private EditText stateText;


    private ImageView profilePic;

    public Uri pictureURI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        changePicButton = (Button)findViewById(R.id.changePhotoButton);
        saveButton = (Button)findViewById(R.id.saveButton);
        cancelButton = (Button)findViewById(R.id.cancelButton);

        nameText = (EditText)findViewById(R.id.nameTextField);
        emailText = (EditText)findViewById(R.id.emailTextField);
        ageText = (EditText)findViewById(R.id.ageTextField);
        cityText = (EditText)findViewById(R.id.cityTextField);
        stateText = (EditText)findViewById(R.id.stateTextField);


        profilePic = (ImageView)findViewById(R.id.profileImageView);

        loadUserData();

        //load saved image if there's saved instance state. Otherwise load the file from SD card
        if (savedInstanceState != null)
        {
            Uri uri_pic = savedInstanceState.getParcelable(URI_KEY);
            profilePic.setImageURI(uri_pic);
            profilePic.setImageBitmap((Bitmap) savedInstanceState.getParcelable("image"));
        } else
        {
            loadPicData(getString(R.string.profile_photo_file_name));
        }

        changePicButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changePicPressed();

            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                savePicData();
                saveUserData();
                Toast.makeText(getBaseContext(), "Saved", Toast.LENGTH_SHORT).show();
                finish();
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getBaseContext(), "Cancelled", Toast.LENGTH_SHORT).show();
                finish();
            }
        });

    }

    //Open dialog on buttonClick
    private void changePicPressed() {
        DialogFragment frag = ChangePicDialogFragment.newInstance(1);
        frag.show(getFragmentManager(), CHANGE_PIC_TAG);
    }

    //Launch gallery with intent
    private void openImageGallery() {
        Intent i = new Intent(Intent.ACTION_PICK);
        i.setType("image/*");
        startActivityForResult(i, SELECT_PICTURE_GALLERY);
    }

    //Save image on device rotation
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putParcelable("image", ((BitmapDrawable)profilePic.getDrawable()).getBitmap());
        super.onSaveInstanceState(outState);

    }


    private void savePicData(){
        // Commit all the changes into preference file
        // Save profile image into internal storage.
        profilePic.buildDrawingCache();
        Bitmap bmap = profilePic.getDrawingCache();
        try {
            FileOutputStream fos = openFileOutput(
                    getString(R.string.profile_photo_file_name), MODE_PRIVATE);
            bmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.flush();
            fos.close();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }

    }


    // load the user data from shared preferences if there is no data make sure
    // that we set it to something reasonable
    private void saveUserData() {

        // Getting the shared preferences editor
        SharedPreferences preferences = getSharedPreferences(PREFERENCES_KEY, MODE_PRIVATE);
        SharedPreferences.Editor preferencesEditor = preferences.edit();
        preferencesEditor.clear();

        //save NAME
        preferencesEditor.putString(NAME_KEY, nameText.getText().toString());

        preferencesEditor.putString(EMAIL_KEY, emailText.getText().toString());

        preferencesEditor.putString(AGE_KEY, ageText.getText().toString());

        preferencesEditor.putString(CITY_KEY, cityText.getText().toString());

        preferencesEditor.putString(STATE_KEY, stateText.getText().toString());



        // Read which index the radio is checked.

        // edit this out and use as a debug example
        // interesting bug because you try and write an int to a string


        RadioGroup radioGroup = (RadioGroup) findViewById(R.id.genderRadioGroup);
        int selectedIndex = radioGroup.indexOfChild(findViewById(radioGroup
                .getCheckedRadioButtonId()));
        preferencesEditor.putInt(RADIO_GROUP_KEY, selectedIndex);

        // Commit all the changes into the shared preference
        preferencesEditor.commit();


    }


    //load the user's data from shared preferences
    private void loadUserData(){

        SharedPreferences preferences = getSharedPreferences(PREFERENCES_KEY, MODE_PRIVATE);

        String name = preferences.getString(NAME_KEY, "");
        nameText.setText(name);

        String email = preferences.getString(EMAIL_KEY, "");
        emailText.setText(email);

        String age = preferences.getString(AGE_KEY, "");
        ageText.setText(age);

        String city = preferences.getString(CITY_KEY, "");
        cityText.setText(city);

        String state = preferences.getString(STATE_KEY, "");
        stateText.setText(state);

        // Please Load gender info and set radio box
        int mIntValue = preferences.getInt(RADIO_GROUP_KEY, -1);
        // In case there isn't one saved before:
        if (mIntValue >= 0) {
            // Find the radio button that should be checked.
            RadioButton radioBtn = (RadioButton) ((RadioGroup) findViewById(R.id.genderRadioGroup))
                    .getChildAt(mIntValue);
            // Check the button.
            radioBtn.setChecked(true);

        }


    }

    //load the profile picture from file
    private void loadPicData(String filePath){
        // Load profile photo from internal storage
        try {
            FileInputStream fis = openFileInput(filePath);
            Bitmap bmap = BitmapFactory.decodeStream(fis);
            profilePic.setImageBitmap(bmap);
            fis.close();
        } catch (IOException e) {
            // Default profile photo if no photo saved before.
            profilePic.setImageResource(R.drawable.ic_launcher);
        }

    }


    //launch the camera intent
    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        pictureURI = Uri.fromFile(new File(Environment.getExternalStorageDirectory(), "tmp_" +
                String.valueOf(System.currentTimeMillis()) + ".jpg"));
        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, pictureURI);
        takePictureIntent.putExtra("return-data", true);

        try {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
        }

    }


    //result from camera, crop, or gallery
    //Camera goes to crop
    //crop sets picture
    //gallery crops picture
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {

            cropPic();
        } else if (requestCode == CROP_PICTURE_CODE && resultCode == RESULT_OK) {
            setPicture(data);
        } else if (requestCode == SELECT_PICTURE_GALLERY && resultCode == RESULT_OK) {
            pictureURI = data.getData();
            cropPic();

        }
    }


    //launch crop intent
    private void cropPic() {
        Intent cropIntent = new Intent("com.android.camera.action.CROP");
        cropIntent.setDataAndType(pictureURI, IMAGE_UNSPECIFIED);

        cropIntent.putExtra("outputX", 200);
        cropIntent.putExtra("outputY", 200);

        cropIntent.putExtra("aspectX", 1);
        cropIntent.putExtra("aspectY", 1);

        cropIntent.putExtra("scale", true);
        cropIntent.putExtra("return-data", true);

        startActivityForResult(cropIntent, CROP_PICTURE_CODE);
    }

    //Get the image from the intent and put it in imageView
    private void setPicture(Intent data){
        Bundle extras = data.getExtras();
        Bitmap imageBitmap = (Bitmap) extras.get("data");
        profilePic.setImageBitmap(imageBitmap);
    }



    public void changePicDialogPressed(int index) {
        switch (index) {
            case CAMERA_ITEM:
                dispatchTakePictureIntent();
                return;
            case GALLERY_ITEM:
                openImageGallery();
                return;
            default:
                return;
        }
    }
}
