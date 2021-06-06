package com.e4rdx.snote.activities.checklistEditor;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.text.InputType;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.e4rdx.snote.R;
import com.e4rdx.snote.popups.TextInputPopup;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONException;
import org.json.JSONObject;

import androidx.coordinatorlayout.widget.CoordinatorLayout;

public class ChecklistEntry extends LinearLayout {
    private ChecklistEntry selfReference;
    private boolean isChecked;
    private EditText noteText;

    @SuppressLint("ClickableViewAccessibility")
    public ChecklistEntry(Context context, String text, boolean checkState, LinearLayout parentLayout) {
        super(context);

        selfReference = this;
        isChecked = checkState;

        this.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        this.setOrientation(LinearLayout.HORIZONTAL);

        CheckBox box = new CheckBox(context);
        box.setChecked(isChecked);
        box.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                isChecked = box.isChecked();
            }
        });

        noteText = new EditText(context);
        noteText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        noteText.setBackgroundResource(android.R.color.transparent);
        noteText.setTextColor(Color.BLACK);
        noteText.setText(text);
        noteText.setTextSize(20);
        noteText.setOnTouchListener(new OnSwipeTouchListener(context) {
            public void onSwipeRight() {
                Snackbar s = Snackbar.make(parentLayout, R.string.checklist_removed_entry, Snackbar.LENGTH_LONG);
                s.setAction(R.string.undo, new View.OnClickListener(){
                    @Override
                    public void onClick(View view) {
                        setVisibility(VISIBLE);
                    };
                });
                s.addCallback(new Snackbar.Callback() {
                    @Override
                    public void onDismissed(Snackbar snackbar, int event) {
                        //Snackbar is gone
                        if(getVisibility() == View.GONE) {
                            LinearLayout parent = (LinearLayout) getParent();
                            parent.removeView(selfReference);
                        }
                    }
                    @Override
                    public void onShown(Snackbar snackbar) {
                        //Snackbar appears
                    }
                });
                s.show();

                setVisibility(View.GONE);
            }
        });
        this.addView(box);
        this.addView(noteText);
    }

    public JSONObject getJsonData(){
        JSONObject myJsonData = new JSONObject();

        try {
            myJsonData.put("text", noteText.getText().toString());
            myJsonData.put("state", isChecked);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return myJsonData;
    }
}
