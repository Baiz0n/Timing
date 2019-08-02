package com.nothing.timing.timing.tools;

public class ValueMaker {

    public static String getCompletedOutOf(int outof, int completed) {

        return outof + " / " + completed;
    }

    public static int getProgress(int outof, int completed) {

        return outof * 100 / completed;
    }
}
