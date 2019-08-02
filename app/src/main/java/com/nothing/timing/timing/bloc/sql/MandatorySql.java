package com.nothing.timing.timing.bloc.sql;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

public class MandatorySql extends SQLiteOpenHelper implements SqlBasicInterface {

    private SQLiteDatabase db = getWritableDatabase();
    private SqlQuery query = new SqlQuery(SqlNames.Mandatory.getTableName());

    public MandatorySql(Context context) {
        super(context, SqlNames.Mandatory.getTableName(), null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL("CREATE TABLE " + SqlNames.Mandatory.getTableName()
                + " (a INTEGER PRIMARY KEY,"
                + SqlNames.Mandatory.TITLES + " TEXT,"
                + SqlNames.Mandatory.HOURS + ")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL(query.getDropQuery());
    }

    @Override
    public boolean addData(String title, String hour) {

        try {

            Cursor action = db.rawQuery(query.getInsertQuery(title, hour),null);
            action.moveToFirst();
            action.close();

        } catch( SQLiteException e ) {

            return false;
        }

        return true;
    }

    @Override
    public Cursor getData(String column) {

        return db.rawQuery(query.getSelectQuery(column), null);
    }

    @Override
    public Cursor getRow(String column, String title) {

        return db.rawQuery(query.getSelectRowQuery(column, title), null);
    }

    @Override
    public boolean delData(String title) {

        try {
            Cursor action = db.rawQuery(query.getDeleteQuery(title),null);
            action.moveToFirst();
            action.close();

            Cursor cursor = getRow(SqlNames.Mandatory.TITLES, title);

            return String.valueOf(cursor.moveToLast()).equals(title);

        }  catch( SQLiteException e ) {

            return false;
        }
    }

    @Override
    public boolean delAllData() {

        try {
            Cursor cursor = db.rawQuery(query.getDeleteAllQuery(), null);
            cursor.moveToFirst();
            cursor.close();

        } catch (SQLException e) {

            return false;
        }

        return true;
    }

    @Override
    public boolean editData(String oldTitle, String title, String hour) {

        try {
            Cursor action = db.rawQuery((query.getUpdateQuery(oldTitle, title, hour)),null);
            action.moveToFirst();
            action.close();

            Cursor cursor = getRow(SqlNames.Mandatory.TITLES, title);

            return String.valueOf(cursor.moveToLast()).equals(title);

        } catch( SQLiteException e ) {

            return false;
        }

    }


}
