package com.nothing.timing.timing.tools;
import android.database.Cursor;

import com.nothing.timing.timing.bloc.sql.AccomplishSql;
import com.nothing.timing.timing.bloc.sql.MandatorySql;
import com.nothing.timing.timing.bloc.sql.SqlNames;

public class Checker {

    public static boolean mandatoryTitleAlreadyExists(MandatorySql db, String title) {

        Cursor allRows = db.getData(SqlNames.Mandatory.TITLES);
        String checker;

        allRows.moveToFirst();

        while ( !allRows.isAfterLast() ) {

            checker = allRows.getString(0);

            if ( checker.equals(title) ) {

                return true;
            }

            allRows.moveToNext();
        }
        return false;
    }

    public static boolean accompishTitleAlreadyExists(AccomplishSql db, String title) {

        Cursor allRows = db.getData(SqlNames.Mandatory.TITLES);
        String checker;

        allRows.moveToFirst();

        while ( !allRows.isAfterLast() ) {

            checker = allRows.getString(0);

            if ( checker.equals(title) ) {

                return true;
            }

            allRows.moveToNext();
        }
        return false;
    }
}
