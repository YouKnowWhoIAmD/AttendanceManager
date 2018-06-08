package com.dsc.attendancemanagement.material_dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.dsc.attendancemanagement.R;
import com.dsc.attendancemanagement.interfaces.OnClick;

/**
 * Created by Developer Student Club
 * Varshit Ratna(lead)
 * Devaraj Akhil(Core team)
 */
public class MaterialDialog extends Dialog {

    private TextView content;
    private Button positiveButton;
    private Button negativeButton;

    private OnClick onClick;

    public MaterialDialog(Context context) {
        super(context);
        setContentView(R.layout.material_dialog);

        getWindow().setBackgroundDrawable(new ColorDrawable(context.getResources().getColor(R.color.transparent)));

        content = (TextView) findViewById(R.id.content);
        positiveButton = (Button) findViewById(R.id.positiveButton);
        negativeButton = (Button) findViewById(R.id.negativeButton);
    }

    public void setContent(String value) {
        content.setText(value);
    }

    public void setPositiveButton(String value) {
        positiveButton.setText(value);
        positiveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();

                if (onClick != null) onClick.OnPositive();
            }
        });
    }

    public void setNegativeButton(String value) {
        negativeButton.setVisibility(View.VISIBLE);
        negativeButton.setText(value);
        negativeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();

                if (onClick != null) onClick.OnNegative();
            }
        });
    }

    public void setOnClickListener(OnClick onClick) {
        this.onClick = onClick;
    }
}
