package com.dsc.attendancemanagement.attendance;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import com.dsc.attendancemanagement.R;
import com.dsc.attendancemanagement.adapters.AttendanceAdapter;
import com.dsc.attendancemanagement.dataStorers.Classroom;
import com.dsc.attendancemanagement.database.DatabaseManager;
import com.dsc.attendancemanagement.dataStorers.Student;

/**
 * Created by Developer Student Club
 * Varshit Ratna(lead)
 * Devaraj Akhil(Core team)
 */
public class AttendanceActivity extends AppCompatActivity {
    private Context context;
    private Toolbar toolbar;
    private ListView list;
    private ArrayList<Student> arrayList;
    private AttendanceAdapter adapter;
    private Classroom classroom;
    private String classDate = "";
    private FloatingActionButton floatingActionButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.simple_listview_with_toolbar);

        Bundle args = getIntent().getExtras();
        if (args != null) {
            classroom = (Classroom) args.getSerializable("classroom");
        }

        context = this;

        //toolbar
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        Date dateTime = new Date();
        SimpleDateFormat targetFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm");
        classDate = targetFormat.format(dateTime);

        setTitle(classroom.getName());
        toolbar.setSubtitle(classDate);

        list = (ListView) findViewById(R.id.list);
        arrayList = new ArrayList<Student>();
        adapter = new AttendanceAdapter(context, R.layout.checkable_text_item, arrayList);
        list.setAdapter(adapter);

        //empty list view text
        TextView emptyText = (TextView) findViewById(R.id.emptyText);
        emptyText.setText(getString(R.string.emptyMessageSave));
        list.setEmptyView(emptyText);

        setListItemClickListener();

        floatingActionButton = (FloatingActionButton) findViewById(R.id.floatingActionButton);
        floatingActionButton.setImageResource(R.drawable.ic_action_save);
        floatingActionButton.setVisibility(View.VISIBLE);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //saves or inserts
                boolean isExist = false;
                if (classroom != null) {
                    DatabaseManager databaseManager = new DatabaseManager(context);
                    isExist = databaseManager.selectAttendanceToCheckExistance(classroom.getId(),
                            classDate);
                }
                if (isExist) {
                    Snackbar.make(list, getString(R.string.couldNotInsertAttendance),
                            Snackbar.LENGTH_LONG).show();
                } else {
                    boolean isSuccessful = false;

                    if (arrayList != null) {
                        DatabaseManager databaseManager = new DatabaseManager(context);
                        isSuccessful = databaseManager.insertAttendance(arrayList, classDate);
                    }
                    if (isSuccessful) {
                        Intent intent = new Intent();
                        setResult(RESULT_OK, intent);
                    }
                    finish();
                }
            }
        });
        //displays all
        ArrayList<Student> tmpList = null;
        if (classroom != null) {
            DatabaseManager databaseManager = new DatabaseManager(context);
            tmpList = databaseManager.selectStudents(classroom.getId());
        }
        arrayList.clear();

        if (tmpList != null) {
            arrayList.addAll(tmpList);
            adapter.notifyDataSetChanged();
        }
    }

    /**
     * setOnItemClickListener
     */
    private void setListItemClickListener() {
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (arrayList.size() > position) {
                    Student student = arrayList.get(position);
                    boolean isPresent = !student.isPresent();
                    arrayList.get(position).setPresent(isPresent);

                    adapter.notifyDataSetChanged();
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}