package com.e.alarmreminder.reminder;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.net.Uri;
import android.os.Build;

public class AR_Scheduler {

    public void setAlarm(Context context, long alarmTime, Uri reminderTask) {
        AlarmManager manager = AR_ManageProvider.getAlarmManager(context);

        PendingIntent operation =
                AR_Service.getReminderPendingIntent(context, reminderTask);


        if (Build.VERSION.SDK_INT >= 23) {

            manager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, alarmTime, operation);

        } else if (Build.VERSION.SDK_INT >= 19) {

            manager.setExact(AlarmManager.RTC_WAKEUP, alarmTime, operation);

        } else {

            manager.set(AlarmManager.RTC_WAKEUP, alarmTime, operation);

        }
    }

    public void setRepeatAlarm(Context context, long alarmTime, Uri reminderTask, long RepeatTime) {
        AlarmManager manager = AR_ManageProvider.getAlarmManager(context);

        PendingIntent operation =
                AR_Service.getReminderPendingIntent(context, reminderTask);

        manager.setRepeating(AlarmManager.RTC_WAKEUP, alarmTime, RepeatTime, operation);

    }

    public void cancelAlarm(Context context, Uri reminderTask) {
        AlarmManager manager = AR_ManageProvider.getAlarmManager(context);

        PendingIntent operation =
                AR_Service.getReminderPendingIntent(context, reminderTask);

        manager.cancel(operation);

    }
}
