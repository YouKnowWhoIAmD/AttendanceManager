package com.dsc.attendancemanagement.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import com.dsc.attendancemanagement.R;
import com.dsc.attendancemanagement.dataStorers.Attendance;

/**
 * Created by Developer Student Club
 * Varshit Ratna(lead)
 * Devaraj Akhil(Core team)
 */
public class StatisticsAdapter extends ArrayAdapter<Attendance> {
    private Context context;
    private int layoutResId;
    private ArrayList<Attendance> items;

    public StatisticsAdapter(Context context, int layoutResId, ArrayList<Attendance> objects) {
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
            viewHolder.keyroll = (TextView) convertView.findViewById(R.id.keyroll);
            viewHolder.key = (TextView) convertView.findViewById(R.id.key);
            viewHolder.value = (TextView) convertView.findViewById(R.id.value);
            viewHolder.valueView = convertView.findViewById(R.id.valueView);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        final Attendance item = items.get(position);
        viewHolder.keyroll.setText(item.getRollNo());
        viewHolder.key.setText(item.getStudentName());
        viewHolder.value.setText(String.valueOf(item.getPresencePercentage()) + "%");

        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams)viewHolder.valueView.getLayoutParams();
        layoutParams.weight = item.getPresencePercentage() / 100f;
        viewHolder.valueView.setLayoutParams(layoutParams);

        return convertView;
    }

    public class ViewHolder {
        TextView keyroll;
        TextView key;
        TextView value;
        View valueView;
    }
}
