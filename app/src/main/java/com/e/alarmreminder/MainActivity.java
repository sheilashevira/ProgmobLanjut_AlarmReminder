package com.e.alarmreminder;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.app.LoaderManager;
import android.app.ProgressDialog;
import android.content.ContentUris;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.e.alarmreminder.data.AR_Contract;
import com.e.alarmreminder.data.AR_DBHelper;
import com.getbase.floatingactionbutton.FloatingActionButton;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{

    private FloatingActionButton btnaddreminder;
    private Toolbar toolbarmainactivity;
    AR_CursorAdapter arCursorAdapter;
    AR_DBHelper arDbHelper = new AR_DBHelper(this);
    ListView arListView;
    ProgressDialog progressDialog;
    private static final int VEHICLE_LOADER = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Toolbar Tampilan Awal
        toolbarmainactivity = (Toolbar) findViewById(R.id.toolbarmainactivity);
        setSupportActionBar(toolbarmainactivity);
        toolbarmainactivity.setTitle("Alarm Reminder");

        //ListView tampilan awal ketika belum ada alarm
        arListView = (ListView) findViewById(R.id.listview);
        View emptyview = findViewById(R.id.emptyview);
        arListView.setEmptyView(emptyview);

        arCursorAdapter = new AR_CursorAdapter(this, null);
        arListView.setAdapter(arCursorAdapter);
        arListView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Intent intent = new Intent(MainActivity.this, AddReminder.class);
                Uri currentVehicleUri = ContentUris.withAppendedId(AR_Contract.AlarmReminderEntry.CONTENT_URI, id);
                intent.setData(currentVehicleUri);
                startActivity(intent);
            }
        });

        btnaddreminder = (FloatingActionButton) findViewById(R.id.btn_addreminder);
        btnaddreminder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), AddReminder.class);
                startActivity(intent);
            }
        });

        getLoaderManager().initLoader(VEHICLE_LOADER, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        String[] project = {
                AR_Contract.AlarmReminderEntry._ID,
                AR_Contract.AlarmReminderEntry.KEY_TITLE,
                AR_Contract.AlarmReminderEntry.KEY_DATE,
                AR_Contract.AlarmReminderEntry.KEY_TIME,
                AR_Contract.AlarmReminderEntry.KEY_REPEAT,
                AR_Contract.AlarmReminderEntry.KEY_REPEAT_INTERVAL,
                AR_Contract.AlarmReminderEntry.KEY_REPEAT_TYPE,
                AR_Contract.AlarmReminderEntry.KEY_ACTIVE
        };

        return new CursorLoader(this, AR_Contract.AlarmReminderEntry.CONTENT_URI, project, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        arCursorAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        arCursorAdapter.swapCursor(null);
    }


}
