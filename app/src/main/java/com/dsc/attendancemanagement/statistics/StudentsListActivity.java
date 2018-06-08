package com.dsc.attendancemanagement.statistics;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.dsc.attendancemanagement.dataStorers.Classroom;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.ArrayList;

import com.dsc.attendancemanagement.R;
import com.dsc.attendancemanagement.adapters.StatisticsAdapter;
import com.dsc.attendancemanagement.database.DatabaseManager;
import com.dsc.attendancemanagement.dataStorers.Attendance;

/**
 * Created by Developer Student Club
 * Varshit Ratna(lead)
 * Devaraj Akhil(Core team)
 */
public class StudentsListActivity extends AppCompatActivity {
    private Context context;
    private ListView list;
    private ArrayList<Attendance> attendanceList;
    private StatisticsAdapter adapter;
    private Classroom classroom;
    private GraphView graph;
    private LinearLayout graphLayout;
    private Attendance attendance;
    private ArrayList<Attendance> graphList;
    private ImageView x;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.statistics);

        Bundle args = getIntent().getExtras();
        if (args != null) {
            classroom = (Classroom) args.getSerializable("classroom");
        }

        context = this;

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        setTitle(classroom.getName());

        graphLayout = (LinearLayout) findViewById(R.id.graphLayout);
        graph = (GraphView) findViewById(R.id.graph);
        x = (ImageView) findViewById(R.id.x);
        graphList = new ArrayList<Attendance>();

        list = (ListView) findViewById(R.id.list);
        attendanceList = new ArrayList<Attendance>();
        adapter = new StatisticsAdapter(context, R.layout.hash_text_item, attendanceList);
        list.setAdapter(adapter);

        TextView emptyText = (TextView) findViewById(R.id.emptyText);
        list.setEmptyView(emptyText);

        x.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (graphLayout.getVisibility() == View.VISIBLE) {
                    graphLayout.setVisibility(View.GONE);
                }
            }
        });

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (attendanceList != null && attendanceList.size() > position) {
                    attendance = attendanceList.get(position);
                    ArrayList<Attendance> tmpList = null;
                    if (attendance != null) {
                        DatabaseManager databaseManager = new DatabaseManager(context);
                        tmpList = databaseManager.selectAllAttendancesOfStudent(attendance.getClassroomId(),
                                attendance.getStudentId());
                    }
                    graphList.clear();

                    if (tmpList != null) {
                        graphList.addAll(tmpList);

                        openGraph();
                    }
                }
            }
        });

        ArrayList<Attendance> tmpList = null;
        if (classroom != null) {
            DatabaseManager databaseManager = new DatabaseManager(context);
            tmpList = databaseManager.selectAllAttendancesOfClass(classroom.getId());
        }
        attendanceList.clear();

        if (tmpList != null) {
            attendanceList.addAll(tmpList);
            adapter.notifyDataSetChanged();
        }
    }

    private void openGraph() {
        ArrayList<Integer> presenceList = new ArrayList<Integer>();
        int weekCount = graphList.size();
        int present = 0;

        for (int i = 0; i < weekCount; i++) {
            Attendance tmpAttendance = graphList.get(i);

            if (tmpAttendance.getPresent() == 1) {
                present++;
            }

            int percentage = (int) ((double)present * 100 / (i+1));//graph point for every day
            presenceList.add(percentage);
        }
        DataPoint[] dataPoints = new DataPoint[presenceList.size()+1];
        dataPoints[0] = new DataPoint(0, 0);//to start from (0,0) coordinate so as to not directly jump on to a particular percentage
        for (int i = 0; i < presenceList.size(); i++) {
            dataPoints[i+1] = new DataPoint((i+1), presenceList.get(i));
        }

        LineGraphSeries<DataPoint> series = new LineGraphSeries<DataPoint>(dataPoints);
        series.setColor(getResources().getColor(R.color.white));

        graph.removeAllSeries();//Reset previous data
        graph.addSeries(series);//Insert current data

        graph.setTitle(attendance.getStudentName());
        graph.getViewport().setMaxY(100);
        graph.getViewport().setYAxisBoundsManual(true);//to keep up with changing values
        graph.getViewport().setMaxX(presenceList.size());//no.of days
        graph.getViewport().setXAxisBoundsManual(true);
        graph.getViewport().setScalable(true);

        if (graphLayout.getVisibility() != View.VISIBLE) {
            graphLayout.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onBackPressed() {
        if (graphLayout.getVisibility() == View.VISIBLE) {
            graphLayout.setVisibility(View.GONE);
        } else {
            finish();
        }
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