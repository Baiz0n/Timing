package com.nothing.timing.timing.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.Toast;

import com.nothing.timing.timing.R;
import com.nothing.timing.timing.bloc.data.GeneralAppData;
import com.nothing.timing.timing.bloc.data.IntentData;
import com.nothing.timing.timing.bloc.sql.AccomplishSql;
import com.nothing.timing.timing.bloc.sql.MandatorySql;
import com.nothing.timing.timing.bloc.sql.ProgressSql;
import com.nothing.timing.timing.bloc.sql.SqlNames;
import com.nothing.timing.timing.fragments.MainRecycle;
import com.nothing.timing.timing.tools.DialogMaker;

import java.util.ArrayList;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private ProgressSql progressSql;
    private AccomplishSql accomplishSql;
    private MandatorySql mandatorySql;

    private Intent intent;
    private RecyclerView recyclerView;
    private FloatingActionButton fab;
    private Button addNew;
    private Button delAll;

    private ArrayList<String> objectives;
    private ArrayList<Integer> progresses;
    private ArrayList<Integer> goalHours;

    SharedPreferences sharedPref;
    SharedPreferences.Editor editor;

    private boolean isFABOpen = false;
    private boolean isFirstTime = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        firstTimeLoader();

        editor = sharedPref.edit();
        editor.apply();
        editor.putBoolean(GeneralAppData.IS_FIRST_TIME, true);

        progressSql = new ProgressSql(this);
        accomplishSql = new AccomplishSql(this);
        mandatorySql = new MandatorySql(this);
        checkForCalendarReset();

        recyclerView = findViewById(R.id.recycler_view);
        fab = findViewById(R.id.action);
        addNew = findViewById(R.id.add_new);
        delAll = findViewById(R.id.delete_all);

        objectives = new ArrayList<>();
        progresses = new ArrayList<>();
        goalHours = new ArrayList<>();

        populateData();
        setFunctionality();
    }

    private void setFunctionality() {
        fab.setOnClickListener(fabClick);
        addNew.setOnClickListener(addNewClick);
        addNew.setOnTouchListener(GeneralAppData.changeYellowTouchButtonFAB);
        delAll.setOnClickListener(delAllClick);
        delAll.setOnTouchListener(GeneralAppData.changeYellowTouchButtonFAB);
    }

    private void firstTimeLoader() {

        sharedPref = getSharedPreferences(GeneralAppData.PREF_TITLE, MODE_PRIVATE);

        if ( !sharedPref.contains(GeneralAppData.IS_FIRST_TIME)
                && sharedPref.getBoolean(GeneralAppData.IS_FIRST_TIME, true)) {

            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putBoolean(GeneralAppData.IS_FIRST_TIME, false);
            editor.apply();

            isFirstTime = true;

            Log.e(TAG, "editor successful (true means not working) .. is first time = "
                    + sharedPref.getBoolean(GeneralAppData.IS_FIRST_TIME, true));

            resetData();
        }
    }

    private void initRecyclerView() {

        Log.e(TAG, "should init man");

        MainRecycle adapter = new MainRecycle(this, objectives, progresses, goalHours);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    private void populateData() {

        Cursor objectivesDB = progressSql.getData(SqlNames.Progress.TITLES);

        while ( objectivesDB.moveToNext() ) {
            objectives.add(objectivesDB.getString(0));
            Log.e(TAG, "progress titles not empty!!!");
        }
        objectivesDB.close();

        Cursor timeLeft = progressSql.getData(SqlNames.Progress.HOURS);

        while( timeLeft.moveToNext() ) {
            progresses.add(Integer.parseInt(timeLeft.getString(0)));
            Log.e(TAG, "progress hours not empty!!!");
        }
        timeLeft.close();

        Cursor totalTime = accomplishSql.getData(SqlNames.Accomplish.HOURS);

        while( totalTime.moveToNext() ) {
            goalHours.add(Integer.parseInt(totalTime.getString(0)));
            Log.e(TAG, "accomplish titles not empty!!!");
        }
        totalTime.close();

        initRecyclerView();
    }

    private void resetData() {

        if (!isFirstTime) {

            if ( progressSql.delAllData()
                    && accomplishSql.delAllData()
                    && mandatorySql.delAllData() ) {

                Toast.makeText(getApplicationContext(),
                        "All objective were deleted", Toast.LENGTH_SHORT).show();
            }
        }

        intent = new Intent(this, Entry.class);
        startActivity(intent);
    }

    View.OnClickListener fabClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(!isFABOpen){
                showFABMenu();
            }else{
                closeFABMenu();
            }
        }
    };

    private void showFABMenu(){
        isFABOpen=true;
        fadeInAnimation(addNew, 500);
        fadeInAnimation(delAll, 500);
        addNew.animate().translationY(-getResources().getDimension(R.dimen.standard_55));
        delAll.animate().translationY(-getResources().getDimension(R.dimen.standard_105));

        GeneralAppData.changeColorAnimation(fab, false,
                ContextCompat.getColor(
                        getApplicationContext(),
                        R.color.yellow),
                ContextCompat.getColor(
                        getApplicationContext(),
                        R.color.dark_blue), 500);

    }

    private void closeFABMenu(){
        isFABOpen=false;
        fadeOutAnimation(addNew, 130);
        fadeOutAnimation(delAll, 130);
        addNew.animate().translationY(0);
        delAll.animate().translationY(0);

        GeneralAppData.changeColorAnimation(fab, true,
                ContextCompat.getColor(
                        getApplicationContext(),
                        R.color.yellow),
                ContextCompat.getColor(
                        getApplicationContext(),
                        R.color.dark_blue), 300);
    }

    public static void fadeInAnimation(final View view, long animationDuration) {
        Animation fadeIn = new AlphaAnimation(0, 1);
        fadeIn.setInterpolator(new DecelerateInterpolator());
        fadeIn.setDuration(animationDuration);
        fadeIn.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }
            @Override
            public void onAnimationEnd(Animation animation) {
                view.setVisibility(View.VISIBLE);
            }
            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });

        view.startAnimation(fadeIn);
    }

    public static void fadeOutAnimation(final View view, long animationDuration) {
        Animation fadeOut = new AlphaAnimation(1, 0);
        fadeOut.setInterpolator(new AccelerateInterpolator());
        fadeOut.setStartOffset(animationDuration);
        fadeOut.setDuration(animationDuration);
        fadeOut.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }
            @Override
            public void onAnimationEnd(Animation animation) {
                view.setVisibility(View.INVISIBLE);
            }
            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });

        view.startAnimation(fadeOut);
    }

    private View.OnClickListener delAllClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            DialogInterface.OnClickListener dialog = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which){
                        case DialogInterface.BUTTON_POSITIVE:
                            //Yes button clicked

                            resetData();
                            break;

                        case DialogInterface.BUTTON_NEGATIVE:
                            //No button clicked
                            break;
                    }
                }
            };

            DialogMaker.executeDialog(MainActivity.this,
                    "Are you sure want to remove all objectives?",
                    dialog);
        }
    };

    View.OnClickListener addNewClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            intent = new Intent(getApplicationContext(), Entry.class);
            intent.putExtra(IntentData.ADD_NEW_GOAL, true);
            startActivity(intent);
        }
    };

    private void checkForCalendarReset() {

        Calendar calendar = Calendar.getInstance();

        if (  calendar.get(Calendar.DAY_OF_WEEK) == 1
                &&
             sharedPref.getLong(GeneralAppData.MILLISECONDS, 1000*60*60*24) >= System.currentTimeMillis() ) {

            Cursor cursor = progressSql.getData(SqlNames.Progress.TITLES);
            while(cursor.moveToNext()) {

                progressSql.editData(cursor.getString(0), "", "0");
            }
        }

        editor.putLong(GeneralAppData.MILLISECONDS, System.currentTimeMillis());
        editor.apply();

        Log.e(TAG, sharedPref.getLong(GeneralAppData.MILLISECONDS, 1)
                + " miliseconds of the " + calendar.get(Calendar.DAY_OF_WEEK) + " day are set!");

    }
}
