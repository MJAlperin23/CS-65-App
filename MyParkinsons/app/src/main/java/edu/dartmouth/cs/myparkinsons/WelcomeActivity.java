package edu.dartmouth.cs.myparkinsons;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
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

        button.setOnClickListener(new View.OnClickListener() {
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

        switch (touchevent.getAction())
        {
            case MotionEvent.ACTION_DOWN:
            {
                lastX = touchevent.getX();
                break;
            }

            case MotionEvent.ACTION_UP:
            {
                float currentX = touchevent.getX();

                if (lastX < currentX)
                {
                    if (viewFlipper.getDisplayedChild()==0)
                        break;

                    viewFlipper.setInAnimation(this, R.anim.in_from_left);
                    viewFlipper.setOutAnimation(this, R.anim.out_to_right);
                    viewFlipper.showPrevious();
                    setSelectedImage(--selected);
                }

                if (lastX > currentX)
                {
                    if (viewFlipper.getDisplayedChild()==viewFlipper.getChildCount()-1)
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
                break;
            case 5:
                five.setImageResource(R.drawable.dark_circle);
                four.setImageResource(R.drawable.light_circle);
                break;
            default:
                break;
        }
    }

//        switch (touchevent.getAction()) {
//
//
//
//            case MotionEvent.ACTION_DOWN:
//
//                lastX = touchevent.getX();
//
//                break;
//
//            case MotionEvent.ACTION_UP:
//
//                float currentX = touchevent.getX();
//
//
//
//                // Handling left to right screen swap.
//
//                if (lastX < currentX) {
//
//
//
//                    // If there aren't any other children, just break.
//
//                    if (viewFlipper.getDisplayedChild() == 0)
//
//                    break;
//
//
//
//                    // Next screen comes in from left.
//
//                    viewFlipper.setInAnimation(this, R.anim.in_from_left);
//
//                    // Current screen goes out from right.
//
//                    viewFlipper.setOutAnimation(this, R.anim.out_to_right);
//
//
//
//                    // Display next screen.
//
//                    viewFlipper.showNext();
//
//                }
//
//
//
//                // Handling right to left screen swap.
//
//                if (lastX > currentX) {
//
//
//
//                    // If there is a child (to the left), kust break.
//
//                    if (viewFlipper.getDisplayedChild() == 1)
//
//                    break;
//
//
//
//                    // Next screen comes in from right.
//
//                    viewFlipper.setInAnimation(this, R.anim.in_from_right);
//
//                    // Current screen goes out from left.
//
//                    viewFlipper.setOutAnimation(this, R.anim.out_to_left);
//
//
//
//                    // Display previous screen.
//
//                    viewFlipper.showPrevious();
//
//                }
//
//                break;
//
//        }
//
//        return false;
//
//    }




}
