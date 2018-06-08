package com.dsc.attendancemanagement.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import com.dsc.attendancemanagement.R;
import com.dsc.attendancemanagement.dataStorers.Student;
/**
 * Created by Developer Student Club
 * Varshit Ratna(lead)
 * Devaraj Akhil(Core team)
 */
public class AttendanceAdapter extends ArrayAdapter<Student> {
    private Context context;
    private int layoutResId;
    private ArrayList<Student> items;
    private boolean once;

    public AttendanceAdapter(Context context, int layoutResId, ArrayList<Student> objects) {
        super(context, layoutResId, objects);
        this.items = objects;
        this.context = context;
        this.layoutResId = layoutResId;
        this.once=false;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;

        if (convertView == null) {
            // return your progress view goes here. Ensure that it has the ID
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            convertView = inflater.inflate(layoutResId, parent, false);
            viewHolder = new ViewHolder();

            viewHolder.rtext = (TextView) convertView.findViewById(R.id.rtext);
            viewHolder.text = (TextView) convertView.findViewById(R.id.text);
            viewHolder.checkBox = (ImageView) convertView.findViewById(R.id.checkBox);
            viewHolder.yes = (ImageView) convertView.findViewById(R.id.yes);
            viewHolder.no = (ImageView) convertView.findViewById(R.id.no);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        final Student item = items.get(position);
        viewHolder.rtext.setText(item.getRollNo());
        viewHolder.text.setText(item.getName());
        viewHolder.yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Student student = items.get(position);
                items.get(position).setPresent(true);
                once=true;
                notifyDataSetChanged();
            }
        });
        viewHolder.no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Student student = items.get(position);
                boolean isPresent = !student.isPresent();
                items.get(position).setPresent(false);
                once=true;
                notifyDataSetChanged();
            }
        });
        if (item.isPresent()) {
            viewHolder.checkBox.setImageResource(R.drawable.abc_btn_check_to_on_mtrl_015);
            viewHolder.checkBox.setColorFilter(context.getResources().getColor(R.color.colourAccent));
        } else if(once){
            viewHolder.checkBox.setImageResource(R.drawable.abc_btn_check_to_on_mtrl_000);
            viewHolder.checkBox.setColorFilter(context.getResources().getColor(R.color.gray));
        } else{
            viewHolder.checkBox.setImageResource(R.drawable.abc_btn_check_to_on_mtrl_000);
            viewHolder.checkBox.setColorFilter(context.getResources().getColor(R.color.gray));
        }

        return convertView;
    }

    public class ViewHolder {
        TextView rtext;
        TextView text;
        ImageView checkBox;
        ImageView yes;
        ImageView no;
    }
}
