package com.e4rdx.snote.activities.checklistEditor;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

public class ChecklistEntry extends LinearLayout {
    private ChecklistEntry selfReference;
    private boolean isChecked;
    private String noteText;

    public ChecklistEntry(Context context, String text, boolean checkState, LinearLayout pParent) {
        super(context);

        selfReference = this;
        isChecked = checkState;
        noteText = text;

        this.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        this.setOrientation(LinearLayout.HORIZONTAL);

        CheckBox box = new CheckBox(context);
        box.setChecked(isChecked);
        box.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                isChecked = box.isChecked();
            }
        });

        TextView noteText = new TextView(context);
        noteText.setTextColor(Color.BLACK);
        noteText.setText(text);
        noteText.setTextSize(20);

        Button removeNote = new Button(context);
        removeNote.setText("X");
        removeNote.setBackgroundColor(0xffffff);
        removeNote.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                System.out.println("Removing self");
                LinearLayout parent = (LinearLayout) getParent();
                parent.removeView(selfReference);
            }
        });

        this.addView(box);
        this.addView(noteText);
        this.addView(removeNote);
    }

    public JSONObject getJsonData(){
        JSONObject myJsonData = new JSONObject();

        try {
            myJsonData.put("text", noteText);
            myJsonData.put("state", isChecked);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return myJsonData;
    }
}
