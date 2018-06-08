package com.dsc.attendancemanagement.adapters;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import com.dsc.attendancemanagement.R;
import com.dsc.attendancemanagement.dataStorers.Classroom;

/**
 * Created by Developer Student Club
 * Varshit Ratna(lead)
 * Devaraj Akhil(Core team)
 */
public class ClassroomAdapter extends ArrayAdapter<Classroom> {
    private Context context;
    private int layoutResId;
    private ArrayList<Classroom> items;

    public ClassroomAdapter(Context context, int layoutResId, ArrayList<Classroom> objects) {
        super(context, layoutResId, objects);
        this.items = objects;
        this.context = context;
        this.layoutResId = layoutResId;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;

        if (convertView == null) {
            // return your progress view goes here. Ensure that it has the ID
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            convertView = inflater.inflate(layoutResId, parent, false);
            viewHolder = new ViewHolder();

            viewHolder.text = (TextView) convertView.findViewById(R.id.text);
            viewHolder.counter = (TextView) convertView.findViewById(R.id.counter);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        final Classroom item = items.get(position);

        viewHolder.text.setText(item.getName());
        if (item.getStudentNumber() > 0) {
            Log.v("hey",String.valueOf(item.getStudentNumber()));
            viewHolder.counter.setText(String.valueOf(item.getStudentNumber()));
        } else {
            viewHolder.counter.setText("+");
        }

        return convertView;
    }

    public class ViewHolder {
        TextView text;
        TextView counter;
    }
}
