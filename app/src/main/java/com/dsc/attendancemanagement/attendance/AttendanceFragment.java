package com.dsc.attendancemanagement.attendance;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

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
public class AttendanceFragment extends Fragment {
    private Context context;
    private ListView list;
    private ArrayList<Classroom> arrayList;
    private ClassroomAdapter adapter;


    public AttendanceFragment() {}

    public static AttendanceFragment newInstance() {
        AttendanceFragment attendanceFragment = new AttendanceFragment();
        return attendanceFragment;
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

        View rootView = inflater.inflate(R.layout.simple_listview, container, false);
        context = rootView.getContext();

        list = (ListView) rootView.findViewById(R.id.list);
        arrayList = new ArrayList<Classroom>();
        adapter = new ClassroomAdapter(context, R.layout.each_part1, arrayList);
        list.setAdapter(adapter);

        //empty list view text
        TextView emptyText = (TextView) rootView.findViewById(R.id.emptyText);
        list.setEmptyView(emptyText);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (arrayList != null && arrayList.size() > position) {
                    Intent intent = new Intent(context, AttendanceActivity.class);
                    intent.putExtra("classroom", arrayList.get(position));
                    startActivityForResult(intent, 0);//two way activity gets the result after taking the attendance so as to update without any lags
                }
            }
        });
        //new SelectClassrooms().execute();
        display();Log.v("a11","normal att");
        return rootView;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            Toast.makeText(context,"Saved",Toast.LENGTH_SHORT).show();
        }
    }

    public void display() {
        DatabaseManager databaseManager = new DatabaseManager(context);
        ArrayList<Classroom> tmpList = databaseManager.selectClassroomsWithStudentNumber();
        arrayList.clear();
        if (tmpList != null) {
            arrayList.addAll(tmpList);
            adapter.notifyDataSetChanged();
        }
    }
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            display();
        }
    }
}