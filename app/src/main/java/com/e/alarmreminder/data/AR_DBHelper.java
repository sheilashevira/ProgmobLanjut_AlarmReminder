package com.e.alarmreminder.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class AR_DBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "db_alarm_reminder.db";
    private static final int DATABASE_VERSION = 1;
    public AR_DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        //Create tabel database+tipe data
        String SQL_CREATE_ALARM_TABLE =  "CREATE TABLE "
                + AR_Contract.AlarmReminderEntry.TABLE_NAME + " ("
                + AR_Contract.AlarmReminderEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + AR_Contract.AlarmReminderEntry.KEY_TITLE + " TEXT NOT NULL, "
                + AR_Contract.AlarmReminderEntry.KEY_DATE + " TEXT NOT NULL, "
                + AR_Contract.AlarmReminderEntry.KEY_TIME + " TEXT NOT NULL, "
                + AR_Contract.AlarmReminderEntry.KEY_REPEAT + " TEXT NOT NULL, "
                + AR_Contract.AlarmReminderEntry.KEY_REPEAT_INTERVAL + " TEXT NOT NULL, "
                + AR_Contract.AlarmReminderEntry.KEY_REPEAT_TYPE + " TEXT NOT NULL, "
                + AR_Contract.AlarmReminderEntry.KEY_ACTIVE + " TEXT NOT NULL " + " );";

        //Eksekusi SQL
        sqLiteDatabase.execSQL(SQL_CREATE_ALARM_TABLE);


    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

}
