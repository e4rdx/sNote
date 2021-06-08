package com.e4rdx.snote.activities.checklistEditor;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.text.Editable;
import android.text.InputType;
import android.text.Selection;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.text.style.UnderlineSpan;
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

@SuppressLint("ViewConstructor")
public class ChecklistEntry extends LinearLayout {
    private final ChecklistEntry selfReference;
    private boolean isChecked;
    private final EditText noteText;

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
        noteText.setText(markLinks(text), TextView.BufferType.SPANNABLE);
        //noteText.setText(text);
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
        noteText.setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if(!noteText.hasFocus()) {//Don't open link if the text is focused
                    noteText.setText(noteText.getText());//Avoid selecting text
                    String[] parts = noteText.getText().toString().split(" ");
                    if (parts.length > 0) {
                        for (String part : parts) {
                            if (part.contains("https://") || part.contains("http://")) {
                                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(part));
                                context.startActivity(browserIntent);
                            }
                        }
                    }
                }
                return false;
            }
        });
        noteText.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                noteText.setText(markLinks(noteText.getText().toString()), TextView.BufferType.SPANNABLE);
            }
        });
        this.addView(box);
        this.addView(noteText);
    }

    private SpannableStringBuilder markLinks(String s){
        SpannableStringBuilder builder = new SpannableStringBuilder();
        String[] parts = s.split(" ");
        if (parts.length > 0) {
            for (String part : parts) {
                SpannableString str= new SpannableString(part+" ");
                if (part.contains("https://") || part.contains("http://")) {
                    str.setSpan(new ForegroundColorSpan(Color.BLUE), 0, str.length()-1, 0);
                    str.setSpan(new UnderlineSpan(), 0, str.length()-1, 0);
                }
                else{
                    str.setSpan(new ForegroundColorSpan(Color.BLACK), 0, str.length(), 0);
                }
                builder.append(str);
            }
        }
        return builder;
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
