package com.nothing.timing.timing.bloc.data;

import android.util.Log;

import com.nothing.timing.timing.bloc.sql.SqlNames;

public class EntryViewData {

    private final String TAG = EntryViewData.class.getSimpleName();

    private int hoursLeft;
    private String mainTitle;
    private String userObjective;

    private MandatoryObjectives mandatoryObjectives;

    public EntryViewData(boolean isMandatory, int hoursLeft) {

        mandatoryObjectives = new MandatoryObjectives();
        if (isMandatory) {

            mainTitle = "Set You Must Do's";
            this.hoursLeft = 168;

        } else {

            mainTitle = "Set Your Goals";
            this.hoursLeft = hoursLeft;
        }
    }

    public String getMainTitle() {
        return mainTitle;
    }

    public String getObjective(int num) {

        return mandatoryObjectives.getTitleValue(num);
    }


    public String getHoursLeft(int subtract) {

        hoursLeft = hoursLeft - subtract;
        return hoursLeft + " Hours Left";
    }

    public int getHoursLeftNum (int subtractor) {

        return hoursLeft - subtractor;
    }

    public String getUserObjective() {
        return userObjective;
    }

    public void setUserObjective(String userObjective) {
        this.userObjective = userObjective;
    }
}
