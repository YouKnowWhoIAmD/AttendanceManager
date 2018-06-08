package com.dsc.attendancemanagement.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import com.dsc.attendancemanagement.R;
import com.dsc.attendancemanagement.dataStorers.Student;

/**
 * Created by Developer Student Club
 * Varshit Ratna(lead)
 * Devaraj Akhil(Core team)
 */
public class CustomAdapter extends ArrayAdapter<Student> {
    private Context context;
    private int layoutResId;
    private ArrayList<Student> items;

    public CustomAdapter(Context context, int layoutResId, ArrayList<Student> objects) {
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

            viewHolder.num = (TextView) convertView.findViewById(R.id.counter);
            viewHolder.roll = (TextView) convertView.findViewById(R.id.rtext);
            viewHolder.name = (TextView) convertView.findViewById(R.id.text);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        final Student item = items.get(position);
        viewHolder.num.setText(String.valueOf(position+1));
        viewHolder.name.setText(item.getName());
        viewHolder.roll.setText(item.getRollNo());

        return convertView;
    }

    public class ViewHolder {
        TextView num;
        TextView roll;
        TextView name;
    }
}
