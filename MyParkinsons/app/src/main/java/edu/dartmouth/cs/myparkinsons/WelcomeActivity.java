package edu.dartmouth.cs.myparkinsons;

import android.animation.AnimatorInflater;
import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ViewFlipper;


public class WelcomeActivity extends Activity {

    private float lastX;

    private ViewFlipper viewFlipper;

    private int selected = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        viewFlipper = (ViewFlipper) findViewById(R.id.viewFlipper);

        setSelectedImage(1);

        Button button = (Button) findViewById(R.id.doneButton);
        button.setText("");
        button.setBackgroundResource(R.drawable.next_arrow);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (selected == 5) {
                    SharedPreferences sharedPreferences = getSharedPreferences(SettingsActivity.SHARED_PREFERENCES_KEY, MODE_PRIVATE);

                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putBoolean(MainActivity.FIRST_TIME_KEY, false);
                    editor.commit();

                    finish();
                } else {

                    viewFlipper.setInAnimation(getApplicationContext(), R.anim.in_from_right);
                    viewFlipper.setOutAnimation(getApplicationContext(), R.anim.out_to_left);
                    viewFlipper.showNext();
                    setSelectedImage(++selected);


                }

            }
        });

        Button skipButton = (Button) findViewById(R.id.skipButton);
        skipButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sharedPreferences = getSharedPreferences(SettingsActivity.SHARED_PREFERENCES_KEY, MODE_PRIVATE);

                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean(MainActivity.FIRST_TIME_KEY, false);
                editor.commit();

                finish();
            }
        });

    }


    // Using the following method, we will handle all screen swaps.

    public boolean onTouchEvent(MotionEvent touchevent) {

        switch (touchevent.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                lastX = touchevent.getX();
                break;
            }

            case MotionEvent.ACTION_UP: {
                float currentX = touchevent.getX();

                if (lastX < currentX) {
                    if (viewFlipper.getDisplayedChild() == 0)
                        break;

                    viewFlipper.setInAnimation(this, R.anim.in_from_left);
                    viewFlipper.setOutAnimation(this, R.anim.out_to_right);
                    viewFlipper.showPrevious();
                    setSelectedImage(--selected);
                }

                if (lastX > currentX) {
                    if (viewFlipper.getDisplayedChild() == viewFlipper.getChildCount() - 1)
                        break;

                    viewFlipper.setInAnimation(this, R.anim.in_from_right);
                    viewFlipper.setOutAnimation(this, R.anim.out_to_left);
                    viewFlipper.showNext();
                    setSelectedImage(++selected);
                }

                break;
            }

        }

        return false;

    }

    private void setSelectedImage(int i) {
        ImageView one = (ImageView) findViewById(R.id.circle1);
        ImageView two = (ImageView) findViewById(R.id.circle2);
        ImageView three = (ImageView) findViewById(R.id.circle3);
        ImageView four = (ImageView) findViewById(R.id.circle4);
        ImageView five = (ImageView) findViewById(R.id.circle5);
        Button button = (Button) findViewById(R.id.doneButton);
        Button skipButton = (Button) findViewById(R.id.skipButton);

        switch (i) {
            case 1:
                one.setImageResource(R.drawable.dark_circle);
                two.setImageResource(R.drawable.light_circle);
                break;
            case 2:
                two.setImageResource(R.drawable.dark_circle);
                one.setImageResource(R.drawable.light_circle);
                three.setImageResource(R.drawable.light_circle);
                break;
            case 3:
                three.setImageResource(R.drawable.dark_circle);
                four.setImageResource(R.drawable.light_circle);
                two.setImageResource(R.drawable.light_circle);
                break;
            case 4:
                four.setImageResource(R.drawable.dark_circle);
                five.setImageResource(R.drawable.light_circle);
                three.setImageResource(R.drawable.light_circle);
                button.setText("");
                button.setBackgroundResource(R.drawable.next_arrow);
                FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) button.getLayoutParams();
                params.width = 60;
                params.height = 60;
                params.bottomMargin = 20;
                params.rightMargin = 40;
                // existing height is ok as is, no need to edit it
                button.setLayoutParams(params);
                skipButton.setVisibility(View.VISIBLE);
                break;
            case 5:
                five.setImageResource(R.drawable.dark_circle);
                four.setImageResource(R.drawable.light_circle);
                button.setBackgroundColor(0x00000000);
                button.setText("DONE");
                params = (FrameLayout.LayoutParams) button.getLayoutParams();
                params.width = 180;
                params.height = 70;
                params.rightMargin = 0;
//                params.bottomMargin = 0;
                // existing height is ok as is, no need to edit it
                button.setLayoutParams(params);
                skipButton.setVisibility(View.INVISIBLE);
                break;
            default:
                break;
        }
    }

}
