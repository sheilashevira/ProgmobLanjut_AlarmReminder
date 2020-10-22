package com.e.alarmreminder;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NavUtils;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.RadialPickerLayout;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import java.util.Calendar;
import com.e.alarmreminder.data.AR_Contract;
import com.e.alarmreminder.reminder.AR_Scheduler;

public class AddReminder extends AppCompatActivity implements
        TimePickerDialog.OnTimeSetListener,
        DatePickerDialog.OnDateSetListener, LoaderManager.LoaderCallbacks<Cursor> {


    private Toolbar toolbaraddreminder;
    private EditText etTittle;
    private TextView tvDate, tvTime, tvRepeat, tvRepeatInterval, tvRepeatType;
    private FloatingActionButton fabOn, fabOff;
    private Calendar calendar;
    private int years, month, day, hour, minutes;
    private long repeatTime;
    private Switch repeatSwitch;
    private String title;
    private String date;
    private String time;
    private String repeat;
    private String repeatInterval;
    private String repeatType;
    private String notifActive;

    private static final int EXISTING_VEHICLE_LOADER = 0;

    private Uri currentReminderUri;
    private boolean vehicleChanged = false;

    //deklarasi variabel db
    private static final String KEY_TITLE = "title_key";
    private static final String KEY_DATE = "date_key";
    private static final String KEY_TIME = "time_key";
    private static final String KEY_REPEAT = "repeat_key";
    private static final String KEY_REPEAT_INTERVAL = "repeat_interval_key";
    private static final String KEY_REPEAT_TYPE = "repeat_type_key";
    private static final String KEY_ACTIVE = "active_key";

    //Nilai konstan dalam milidetik
    private static final long milMinute = 60000L;
    private static final long milHour = 3600000L;
    private static final long milDay = 86400000L;
    private static final long milWeek = 604800000L;
    private static final long milMonth = 2592000000L;

    private View.OnTouchListener touchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            vehicleChanged = true;
            return false;
        }
    };

    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_reminder);

        Intent intent = getIntent();
        currentReminderUri = intent.getData();

        if (currentReminderUri == null) {
            setTitle(getString(R.string.add_reminder_details));
            //Menyembunyikan tombol delete disaat belum ada data
            invalidateOptionsMenu();
        } else {
            setTitle(getString(R.string.edit_reminder));
            getLoaderManager().initLoader(EXISTING_VEHICLE_LOADER, null, this);
        }

        //Inisialisasi tampilan per variabel
        toolbaraddreminder = (Toolbar) findViewById(R.id.toolbaraddreminder);
        etTittle = (EditText) findViewById(R.id.tittle);
        tvDate = (TextView) findViewById(R.id.set_date);
        tvTime = (TextView) findViewById(R.id.set_time);
        tvRepeat = (TextView) findViewById(R.id.set_repeat);
        tvRepeatInterval = (TextView) findViewById(R.id.set_repeatinterval);
        tvRepeatType = (TextView) findViewById(R.id.set_repeattype);
        repeatSwitch = (Switch) findViewById(R.id.repeat_switch);
        fabOn = (FloatingActionButton) findViewById(R.id.btn_notification_on);
        fabOff = (FloatingActionButton) findViewById(R.id.btn_notification_off);

        //Default value
        notifActive = "true"; //Kalo notif aktif true, kalo nonaktif false
        repeat = "true"; //Kalo repeat aktif true, kalo nonaktif false
        repeatInterval = Integer.toString(1); //Tampilan angka yang muncul pas baru nambah data
        repeatType = "Hour"; //Tampilan hour yang muncul pas baru nambah data

        calendar = Calendar.getInstance();
        years = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DATE);
        hour = calendar.get(Calendar.HOUR_OF_DAY);
        minutes = calendar.get(Calendar.MINUTE);

        //tampilan tanggal dan waktu yang muncul sesuai tanggal dan waktu di emu
        date = day + "/" + month + "/" + years;
        time = hour + ":" + minutes;

        etTittle.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                title = s.toString().trim();
                etTittle.setError(null);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        //Pengaturan textview
        tvDate.setText(date);
        tvTime.setText(time);
        tvRepeat.setText("Every " + repeatInterval + " " + repeatType + "(s)");
        tvRepeatInterval.setText(repeatInterval);
        tvRepeatType.setText(repeatType);

        //Save status data yang udah dimasukkan
        if (savedInstanceState != null) {
            String savedTitle = savedInstanceState.getString(KEY_TITLE);
            etTittle.setText(savedTitle);
            title = savedTitle;

            String savedDate = savedInstanceState.getString(KEY_DATE);
            tvDate.setText(savedDate);
            date = savedDate;

            String savedTime = savedInstanceState.getString(KEY_TIME);
            tvTime.setText(savedTime);
            time = savedTime;

            String saveRepeat = savedInstanceState.getString(KEY_REPEAT);
            tvRepeat.setText(saveRepeat);
            repeat = saveRepeat;

            String savedRepeatNo = savedInstanceState.getString(KEY_REPEAT_INTERVAL);
            tvRepeatInterval.setText(savedRepeatNo);
            repeatInterval = savedRepeatNo;

            String savedRepeatType = savedInstanceState.getString(KEY_REPEAT_TYPE);
            tvRepeatType.setText(savedRepeatType);
            repeatType = savedRepeatType;

            notifActive = savedInstanceState.getString(KEY_ACTIVE);
        }

        //Tombol aktif/nonaktifnya
        if (notifActive.equals("false")) {
            fabOff.setVisibility(View.VISIBLE);
            fabOn.setVisibility(View.GONE);

        } else if (notifActive.equals("true")) {
            fabOff.setVisibility(View.GONE);
            fabOn.setVisibility(View.VISIBLE);
        }

        setSupportActionBar(toolbaraddreminder);
        getSupportActionBar().setTitle(R.string.add_reminder);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //Ngambil textview
        outState.putCharSequence(KEY_TITLE, etTittle.getText());
        outState.putCharSequence(KEY_DATE, tvDate.getText());
        outState.putCharSequence(KEY_TIME, tvTime.getText());
        outState.putCharSequence(KEY_REPEAT, tvRepeat.getText());
        outState.putCharSequence(KEY_REPEAT_INTERVAL, tvRepeatInterval.getText());
        outState.putCharSequence(KEY_REPEAT_TYPE, tvRepeatType.getText());
        outState.putCharSequence(KEY_ACTIVE, notifActive);
    }

    //On Clicking Time Picker
    public void setTime(View v) {
        Calendar now = Calendar.getInstance();
        com.wdullaer.materialdatetimepicker.time.TimePickerDialog tpd = com.wdullaer.materialdatetimepicker.time.TimePickerDialog.newInstance(
                this,
                now.get(Calendar.HOUR_OF_DAY),
                now.get(Calendar.MINUTE),
                false
        );
        tpd.setThemeDark(false);
        tpd.show(getFragmentManager(), "Timepickerdialog");
    }

    //On Clicking Date Picker
    public void setDate(View v) {
        Calendar now = Calendar.getInstance();
        com.wdullaer.materialdatetimepicker.date.DatePickerDialog dpd = com.wdullaer.materialdatetimepicker.date.DatePickerDialog.newInstance(
                this,
                now.get(Calendar.YEAR),
                now.get(Calendar.MONTH),
                now.get(Calendar.DAY_OF_MONTH)
        );
        dpd.show(getFragmentManager(), "Datepickerdialog");
    }

    //Ngambil waktu dari time picker
    @Override
    public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute) {
        hour = hourOfDay;
        minutes = minute;
        if (minute < 10) {
            time = hourOfDay + ":" + "0" + minute;
        } else {
            time = hourOfDay + ":" + minute;
        }
        tvTime.setText(time);
    }

    //Ngambil tanggal dari time picker
    @Override
    public void onDateSet(com.wdullaer.materialdatetimepicker.date.DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        monthOfYear++;
        day = dayOfMonth;
        month = monthOfYear;
        years = year;
        date = dayOfMonth + "/" + monthOfYear + "/" + year;
        tvDate.setText(date);
    }

    //On clicking tombol notifikasi aktif
    @SuppressLint("RestrictedApi")
    public void selectNotificationOff(View v) {
        fabOff = (FloatingActionButton) findViewById(R.id.btn_notification_off);
        fabOff.setVisibility(View.GONE);
        fabOn = (FloatingActionButton) findViewById(R.id.btn_notification_on);
        fabOn.setVisibility(View.VISIBLE);
        notifActive = "true";
    }

    //On clicking tombol notifikasi yang nonaktif
    @SuppressLint("RestrictedApi")
    public void selectNotificationOn(View v) {
        fabOn = (FloatingActionButton) findViewById(R.id.btn_notification_on);
        fabOn.setVisibility(View.GONE);
        fabOff = (FloatingActionButton) findViewById(R.id.btn_notification_off);
        fabOff.setVisibility(View.VISIBLE);
        notifActive = "false";
    }

    //On clicking tombol switch
    public void SwitchRepeat(View view) {
        boolean on = ((Switch) view).isChecked();
        if (on) {
            repeat = "true";
            tvRepeat.setText("Every " + repeatInterval + " " + repeatType + "(s)");
        } else {
            repeat = "false";
            tvRepeat.setText(R.string.repeat_off);
        }
    }

    //Tampilan box ketika type repetition di klik
    public void setRepeatType(View v) {
        final String[] items = new String[5];

        items[0] = "Minute";
        items[1] = "Hour";
        items[2] = "Day";
        items[3] = "Week";
        items[4] = "Month";

        // Create List Dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Type");
        builder.setItems(items, new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int item) {

                repeatType = items[item];
                tvRepeatType.setText(repeatType);
                tvRepeat.setText("Every " + repeatInterval + " " + repeatType + "(s)");
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    //Tampilan box ketika mengisikan jumlah interval repetition
    public void setRepeatInterval(View v) {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Enter Number");

        //Membuat EditText untuk memasukkan jumlah repetition
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_NUMBER);
        alert.setView(input);
        alert.setPositiveButton("Ok",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {

                        if (input.getText().toString().length() == 0) {
                            repeatInterval = Integer.toString(1);
                            tvRepeatInterval.setText(repeatInterval);
                            tvRepeat.setText("Every " + repeatInterval + " " + repeatType + "(s)");
                        } else {
                            repeatInterval = input.getText().toString().trim();
                            tvRepeatInterval.setText(repeatInterval);
                            tvRepeat.setText("Every " + repeatInterval + " " + repeatType + "(s)");
                        }
                    }
                });
        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {

            }
        });
        alert.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //Nampilin menu tombol delete & save di toolbar menu tambah reminder
        getMenuInflater().inflate(R.menu.menu_add_reminder, menu);
        return true;
    }

    //Dipanggilnya setelah invalidateOptionsMenu() jadi ada tombol yang bisa disembunyiin kalo belum kepake
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        //If belum pernah buat reminder, maka sembunyikan tombol delete
        if (currentReminderUri == null) {
            MenuItem menuItem = menu.findItem(R.id.discard_reminder);
            menuItem.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            //Hasil ketika tombol save di klik
            case R.id.save_reminder:

                if (etTittle.getText().toString().length() == 0) {
                    etTittle.setError("Reminder Title cannot be blank!");
                } else {
                    saveReminder();
                    finish();
                }
                return true;

            //Hasil ketika tombol delete di klik
            case R.id.discard_reminder:
                //Muncul pop up konfirmasi delete
                showDeleteConfirmationDialog();
                return true;

            //Tombol back toolbar ke parent activity
            case android.R.id.home:

                if (!vehicleChanged) {
                    NavUtils.navigateUpFromSameTask(AddReminder.this);
                    return true;
                }

                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                NavUtils.navigateUpFromSameTask(AddReminder.this);
                            }
                        };

                showUnsavedChangesDialog(discardButtonClickListener);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void showUnsavedChangesDialog(
            DialogInterface.OnClickListener discardButtonClickListener) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_changes_dialog_msg);
        builder.setPositiveButton(R.string.discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                //Klik keep editing, sehingga pop up alert diabaikan saja
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        //Create alert dialog dan tampilkan
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void showDeleteConfirmationDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                deleteReminder();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                //Klik delete, sehingga pop up alert diabaikan saja
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        //Create alert dialog dan tampilkan
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void deleteReminder() {

        if (currentReminderUri != null) {

            int rowsDeleted = getContentResolver().delete(currentReminderUri, null, null);

            //Tampilkan toast kalo delete eror
            if (rowsDeleted == 0) {
                //Delete eror/gagal
                Toast.makeText(this, getString(R.string.failed_delete),
                        Toast.LENGTH_SHORT).show();
            } else {
                //Delete sukses
                Toast.makeText(this, getString(R.string.success_delete),
                        Toast.LENGTH_SHORT).show();
            }
        }

        //Keluar acitivity
        finish();
    }

    //On clicking tombol save
    public void saveReminder() {

        ContentValues values = new ContentValues();

        values.put(AR_Contract.AlarmReminderEntry.KEY_TITLE, title);
        values.put(AR_Contract.AlarmReminderEntry.KEY_DATE, date);
        values.put(AR_Contract.AlarmReminderEntry.KEY_TIME, time);
        values.put(AR_Contract.AlarmReminderEntry.KEY_REPEAT, repeat);
        values.put(AR_Contract.AlarmReminderEntry.KEY_REPEAT_INTERVAL, repeatInterval);
        values.put(AR_Contract.AlarmReminderEntry.KEY_REPEAT_TYPE, repeatType);
        values.put(AR_Contract.AlarmReminderEntry.KEY_ACTIVE, notifActive);


        //Setting kalender dan waktu
        calendar.set(Calendar.MONTH, --month);
        calendar.set(Calendar.YEAR, years);
        calendar.set(Calendar.DAY_OF_MONTH, day);
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minutes);
        calendar.set(Calendar.SECOND, 0);

        long selectedTimestamp = calendar.getTimeInMillis();

        //Tipe repetition
        if (repeatType.equals("Minute")) {
            repeatTime = Integer.parseInt(repeatInterval) * milMinute;
        } else if (repeatType.equals("Hour")) {
            repeatTime = Integer.parseInt(repeatInterval) * milHour;
        } else if (repeatType.equals("Day")) {
            repeatTime = Integer.parseInt(repeatInterval) * milDay;
        } else if (repeatType.equals("Week")) {
            repeatTime = Integer.parseInt(repeatInterval) * milWeek;
        } else if (repeatType.equals("Month")) {
            repeatTime = Integer.parseInt(repeatInterval) * milMonth;
        }

        if (currentReminderUri == null) {
            //Tambah new reminder
            Uri newUri = getContentResolver().insert(AR_Contract.AlarmReminderEntry.CONTENT_URI, values);

            if (newUri == null) {
                //Toast save eror
                Toast.makeText(this, getString(R.string.failed_insert_reminder),
                        Toast.LENGTH_SHORT).show();
            } else {
                //Toast save sukses
                Toast.makeText(this, getString(R.string.success_insert_reminder),
                        Toast.LENGTH_SHORT).show();
            }
        } else {

            int rowsAffected = getContentResolver().update(currentReminderUri, values, null, null);

            if (rowsAffected == 0) {
                //Toast edit eror
                Toast.makeText(this, getString(R.string.failed_update),
                        Toast.LENGTH_SHORT).show();
            } else {
                //Toast edit sukses
                Toast.makeText(this, getString(R.string.success_update),
                        Toast.LENGTH_SHORT).show();
            }
        }

        //Create notification baru
        if (notifActive.equals("true")) {
            if (repeat.equals("true")) {
                new AR_Scheduler().setRepeatAlarm(getApplicationContext(), selectedTimestamp, currentReminderUri, repeatTime);
            } else if (repeat.equals("false")) {
                new AR_Scheduler().setAlarm(getApplicationContext(), selectedTimestamp, currentReminderUri);
            }

            Toast.makeText(this, "Alarm time is " + selectedTimestamp,
                    Toast.LENGTH_LONG).show();
        }

        //Toast konfirm save reminder
        Toast.makeText(getApplicationContext(), "Saved",
                Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {

        String[] projection = {
                AR_Contract.AlarmReminderEntry._ID,
                AR_Contract.AlarmReminderEntry.KEY_TITLE,
                AR_Contract.AlarmReminderEntry.KEY_DATE,
                AR_Contract.AlarmReminderEntry.KEY_TIME,
                AR_Contract.AlarmReminderEntry.KEY_REPEAT,
                AR_Contract.AlarmReminderEntry.KEY_REPEAT_INTERVAL,
                AR_Contract.AlarmReminderEntry.KEY_REPEAT_TYPE,
                AR_Contract.AlarmReminderEntry.KEY_ACTIVE,
        };

        return new CursorLoader(this,
                currentReminderUri,
                projection,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (cursor == null || cursor.getCount() < 1) {
            return;
        }

        if (cursor.moveToFirst()) {
            int titleColumnIndex = cursor.getColumnIndex(AR_Contract.AlarmReminderEntry.KEY_TITLE);
            int dateColumnIndex = cursor.getColumnIndex(AR_Contract.AlarmReminderEntry.KEY_DATE);
            int timeColumnIndex = cursor.getColumnIndex(AR_Contract.AlarmReminderEntry.KEY_TIME);
            int repeatColumnIndex = cursor.getColumnIndex(AR_Contract.AlarmReminderEntry.KEY_REPEAT);
            int repeatNoColumnIndex = cursor.getColumnIndex(AR_Contract.AlarmReminderEntry.KEY_REPEAT_INTERVAL);
            int repeatTypeColumnIndex = cursor.getColumnIndex(AR_Contract.AlarmReminderEntry.KEY_REPEAT_TYPE);
            int activeColumnIndex = cursor.getColumnIndex(AR_Contract.AlarmReminderEntry.KEY_ACTIVE);

            //Ekstrak nilai dari kursor per index kolom
            String title = cursor.getString(titleColumnIndex);
            String date = cursor.getString(dateColumnIndex);
            String time = cursor.getString(timeColumnIndex);
            String repeat = cursor.getString(repeatColumnIndex);
            String repeatInterval = cursor.getString(repeatNoColumnIndex);
            String repeatType = cursor.getString(repeatTypeColumnIndex);
            String notifActive = cursor.getString(activeColumnIndex);


            //Update hasil list data sesuai database
            etTittle.setText(title);
            tvDate.setText(date);
            tvTime.setText(time);
            tvRepeatInterval.setText(repeatInterval);
            tvRepeatType.setText(repeatType);
            tvRepeat.setText("Every " + repeatInterval + " " + repeatType + "(s)");
            if (repeat.equals("false")) {
                repeatSwitch.setChecked(false);
                tvRepeat.setText(R.string.repeat_off);

            } else if (repeat.equals("true")) {
                repeatSwitch.setChecked(true);
            }

        }


    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
