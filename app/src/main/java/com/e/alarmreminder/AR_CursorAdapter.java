package com.e.alarmreminder;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.e.alarmreminder.data.AR_Contract;


public class AR_CursorAdapter extends CursorAdapter {

    private TextView tvtitle, tvdatetime, tvrepeat;
    private ImageView ivimagelist, ivnotifimage;
    private ColorGenerator colorGenerator = ColorGenerator.DEFAULT;
    private TextDrawable textDrawable;

    public AR_CursorAdapter(Context context, Cursor cursor) {
        super(context, cursor, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_alarm, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        tvtitle = (TextView) view.findViewById(R.id.alarm_title);
        tvdatetime = (TextView) view.findViewById(R.id.alarm_date_time);
        tvrepeat = (TextView) view.findViewById(R.id.alarm_repeat);
        ivimagelist = (ImageView) view.findViewById(R.id.image_list);
        ivnotifimage = (ImageView) view.findViewById(R.id.notif_image);

        int titleColumnIndex = cursor.getColumnIndex(AR_Contract.AlarmReminderEntry.KEY_TITLE);
        int dateColumnIndex = cursor.getColumnIndex(AR_Contract.AlarmReminderEntry.KEY_DATE);
        int timeColumnIndex = cursor.getColumnIndex(AR_Contract.AlarmReminderEntry.KEY_TIME);
        int repeatColumnIndex = cursor.getColumnIndex(AR_Contract.AlarmReminderEntry.KEY_REPEAT);
        int repeatintervalColumnIndex = cursor.getColumnIndex(AR_Contract.AlarmReminderEntry.KEY_REPEAT_INTERVAL);
        int repeattypeColumnIndex = cursor.getColumnIndex(AR_Contract.AlarmReminderEntry.KEY_REPEAT_TYPE);
        int activeColumnIndex = cursor.getColumnIndex(AR_Contract.AlarmReminderEntry.KEY_ACTIVE);

        String title = cursor.getString(titleColumnIndex);
        String date = cursor.getString(dateColumnIndex);
        String time = cursor.getString(timeColumnIndex);
        String repeat = cursor.getString(repeatColumnIndex);
        String repeatinterval = cursor.getString(repeatintervalColumnIndex);
        String repeattype = cursor.getString(repeattypeColumnIndex);
        String active = cursor.getString(activeColumnIndex);

        String dateTime = date + " " + time;

        setReminderTitle (title);
        setReminderDateTime (dateTime);
        setReminderRepeat (repeat, repeatinterval, repeattype);
        setActiveNotif (active);
    }

    public void setReminderTitle(String title) {
        tvtitle.setText(title);
        //Buat ikon lingkaran yang terdiri dari warna latar belakang acak dan huruf pertama dari judul
        String letter = "A";

        if(title != null && !title.isEmpty()){
            letter = title.substring(0,1);
        }

        int color = colorGenerator.getRandomColor();

        textDrawable = TextDrawable.builder()
                .buildRound(letter, color);
        ivimagelist.setImageDrawable(textDrawable); //set gambar dengan drawable builder yang berisi huruf awal judul dan warna random
    }

    public void setReminderDateTime(String dateTime) {
        tvdatetime.setText(dateTime);
    }
    //Mengatur tulisan repetition yang muncul
    public void setReminderRepeat(String repeat, String repeatinterval, String repeattype) {
        if(repeat.equals("true")){
            tvrepeat.setText("Every " + repeatinterval + " " + repeattype + "(s)");
        }else if(repeat.equals("false")){
            tvrepeat.setText("Repeat Off");
        }
    }

    public void setActiveNotif(String active) {
        if(active.equals("true")){
            ivnotifimage.setImageResource(R.drawable.icc_notif_on);
        }else if (active.equals("false")){
            ivnotifimage.setImageResource(R.drawable.icc_notif_off);
        }
    }


}
