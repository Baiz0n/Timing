package com.nothing.timing.timing.bloc.data;

import com.nothing.timing.timing.tools.ValueMaker;

public class HourSetterData {

    private String objective;
    private int outof;
    private int overall;

   public HourSetterData(String objective, int outof, int overall) {

        this.objective = objective;
        this.outof = outof;
        this.overall = overall;
    }

    public String getObjective() {
        return objective;
    }

    public int getProgress() {

       return ValueMaker.getProgress(outof, overall);
    }

    public String getDetails() {

       return getProgress() + "% completed " + outof + " / " + overall;
    }

    public int getOverall() {

       return overall;
    }

    public int getNewOutof(int add) {

       return outof + add;
    }

    public int getOutof() {

       return outof;
    }
}
