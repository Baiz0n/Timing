package com.nothing.timing.timing.bloc.data;

public class MandatoryObjectives {

    private String mandObjectives[] = {

            "Sleeping",
            "Get ready in the morning",
            "Work",
            "Cook",
            "Drive",
            "Practice Sports",
            "Shopping",
            "House work"
    };

    public String getTitleValue(int num) {

        if ( num >= mandObjectives.length ) {

            return null;
        }

        return mandObjectives[num];
    }
}
