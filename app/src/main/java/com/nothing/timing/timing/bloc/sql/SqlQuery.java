package com.nothing.timing.timing.bloc.sql;

public class SqlQuery {

    private String table;

    private String titles;
    private String hours;

    protected SqlQuery(String table) {

        this.table = table;

        if (table.equals(SqlNames.Accomplish.getTableName())) {

            this.titles = SqlNames.Accomplish.TITLES;
            this.hours = SqlNames.Accomplish.HOURS;

        } else if (table.equals(SqlNames.Progress.getTableName())) {

            this.titles = SqlNames.Progress.TITLES;
            this.hours = SqlNames.Progress.HOURS;

        } else if (table.equals(SqlNames.Mandatory.getTableName())) {

            this.titles = SqlNames.Mandatory.TITLES;
            this.hours = SqlNames.Mandatory.HOURS;
        }
    }

    protected String getDropQuery() {

        return "DROP TABLE IF EXISTS " + table;
    }

    protected String getInsertQuery(String title, String hour) {

        return "INSERT INTO " + table
                + " (" + titles + "," + hours + ") "
                + "VALUES(" + "'" + title + "'" + ","
                + "'" + hour + "'" +")";
    }

    protected String getSelectQuery(String column) {

        return "SELECT " + column
                + " FROM " + table;
    }

    protected String getSelectRowQuery(String column, String title) {

        return "SELECT " + column
                + " FROM " + table
                + " WHERE " + titles
                + "=" + "'" + title + "'";
    }

    protected String getDeleteQuery(String title) {

        return "DELETE FROM " + table
                + " WHERE " + titles
                + "=" + "'" + title + "'";
    }

    protected String getDeleteAllQuery() {

        return "DELETE FROM " + table;
    }

    protected String getUpdateQuery(String oldTitle, String title, String hour) {

        if ( title.equals("") ) {

            return "UPDATE " + table
                    + " SET "
                    + hours + "=" + hour
                    + " WHERE " + titles
                    + "=" + "'" + oldTitle + "'";
        }

        return "UPDATE " + table
                + " SET "
                + titles + "=" + "'" + title
                + "' "
                + hours + "=" + hour
                + " WHERE " + titles
                + "=" + "'" + oldTitle + "'";
    }

}
