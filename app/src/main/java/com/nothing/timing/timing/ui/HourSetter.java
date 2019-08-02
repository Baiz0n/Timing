package com.nothing.timing.timing.ui;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.nothing.timing.timing.R;
import com.nothing.timing.timing.bloc.data.GeneralAppData;
import com.nothing.timing.timing.bloc.data.HourSetterData;
import com.nothing.timing.timing.bloc.data.IntentData;
import com.nothing.timing.timing.bloc.sql.AccomplishSql;
import com.nothing.timing.timing.bloc.sql.ProgressSql;
import com.nothing.timing.timing.tools.DialogMaker;

public class HourSetter extends AppCompatActivity {

    private final String TAG = HourSetter.class.getSimpleName();

    private Intent intent;
    private ProgressSql progressSql;
    private AccomplishSql accomplishSql;

    private TextView objective;
    private ProgressBar progressBar;
    private TextView details;
    private Button save;
    private EditText addTime;
    private Button reset;
    private Button remove;

    private boolean someWasAdded = false;
    private int add = 0;
    private boolean success = false;

    private HourSetterData data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hour_setter);

        objective = findViewById(R.id.objective);
        progressBar = findViewById(R.id.progress_bar);
        details = findViewById(R.id.details);
        save = findViewById(R.id.save);
        addTime = findViewById(R.id.add_time);
        reset = findViewById(R.id.reset);
        remove = findViewById(R.id.remove);

        progressSql = new ProgressSql(this);
        accomplishSql = new AccomplishSql(this);

        getIncomingIntent();
        populate();
        addFunctionality();
    }

    private void getIncomingIntent() {

        if (getIntent().hasExtra(IntentData.OBJECTIVE)) {

            data = new HourSetterData(
            getIntent().getStringExtra(IntentData.OBJECTIVE),
            getIntent().getIntExtra(IntentData.OUTOF, 0),
            getIntent().getIntExtra(IntentData.TOTAL, 0)
            );

        }
    }

    private void populate() {

        objective.setText(data.getObjective());
        MyTask myTask = new MyTask();
        myTask.execute();
    }

    private void addFunctionality() {

        save.setOnClickListener(saveClick);
        reset.setOnClickListener(resetClick);
        remove.setOnClickListener(removeClick);
        addTime.addTextChangedListener(addTimeWatcher);

        save.setOnTouchListener(GeneralAppData.changeYellowTouchButton);
        remove.setOnTouchListener(GeneralAppData.changeGreenTouchButton);
        reset.setOnTouchListener(GeneralAppData.changeGreenTouchButton);
    }

    // messy code ==>

    private View.OnClickListener saveClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            if (someWasAdded) {

                if (progressSql.editData(data.getObjective(), "", String.valueOf(data.getNewOutof(add)))) {

                    if ( success ) {

                        shortToast("WELL DONE! You've completed your goal!");

                    } else {

                        shortToast(add + " hours added. Keep it up, well done!");
                    }
                    
                    goHome();

                } else {

                    shortToast("Something went wrong, try again");
                }

            } else {

                shortToast("Nothing was changed");
                goHome();
            }

        }
    };

    private View.OnClickListener resetClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            DialogInterface.OnClickListener dialog = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which){
                        case DialogInterface.BUTTON_POSITIVE:
                            //Yes button clicked

                            if (progressSql.editData(data.getObjective(),"", "0") ) {

                                resetThis();
                                shortToast(data.getObjective() + "'s progress was reset!");
                            }

                            break;

                        case DialogInterface.BUTTON_NEGATIVE:
                            //No button clicked
                            break;
                    }
                }


            };
            DialogMaker.executeDialog(HourSetter.this,
                    "Are you sure you want to reset "+ data.getObjective() + "?",
                    dialog);
        }
    };

    private View.OnClickListener removeClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            DialogInterface.OnClickListener dialog = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which){
                        case DialogInterface.BUTTON_POSITIVE:
                            //Yes button clicked

                            if (progressSql.delData(data.getObjective())
                                && accomplishSql.delData(data.getObjective())) {

                                shortToast(data.getObjective() + " was deleted!");
                                addHoursBackToSharedPrefs();
                                goHome();
                            }

                            break;

                        case DialogInterface.BUTTON_NEGATIVE:
                            //No button clicked
                            break;
                    }
                }


            };
            DialogMaker.executeDialog(HourSetter.this,
                    "Are you sure you want to remove " + data.getObjective() + "?",
                    dialog);
        }
    };

    private TextWatcher addTimeWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {

            if ( !String.valueOf(s).equals("") ) {

                    int i = Integer.parseInt(String.valueOf(s));


                    if (i == 0) {

                        someWasAdded = false;

                    } else if ( data.getNewOutof(i) > data.getOverall() ) {

                        addTime.setText(String.valueOf(data.getOverall() - data.getOutof() ) );

                    } else if ( data.getNewOutof(i) == data.getOverall() ) {

                        success = true;
                        someWasAdded = true;

                        add = i;

                    } else {

                        success = false;
                        someWasAdded = true;

                        add = i;
                    }
                } else {

                someWasAdded = false;
                }
            }
    };

    private void goHome() {

        intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    private void shortToast(String msg) {

        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    private void resetThis() {

        data = new HourSetterData(data.getObjective(), 0, data.getOverall());

        progressBar.setProgress(data.getProgress());
        details.setText(data.getDetails());
    }

    private void addHoursBackToSharedPrefs() {

        SharedPreferences sharedPref = getSharedPreferences(GeneralAppData.PREF_TITLE, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();

        int oldHoursLeft = sharedPref.getInt(GeneralAppData.HOURS_LEFT, 0);
        int newHoursLeft = oldHoursLeft + data.getOverall();

        editor.putInt(GeneralAppData.HOURS_LEFT, newHoursLeft);

        editor.apply();
    }

    private class MyTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {

            int max = data.getProgress();
            int scale = 0;

            while ( scale <=  max) {

                scaleUpPercent(scale);

                try {

                    Thread.sleep(15);

                } catch ( InterruptedException e ) {

                    Log.e(TAG, "It wasn't night time!");
                }
                scale++;
            }
            return null;
        }

        private void scaleUpPercent(final int scale) {

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    progressBar.setProgress(scale);
                    details.setText( scale + "% completed " + data.getOutof() + " / " + data.getOverall() );
                }
            });
        }
    } // end aysnc

}
