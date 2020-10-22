package com.e.alarmreminder.reminder;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;

import androidx.core.app.NotificationCompat;
import androidx.core.app.TaskStackBuilder;

import com.e.alarmreminder.AddReminder;
import com.e.alarmreminder.R;
import com.e.alarmreminder.data.AR_Contract;

public class AR_Service extends IntentService {
    private static final String TAG = AR_Service.class.getSimpleName();

    private static final int NOTIFICATION_ID = 42;

    Cursor cursor;

    public static PendingIntent getReminderPendingIntent(Context context, Uri uri) {
        Intent action = new Intent(context, AR_Service.class);
        action.setData(uri);
        return PendingIntent.getService(context, 0, action, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    public AR_Service() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        Uri uri = intent.getData();

        //Menampilkan notifikasi untuk meliha detail agenda
        Intent action = new Intent(this, AddReminder.class);
        action.setData(uri);
        PendingIntent operation = TaskStackBuilder.create(this)
                .addNextIntentWithParentStack(action)
                .getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        //Ambil deskripsi agenda
        if(uri != null){
            cursor = getContentResolver().query(uri, null, null, null, null);
        }

        String description = "";
        try {
            if (cursor != null && cursor.moveToFirst()) {
                description = AR_Contract.getColumnString(cursor, AR_Contract.AlarmReminderEntry.KEY_TITLE);
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        Notification note = new NotificationCompat.Builder(this)
                .setContentTitle(getString(R.string.reminder_title))
                .setContentText(description)
                .setSmallIcon(R.drawable.icc_alert)
                .setContentIntent(operation)
                .setAutoCancel(true)
                .build();

        manager.notify(NOTIFICATION_ID, note);
    }
}
