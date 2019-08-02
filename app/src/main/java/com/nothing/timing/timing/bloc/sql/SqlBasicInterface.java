package com.nothing.timing.timing.bloc.sql;

import android.database.Cursor;

public interface SqlBasicInterface {

     boolean addData(String title, String hour);
     Cursor getData(String column);
     Cursor getRow(String column, String title);
     boolean delData(String title);
     boolean delAllData();
     boolean editData(String oldTitle, String title, String hour);
}
