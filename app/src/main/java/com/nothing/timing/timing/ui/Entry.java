package com.nothing.timing.timing.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.AsyncTask;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.nothing.timing.timing.R;
import com.nothing.timing.timing.bloc.data.EntryViewData;
import com.nothing.timing.timing.bloc.data.GeneralAppData;
import com.nothing.timing.timing.bloc.data.IntentData;
import com.nothing.timing.timing.bloc.sql.AccomplishSql;
import com.nothing.timing.timing.bloc.sql.MandatorySql;
import com.nothing.timing.timing.bloc.sql.ProgressSql;
import com.nothing.timing.timing.bloc.sql.SqlNames;
import com.nothing.timing.timing.tools.Checker;

import java.util.Calendar;


public class Entry extends AppCompatActivity {

    private static final String TAG = Entry.class.getSimpleName();

    //sql
    private MandatorySql mandatorySql;
    private AccomplishSql accomplishSql;
    private ProgressSql progressSql;

    //views
    private TextView mainTitle;
    private TextView hoursLeftView;
    private TextView objective;
    private EditText userObjective;
    private EditText dailyHours;
    private EditText weeklyHours;
    private Button next;
    private Button save;
    private ConstraintLayout entry;

    private int subtractor = 0;
    private boolean isMandatory;
    private EntryViewData entryData;
    private int objectiveCount = 0;
    boolean objectiveSwitchover = false;
    Intent intent;
    private boolean allowChanges = false;
    private boolean saveAndFinishBool = false;
    private boolean changeNumber = true;
    private boolean changesMade = false;
    private boolean addOnlyOne = false;
    private boolean firstSave = false;
    private boolean secondTime = false;

    SharedPreferences sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entry);

        GeneralAppData.num = 1;
        sharedPref = getSharedPreferences(GeneralAppData.PREF_TITLE, MODE_PRIVATE);

        mandatorySql = new MandatorySql(this);
        accomplishSql = new AccomplishSql(this);
        progressSql = new ProgressSql(this);

        mainTitle = findViewById(R.id.main_title);
        hoursLeftView = findViewById(R.id.hours_left);
        objective = findViewById(R.id.objective);
        userObjective = findViewById(R.id.user_objective);
        dailyHours = findViewById(R.id.daily_hours);
        weeklyHours = findViewById(R.id.weekly_hours);
        next = findViewById(R.id.next);
        save = findViewById(R.id.save);
        entry = findViewById(R.id.entry);

        getIncomingIntent();
        populate();
        addFunctionality();

    }

    private void populate() {

        mainTitle.setText(entryData.getMainTitle());
        refresh();
    }

    private void refresh() {

        if (!objectiveSwitchover && entryData.getObjective(objectiveCount) != null && isMandatory) {

            objective.setText(entryData.getObjective(objectiveCount));

        } else {

            if (!objectiveSwitchover) {

                objective.setVisibility(View.GONE);
                userObjective.setVisibility(View.VISIBLE);

                objectiveSwitchover = true;
            }

            userObjective.setText("");
        }

        hoursLeftView.setText(entryData.getHoursLeft(subtractor));
        dailyHours.setText("");
        weeklyHours.setText("");

        subtractor = 0;
    }

    private void addFunctionality() {

        weeklyHours.addTextChangedListener(weeklyWatcher);
        dailyHours.addTextChangedListener(dailyWatcher);
        userObjective.addTextChangedListener(userObjectiveWatcher);

        next.setOnClickListener(nextClick);
        save.setOnClickListener(saveClick);

        next.setOnTouchListener(GeneralAppData.changeYellowTouchButton);
        save.setOnTouchListener(GeneralAppData.changeYellowTouchButton);
    }

    private void getIncomingIntent() {

        int hoursLeft = 0;

        if (getIntent().hasExtra(IntentData.ADD_NEW_GOAL) &&
                getIntent().getBooleanExtra(IntentData.ADD_NEW_GOAL, false)) {

            isMandatory = false;
            addOnlyOne = true;
            save.setVisibility(View.INVISIBLE);
            next.setText(getString(R.string.save_add_one));

            hoursLeft = sharedPref.getInt(GeneralAppData.HOURS_LEFT,0);
        } else {

            isMandatory = true;
        }

        entryData = new EntryViewData(isMandatory, hoursLeft);
    }

    // messy code ------>

    private View.OnClickListener nextClick = new View.OnClickListener() {

        @Override
        public void onClick(View v) {

            nextFunc();
        } // end onclick


    };

    private void nextFunc() {

        if ( allowChanges ) {

            Log.d(TAG, "allow changes = true");

            secondTime = false;

            if ( String.valueOf(weeklyHours.getText()).equals("") ) {

                Toast.makeText(getApplicationContext(), "Nothing was added", Toast.LENGTH_SHORT).show();

            } else {

                Cursor cursor = null;

                if (isMandatory && entryData.getHoursLeftNum(subtractor) >= 0) {

                    Log.e(TAG, "isMandataory = true");

                    if (objectiveSwitchover || entryData.getObjective(objectiveCount) == null) {

                        Log.e(TAG, "objectiveSwitchover || entryData.getObjective(objectiveCount) == null ==== TRUE");

                        entryData.setUserObjective(String.valueOf(userObjective.getText()));

                        if ( Checker.mandatoryTitleAlreadyExists(mandatorySql ,entryData.getUserObjective()) ) {

                            Toast.makeText(getApplicationContext(), "This mandatory objective already exists!", Toast.LENGTH_SHORT).show();

                            allowChanges = false;

                        } else if ( entryData.getUserObjective().equals("") ) {

                            Toast.makeText(getApplicationContext(), "Enter an objective, clumsy!", Toast.LENGTH_SHORT).show();

                            allowChanges = false;

                        } else {

                            if (mandatorySql.addData
                                    (entryData.getUserObjective(),
                                            subtractor+"")) {

                                cursor = mandatorySql.getRow(SqlNames.Mandatory.TITLES, entryData.getUserObjective());

                            }
                        }

                        Log.e(TAG, "objectiveSwitchover || entryData.getObjective(objectiveCount) == null ==== FALSE");

                    } else {

                        if ( entryData.getHoursLeftNum(subtractor) == 0 && isMandatory ) {

                            Log.e(TAG, "hours left == 0");

                            Toast.makeText(getApplicationContext(),
                                    "edit mandatory objectives, you haven't left hours for goals",
                                    Toast.LENGTH_LONG).show();

                            allowChanges = false;
                        }

                        if (mandatorySql.addData
                                (entryData.getObjective(objectiveCount),
                                        subtractor+"")) {

                            cursor = mandatorySql.getRow(SqlNames.Mandatory.TITLES, entryData.getObjective(objectiveCount));
                        }
                    }


                } else {

                    Log.e(TAG, "isMandatory || hours left == FALSE >>>>>>>> hours left == " + entryData.getHoursLeftNum(subtractor));

                    if ( entryData.getHoursLeftNum(subtractor) <= 0 && isMandatory ) {

                        Toast.makeText(getApplicationContext(),
                                "edit mandatory objectives, you haven't left hours for goals",
                                Toast.LENGTH_LONG).show();

                        allowChanges = false;

                    } else {

                        entryData.setUserObjective( String.valueOf(userObjective.getText()) );

                        if ( Checker.accompishTitleAlreadyExists(accomplishSql, entryData.getUserObjective())) {

                            Toast.makeText(getApplicationContext(), "This goal already exists!", Toast.LENGTH_SHORT).show();

                            allowChanges = false;

                        } else if ( entryData.getUserObjective().equals("") ) {

                            Toast.makeText(getApplicationContext(), "Enter an goal, clumsy!", Toast.LENGTH_SHORT).show();

                            allowChanges = false;

                        } else if (accomplishSql.addData
                                    (entryData.getUserObjective(),
                                            subtractor+"")
                                    &&
                                    progressSql.addData
                                            (entryData.getUserObjective(),
                                                    "0")) {

                            cursor = accomplishSql.getRow(SqlNames.Accomplish.TITLES,entryData.getUserObjective());
                        }
                    }
                }

                if (allowChanges) {

                    assert cursor != null;
                    cursor.moveToFirst();

                    String string = cursor.getString(0);
                    cursor.close();

                    if ( firstSave ) {

                        Toast.makeText(getApplicationContext(), string + " has been added, Now set your Goals!", Toast.LENGTH_LONG).show();

                    } else {

                        Toast.makeText(getApplicationContext(), string + " has been added!", Toast.LENGTH_SHORT).show();
                    }


                }
            }

            if ( allowChanges ) {

                if ( saveAndFinishBool ) {

                    Toast.makeText(getApplicationContext(), "No more hours available, good luck!", Toast.LENGTH_LONG).show();

                    saveAndFinish();

                } else if (addOnlyOne) {

                    Toast.makeText(getApplicationContext(),
                            entryData.getUserObjective() + " has been added!",
                            Toast.LENGTH_SHORT).show();

                    saveAndFinish();

                } else if (firstSave) {

                    saveAndFinish();
                } else {

                    objectiveCount++;
                    refresh();
                }
            } else {

                changesMade = false;
            }

        } else {

                if (changesMade && !secondTime) {

                    Toast.makeText(getApplicationContext(),
                            subtractor + entryData.getHoursLeftNum(subtractor)
                                    + " hours is more than a week, that's impressive! Now type a valid number",
                            Toast.LENGTH_LONG).show();

                    secondTime = true;

                } else {

                    Toast.makeText(getApplicationContext(),
                            "It's definitely not gonna help to keep trying..!",
                            Toast.LENGTH_LONG).show();
                }
            }

    }

    private View.OnClickListener saveClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            if (isMandatory) {

                if ( changesMade ) {

                    firstSave = true;
                    setAlphaAnimation(entry);

                    MyAsyncFirst myAsyncFirst = new MyAsyncFirst();
                    myAsyncFirst.execute();

                    firstSave = false;
                    changesMade = false;

                } else {

                    setAlphaAnimation(entry);

                    MyAsyncFirst myAsyncFirst = new MyAsyncFirst();
                    myAsyncFirst.execute();

                    changesMade = false;
                }

            } else {

                if ( changesMade ) {

                    addOnlyOne = true;
                    nextFunc();

                } else {

                    Toast.makeText(getApplicationContext(),
                            "All set, Good luck!", Toast.LENGTH_SHORT).show();
                    saveAndFinish();
                }

            }
        }
    };

    private TextWatcher userObjectiveWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @SuppressLint("SetTextI18n")
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @SuppressLint("SetTextI18n")
        @Override
        public void afterTextChanged(Editable s) {

            if ( String.valueOf(s).equals("") ) {

                changesMade = false;
                allowChanges = false;

            } else {
                changesMade = true;
                allowChanges = true;
            }
        }
    };

    private TextWatcher weeklyWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @SuppressLint("SetTextI18n")
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @SuppressLint("SetTextI18n")
        @Override
        public void afterTextChanged(Editable s) {

            changesMade = true;

            if ( changeNumber ) {

                changeNumber = false;

                dailyHours.setText("");

                if (String.valueOf(s).equals("")) {

                    subtractor = 0;
                    changesMade = false;

                } else {

                    try {
                        subtractor = Integer.parseInt(s+"");

                        if ( entryData.getHoursLeftNum(subtractor) < 0 ) {

                            allowChanges = false;

                        } else {

                            if ( entryData.getHoursLeftNum(subtractor) == 0 ) {

                                saveAndFinishBool = true;
                            }

                            allowChanges = true;
                        }

                    }  catch ( NumberFormatException e ){

                        Log.e(TAG, "Exception caught: " + e);
                    }

                }

                changeNumber = true;
            }

        }
    };

    private TextWatcher dailyWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @SuppressLint("SetTextI18n")
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @SuppressLint("SetTextI18n")
        @Override
        public void afterTextChanged(Editable s) {

            if ( changeNumber ) {

                changeNumber = false;

                if (String.valueOf(s).equals("")) {

                    weeklyHours.setText("");
                    subtractor = 0;

                } else {

                    try {
                        int num = Integer.parseInt(s+"");
                        num = num*7;

                        weeklyHours.setText(num+"");
                        subtractor = num;

                        if ( entryData.getHoursLeftNum(subtractor) < 0 ) {

                            allowChanges = false;

                        } else {

                            if ( entryData.getHoursLeftNum(subtractor) == 0 ) {

                                saveAndFinishBool = true;
                            }

                            allowChanges = true;
                        }

                    }  catch ( NumberFormatException e ){

                        Log.e(TAG, "Exception caught: " + e);
                    }

                }

                changeNumber = true;
            }

        }
    };

    @SuppressLint("SetTextI18n")
    private void saveAndFinish() {

        if (entryData.getHoursLeftNum(subtractor) < 0) {

            Toast.makeText(getApplicationContext(), subtractor + entryData.getHoursLeftNum(subtractor)
                    + " hours is more than a week, that's impressive! Now type a valid number", Toast.LENGTH_LONG).show();

        } else if (entryData.getHoursLeftNum(subtractor) == 0 && isMandatory) {

            Toast.makeText(getApplicationContext(),
                    "edit mandatory objectives, you haven't left hours for goals",
                    Toast.LENGTH_SHORT).show();

        } else if (isMandatory) {

            isMandatory = false;
            refresh();

            entryData = new EntryViewData(isMandatory, entryData.getHoursLeftNum(0));
            mainTitle.setText(entryData.getMainTitle());

        } else {

            SharedPreferences.Editor editor = sharedPref.edit();

            editor.putInt(GeneralAppData.HOURS_LEFT, entryData.getHoursLeftNum(subtractor)).apply();

            intent = new Intent(getApplicationContext(), MainActivity.class);
            Entry.this.startActivity(intent);
        }
    }

    public static void setAlphaAnimation(View v) {
        ObjectAnimator fadeOut = ObjectAnimator.ofFloat(v, "alpha",  1f, 0f);
        fadeOut.setDuration(1000);
        ObjectAnimator fadeIn = ObjectAnimator.ofFloat(v, "alpha", 0f, 1f);
        fadeIn.setDuration(1000);

        final AnimatorSet mAnimationSet = new AnimatorSet();

        mAnimationSet.play(fadeIn).after(fadeOut);


                mAnimationSet.start();
        mAnimationSet.start();
    }

    private class MyAsyncFirst extends AsyncTask<Void, Void, Void> {


        @Override
        protected Void doInBackground(Void... voids) {

            try {

                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            running();

            return null;
        }

        private void running() {

            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    if (firstSave) {

                        nextFunc();

                    } else {

                        saveAndFinish();
                        Toast.makeText(getApplicationContext(),
                                "Now add your goals!", Toast.LENGTH_SHORT).show();
                    }

                }
            });
        }
    }

} // end class
