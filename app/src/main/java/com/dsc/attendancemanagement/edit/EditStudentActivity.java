package com.dsc.attendancemanagement.edit;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import com.dsc.attendancemanagement.R;
import com.dsc.attendancemanagement.adapters.CustomAdapter;
import com.dsc.attendancemanagement.dataStorers.Classroom;
import com.dsc.attendancemanagement.database.DatabaseManager;
import com.dsc.attendancemanagement.dataStorers.Student;

/**
 * Created by Developer Student Club
 * Varshit Ratna(lead)
 * Devaraj Akhil(Core team)
 */
public class EditStudentActivity extends AppCompatActivity {
    private Context context;
    private ListView list;
    private ArrayList<Student> arrayList;
    private CustomAdapter adapter;
    private Classroom classroom;
    private FloatingActionButton floatingActionButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.simple_listview_with_toolbar);

        Bundle args = getIntent().getExtras();// to get intents from fragments and other serialized kays
        if (args != null) {
            classroom = (Classroom) args.getSerializable("classroom");
            Log.v("2d2d2d","nak rala");
        }
        context = this;

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        setTitle(classroom.getName());

        list = (ListView) findViewById(R.id.list);
        arrayList = new ArrayList<Student>();
        adapter = new CustomAdapter(context, R.layout.each_part2, arrayList);
        list.setAdapter(adapter);

        //empty list view text
        TextView emptyText = (TextView) findViewById(R.id.emptyText);
        emptyText.setText(getString(R.string.emptyMessageStudent));
        list.setEmptyView(emptyText);

        setListItemClickListener();

        floatingActionButton = (FloatingActionButton) findViewById(R.id.floatingActionButton);
        floatingActionButton.setImageResource(R.drawable.ic_action_add);
        floatingActionButton.setVisibility(View.VISIBLE);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater li = LayoutInflater.from(context);
                View promptsView = li.inflate(R.layout.prompts, null);
                final AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setView(promptsView);
                final TextView tv = (TextView) promptsView.findViewById(R.id.title);
                final EditText ed = (EditText) promptsView.findViewById(R.id.content);

                tv.setText(getString(R.string.studentDetails));
                builder.setCancelable(true);
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String aa = ed.getText().toString();
                            new InsertStudent().execute("",aa);
                        try {


                            //CLOSE HERE............................................................................................................


                        } catch (Exception e) {
                            e.printStackTrace();

                        }
                    }
                });
                builder.show();
                ed.setAllCaps(true);
                ed.requestFocus();
                InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
            }
        });

        display();
    }

    private void display(){
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
        list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                if (arrayList != null && arrayList.size() > position) {
                    final Student student = arrayList.get(position);

                    AlertDialog.Builder alert = new AlertDialog.Builder(context);
                    alert.setTitle("Delete entry");
                    alert.setMessage("Are you sure you want to delete?");
                    alert.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // continue with delete
                            DatabaseManager databaseManager = new DatabaseManager(context);
                            boolean isSuccessful = databaseManager.deleteStudent(student.getId(), classroom.getId());
                            if (isSuccessful) display();
                        }
                    });
                    alert.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // close dialog
                            dialog.cancel();
                        }
                    });
                    alert.show();
                    //alert
                }
                return true;
            }
        });
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar actions click
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }
    /**
     * Insert student name into DB
     */
    private class InsertStudent extends AsyncTask<String, Void, Boolean> {

        @Override
        protected Boolean doInBackground(String... params) {
            boolean isSuccessful = false;
            String rollno = params[0];
            String student = params[1];
            if (classroom != null) {
                DatabaseManager databaseManager = new DatabaseManager(context);
                isSuccessful = databaseManager.insertStudent(classroom.getId(),rollno, student);
            }

            return isSuccessful;
        }

        @Override
        protected void onPostExecute(Boolean isSuccessful) {
            if (isSuccessful) display();
                //new SelectStudents().execute();
        }
    }
}