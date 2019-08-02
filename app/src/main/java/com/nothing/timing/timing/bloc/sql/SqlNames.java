package com.nothing.timing.timing.bloc.sql;

public class SqlNames {

    public static final String ALL = "all";

    public static class Accomplish {

        public final static String TITLES = "titles";
        public final static String HOURS = "hours";

        public static String getTableName() {
            return "accomplish";
        }
    }

    public static class Progress {

        public final static String TITLES = "titles";
        public final static String HOURS = "hours";

        public static String getTableName() {
            return "progress";
        }
    }

    public static class Mandatory {

        public final static String TITLES = "titles";
        public final static String HOURS = "hours";

        public static String getTableName() {

            return "mandatory";
        }
    }

}
