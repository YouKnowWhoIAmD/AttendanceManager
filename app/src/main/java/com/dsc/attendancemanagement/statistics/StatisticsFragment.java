package com.dsc.attendancemanagement.statistics;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import com.dsc.attendancemanagement.R;
import com.dsc.attendancemanagement.adapters.ClassroomAdapter;
import com.dsc.attendancemanagement.database.DatabaseManager;
import com.dsc.attendancemanagement.dataStorers.Attendance;
import com.dsc.attendancemanagement.dataStorers.Classroom;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by Developer Student Club
 * Varshit Ratna(lead)
 * Devaraj Akhil(Core team)
 */
public class StatisticsFragment extends Fragment {
    //excel
    private final String PATH_FOLDER = Environment.getExternalStorageDirectory()
            + "/attendance_taker/";
    private final String FILE_NAME = "/attendances.xls";
    private Context context;
    private ListView list;
    private FloatingActionButton floatingActionButton;
    private ArrayList<Classroom> classroomArrayList;
    private ClassroomAdapter classroomAdapter;
    private ArrayList<Attendance> attendanceArrayList = new ArrayList<Attendance>();
    private static final int EXTERNAL_STORAGE_PERMISSION_CONSTANT = 100;
    private static final int REQUEST_PERMISSION_SETTING = 101;
    private boolean sentToSettings = false;
    private SharedPreferences permissionStatus;


    public StatisticsFragment() {}

    public static StatisticsFragment newInstance() {
        StatisticsFragment statisticsFragment = new StatisticsFragment();
        return statisticsFragment;
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
        permissionStatus = context.getSharedPreferences("permissionStatus",MODE_PRIVATE);

        list = (ListView) rootView.findViewById(R.id.list);
        classroomArrayList = new ArrayList<Classroom>();
        classroomAdapter = new ClassroomAdapter(context, R.layout.each_part1, classroomArrayList);
        list.setAdapter(classroomAdapter);

        floatingActionButton = (FloatingActionButton) rootView.findViewById(R.id.fab);
        floatingActionButton.setVisibility(View.VISIBLE);
        floatingActionButton.setImageResource(R.drawable.ic_action_download);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDataForExcel();

                Log.d("hello","came till 87");
            }
        });
        //empty list view text
        TextView emptyText = (TextView) rootView.findViewById(R.id.emptyText);
        list.setEmptyView(emptyText);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (classroomArrayList != null && classroomArrayList.size() > position) {
                    Intent intent = new Intent(context, StudentsListActivity.class);
                    intent.putExtra("classroom", classroomArrayList.get(position));
                    startActivity(intent);
                    Log.d("hello","came till 101");
                }
            }
        });
        display();
        Log.v("a11","normal stat");
        return rootView;
    }

    public void display() {
        DatabaseManager databaseManager = new DatabaseManager(context);
        ArrayList<Classroom> tmpList = databaseManager.selectClassroomsWithStudentNumber();
        classroomArrayList.clear();
        if (tmpList != null) {
            classroomArrayList.addAll(tmpList);
            classroomAdapter.notifyDataSetChanged();
        }
    }

    private void getDataForExcel() {
        DatabaseManager databaseManager = new DatabaseManager(context);
        ArrayList<Attendance> tmpList = databaseManager.selectAllAttendances();
        if (tmpList != null) {
            attendanceArrayList.addAll(tmpList);

            makeWorkbook();
            Log.d("hello","came till 127");

        }
    }

    /**
     * Converts all attendances into excel format
     */
    private void makeWorkbook() {
        Log.d("hello","came till 136");
        int length = classroomArrayList.size();

        HSSFWorkbook wb = new HSSFWorkbook();
        for (int i = 0; i < length; i++) {
            Classroom classroom = classroomArrayList.get(i);

            HSSFSheet sheet = wb.createSheet(classroom.getName());

            //header
            HashMap<String, Integer> date_column_map = new HashMap<String, Integer>();
            ArrayList<String> dates = new ArrayList<String>();
            int rowNumber = 0;
            int colNumber = 1;
            HSSFRow row = sheet.createRow(rowNumber);

            for (int j = 0; j < attendanceArrayList.size(); j++) {
                Attendance attendance = attendanceArrayList.get(j);

                if (classroom.getId() == attendance.getClassroomId()
                        && !dates.contains(attendance.getDateTime())) {
                    HSSFCell cellDate = row.createCell(colNumber);
                    cellDate.setCellValue(attendance.getDateTime());
                    dates.add(attendance.getDateTime());
                    date_column_map.put(attendance.getDateTime(), colNumber);

                    colNumber++;
                }
            }

            //students list at the left column
            HashMap<Integer, Integer> student_row_map = new HashMap<Integer, Integer>();
            ArrayList<Integer> studentIds = new ArrayList<Integer>();
            rowNumber = 1;
            Log.d("babu","for came");
            for (int j = 0; j < attendanceArrayList.size(); j++) {
                Attendance attendance = attendanceArrayList.get(j);

                if (classroom.getId() == attendance.getClassroomId()) {
                    if (!studentIds.contains(attendance.getStudentId())) { //another student
                        row = sheet.createRow(rowNumber);

                        HSSFCell cellStudent = row.createCell(0);

                        cellStudent.setCellValue(attendance.getStudentName());

                        studentIds.add(attendance.getStudentId());
                        student_row_map.put(attendance.getStudentId(), rowNumber);

                        rowNumber++;
                    }
                }
            }

            //now get column number from date columns
            //and get row number from student rows
            //match row-column pair and print into cell
            for (int j = 0; j < attendanceArrayList.size(); j++) {
                Attendance attendance = attendanceArrayList.get(j);

                if (classroom.getId() == attendance.getClassroomId()) {
                    rowNumber = student_row_map.get(attendance.getStudentId());
                    colNumber = date_column_map.get(attendance.getDateTime());

                    row = sheet.getRow(rowNumber);

                    HSSFCell cellPresence = row.createCell(colNumber);

                    cellPresence.setCellValue(attendance.getPresent());
                }
            }
        }
        Log.d("babu","came till 207");
        if (length > 0) storeInternal(wb);

    }

    /**
     * Write into an excel file
     */
    private void storeInternal(HSSFWorkbook wb) {
        //check();
        Log.v("dekhidar",wb.getSheetAt(0).getRow(0).getCell(1).toString());
        FileOutputStream fileOut = null;
        try {
            // Output stream
            // create a File object for the parent directory
            File file = null;   //  File file = new File(directory, csvFile);
            File root = Environment.getExternalStorageDirectory();
            Log.v("dekhidar",root.toString());
                File dir = new File(root.getAbsolutePath()+"/AttendanceManager");
                if(!dir.exists())
                    dir.mkdirs();
                Log.v("dekhidar",dir.toString());
                if(dir.exists())
                    Log.v("dekhidar","ippudu undi 230");
                file = new File(dir,"attendance.csv");
                try{
                    fileOut = new FileOutputStream(file);
                    Log.v("dekhidar",file.toString());
                }catch (FileNotFoundException e){
                    Log.v("ffff","fafafa");
                    e.printStackTrace();
                }
                try{
                    wb.write(fileOut);
                }catch (Exception e){
                    e.printStackTrace();
                    Log.v("222","fadsfda");
                }
                if(fileOut!=null){
                    try{
                        fileOut.flush();
                        fileOut.close();
                    }catch (IOException e){
                        Log.v("333","fsdffsd");
                    }
                }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}