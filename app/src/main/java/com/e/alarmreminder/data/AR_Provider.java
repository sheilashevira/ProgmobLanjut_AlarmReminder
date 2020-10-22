package com.e.alarmreminder.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class AR_Provider extends ContentProvider {

    public static final String LOG_TAG = AR_Provider.class.getSimpleName();

    private static final int REMINDER = 100;

    private static final int REMINDER_ID = 101;

    private static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {

        uriMatcher.addURI(AR_Contract.CONTENT_AUTHORITY, AR_Contract.PATH_VEHICLE, REMINDER);

        uriMatcher.addURI(AR_Contract.CONTENT_AUTHORITY, AR_Contract.PATH_VEHICLE + "/#", REMINDER_ID);
    }

    private AR_DBHelper arDbHelper;

    @Override
    public boolean onCreate() {
        arDbHelper = new AR_DBHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        SQLiteDatabase database = arDbHelper.getReadableDatabase();

        //Kursor untuk menampung hasil query
        Cursor cursor = null;

        int match = uriMatcher.match(uri);
        switch (match) {
            case REMINDER:
                cursor = database.query(AR_Contract.AlarmReminderEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            case REMINDER_ID:
                selection = AR_Contract.AlarmReminderEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };

                cursor = database.query(AR_Contract.AlarmReminderEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }


        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        final int match = uriMatcher.match(uri);
        switch (match) {
            case REMINDER:
                return AR_Contract.AlarmReminderEntry.CONTENT_LIST_TYPE;
            case REMINDER_ID:
                return AR_Contract.AlarmReminderEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        final int match = uriMatcher.match(uri);
        switch (match) {
            case REMINDER:
                return insertReminder(uri, contentValues);

            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }

    private Uri insertReminder(Uri uri, ContentValues values) {

        SQLiteDatabase database = arDbHelper.getWritableDatabase();

        long id = database.insert(AR_Contract.AlarmReminderEntry.TABLE_NAME, null, values);

        if (id == -1) {
            Log.e(LOG_TAG, "Failed to insert row for " + uri);
            return null;
        }

        getContext().getContentResolver().notifyChange(uri, null);

        return ContentUris.withAppendedId(uri, id);
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {

        SQLiteDatabase database = arDbHelper.getWritableDatabase();

        int rowsDeleted;

        final int match = uriMatcher.match(uri);
        switch (match) {
            case REMINDER:
                rowsDeleted = database.delete(AR_Contract.AlarmReminderEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case REMINDER_ID:
                selection = AR_Contract.AlarmReminderEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                rowsDeleted = database.delete(AR_Contract.AlarmReminderEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }

        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String selection,
                      String[] selectionArgs) {
        final int match = uriMatcher.match(uri);
        switch (match) {
            case REMINDER:
                return updateReminder(uri, contentValues, selection, selectionArgs);
            case REMINDER_ID:
                selection = AR_Contract.AlarmReminderEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                return updateReminder(uri, contentValues, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }
    }

    private int updateReminder(Uri uri, ContentValues values, String selection, String[] selectionArgs) {

        if (values.size() == 0) {
            return 0;
        }

        SQLiteDatabase database = arDbHelper.getWritableDatabase();

        int rowsUpdated = database.update(AR_Contract.AlarmReminderEntry.TABLE_NAME, values, selection, selectionArgs);

        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowsUpdated;
    }
}
