package com.nothing.timing.timing.bloc.data;


import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.nothing.timing.timing.R;
import com.nothing.timing.timing.ui.MainActivity;

import java.util.Collections;

public class GeneralAppData {

    private final static String TAG = GeneralAppData.class.getSimpleName();

    public static final String PREF_TITLE = "something_random";
    public static final String HOURS_LEFT = "hours_left";
    public static final String IS_FIRST_TIME = "is_first_time";
    public static final String RESET_WEEKLY = "reset_weekly";
    public static final String MILLISECONDS = "milliseconds";

    public static int num = 0;

    public static final int RESET = 0;
    public static final int DELETE = 1;
    public static final int DELETE_ALL = 2;

    public static View.OnTouchListener changeYellowTouchButton = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {

            switch(event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    // touch down code
                    v.setBackgroundResource(R.color.dark_yellow);
                    break;

                case MotionEvent.ACTION_MOVE:
                    // touch move code
                    v.setBackgroundResource(R.color.dark_yellow);
                    break;

                case MotionEvent.ACTION_UP:
                    // touch up code
                    v.setBackgroundResource(R.color.yellow);
                    break;
            }
            return false;
        }
    };

    public static View.OnTouchListener changeYellowTouchButtonFAB = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {

            switch(event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    // touch down code
                    v.setBackgroundResource(R.color.dark_yellow);
                    break;

                case MotionEvent.ACTION_MOVE:
                    // touch move code
                    v.setBackgroundResource(R.color.dark_yellow);
                    break;

                case MotionEvent.ACTION_UP:
                    // touch up code
                    v.setBackgroundResource(R.color.button_yellow);
                    break;
            }
            return false;
        }
    };

    public static View.OnTouchListener changeGreenTouchButton = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {

            switch(event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    // touch down code
                    v.setBackgroundResource(R.color.button_change_blue);
                    break;

                case MotionEvent.ACTION_MOVE:
                    // touch move code
                    v.setBackgroundResource(R.color.button_change_blue);
                    break;

                case MotionEvent.ACTION_UP:
                    // touch up code
                    v.setBackgroundResource(R.color.dark_blue);
                    break;
            }
            return false;
        }
    };

    public static View.OnTouchListener changeListColor = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {

            ColorDrawable color = (ColorDrawable) v.getBackground();
            int colorId = color.getColor();

            switch(event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    // touch down code
                    changeColorAnimation(v, false,
                            colorId,
                            ResourcesCompat.getColor(v.getResources(), R.color.button_change_blue,null),400);
                    break;

                case MotionEvent.ACTION_MOVE:
                    // touch move code
                    changeColorAnimation(v, true,
                            colorId,
                            ResourcesCompat.getColor(v.getResources(), R.color.button_change_blue,null),400);
                    break;

                case MotionEvent.ACTION_UP:
                    // touch up code
                    changeColorAnimation(v, true,
                            colorId,
                            ResourcesCompat.getColor(v.getResources(), R.color.button_change_blue,null),400);
                    break;
            }
            return false;
        }
    };


    public static boolean changeColorAnimation(final View view, boolean changed, int fromInk, int toInk, int duration) {

        final float[] from = new float[3],
                to = new float[3];

        ValueAnimator anim;

        if ( changed ) { // to original state

            Color.colorToHSV(toInk, from);
            Color.colorToHSV(fromInk, to);

            anim = ValueAnimator.ofFloat(0, 1);   // animate from 0 to 1
            anim.setDuration(duration);

        } else { // to pressed state

            Color.colorToHSV(fromInk, from);
            Color.colorToHSV(toInk, to);

            anim = ValueAnimator.ofFloat(0, 1);   // animate from 0 to 1
            anim.setDuration(duration);
        }

        final float[] hsv = new float[3];                  // transition color
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @TargetApi(Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                // Transition along each axis of HSV (hue, saturation, value)
                hsv[0] = from[0] + (to[0] - from[0]) * animation.getAnimatedFraction();
                hsv[1] = from[1] + (to[1] - from[1]) * animation.getAnimatedFraction();
                hsv[2] = from[2] + (to[2] - from[2]) * animation.getAnimatedFraction();

                view.setBackgroundTintList(ColorStateList.valueOf(Color.HSVToColor(hsv)));
            }
        });
        anim.start();
        return true;
    }

} // end class
