package com.dsc.attendancemanagement.edit;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import com.dsc.attendancemanagement.R;
import com.dsc.attendancemanagement.adapters.ClassroomAdapter;
import com.dsc.attendancemanagement.dataStorers.Classroom;
import com.dsc.attendancemanagement.database.DatabaseManager;

/**
 * Created by Developer Student Club
 * Varshit Ratna(lead)
 * Devaraj Akhil(Core team)
 */
public class EditClassroomFragment extends Fragment {
    private Context context;
    private FloatingActionButton floatingActionButton;
    private ListView list;
    private ArrayList<Classroom> arrayList;
    private ClassroomAdapter adapter;


    public EditClassroomFragment() {}

    public static EditClassroomFragment newInstance() {
        EditClassroomFragment editClassroomFragment = new EditClassroomFragment();
        return editClassroomFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // retain this fragment
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        final View rootView = inflater.inflate(R.layout.simple_listview, container, false);
        context = rootView.getContext();

        list = (ListView) rootView.findViewById(R.id.list);
        arrayList = new ArrayList<Classroom>();
        adapter = new ClassroomAdapter(context, R.layout.each_part1, arrayList);
        list.setAdapter(adapter);

        floatingActionButton = (FloatingActionButton) rootView.findViewById(R.id.fab);
        floatingActionButton.setVisibility(View.VISIBLE);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //addNewItem();
                LayoutInflater li = LayoutInflater.from(context);
                View promptsView = li.inflate(R.layout.prompts, null);
                final AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setView(promptsView);
                final TextView tv = (TextView) promptsView.findViewById(R.id.title);
                final EditText ed = (EditText) promptsView.findViewById(R.id.content);
                tv.setText(getString(R.string.classroomName));
                builder.setCancelable(true);
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String aa = ed.getText().toString().toUpperCase();
                        if (!aa.equals("")){
                            DatabaseManager databaseManager = new DatabaseManager(context);
                            boolean isSuccessful = databaseManager.insertClassroom(aa);
                            if (isSuccessful) display();
                            Log.v("a11","fab insert edit");
                        }
                        try {
                                //InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                                //imm.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
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
        TextView emptyText = (TextView) rootView.findViewById(R.id.emptyText);
        list.setEmptyView(emptyText);
        listen();
        display();Log.v("a11","normal edit");
        return rootView;
    }
    public void hideSoftInput(){
        try {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        } catch (Exception e) {}
    }
    private void listen() {
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (arrayList != null && arrayList.size() > position) {
                    Intent intent = new Intent(context, EditStudentActivity.class);
                    intent.putExtra("classroom", arrayList.get(position));
                    Log.v("idigo",arrayList.get(position).getName());
                    startActivity(intent);
                }
            }
        });

        list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                if (arrayList != null && arrayList.size() > position) {
                    final Classroom classroom = arrayList.get(position);

                    AlertDialog.Builder alert = new AlertDialog.Builder(context);
                    alert.setTitle("Delete entry");
                    alert.setMessage("Are you sure you want to delete?");
                    alert.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            DatabaseManager databaseManager = new DatabaseManager(context);
                            boolean isSuccessful = databaseManager.deleteClassroom(classroom.getId());
                            if(isSuccessful) display();
                            Log.v("a11","delete edit");
                        }
                    });
                    alert.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    alert.show();
                }
                return true;
            }
        });
    }

    private void display(){
        DatabaseManager databaseManager = new DatabaseManager(context);
        ArrayList<Classroom> tmpList = databaseManager.selectClassrooms();
        arrayList.clear();
        if (tmpList != null) {
            arrayList.addAll(tmpList);
            adapter.notifyDataSetChanged();
        }
    }
}