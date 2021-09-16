package com.e4rdx.snote.activities.checklistEditor;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.e4rdx.snote.R;
import com.e4rdx.snote.activities.basicNoteEditor.BasicNoteEditor;
import com.e4rdx.snote.activities.notebookDisplayer.NotebookDisplayer;
import com.e4rdx.snote.activities.notebookDisplayer.fragments.tags.FlowLayout;
import com.e4rdx.snote.dialogs.SelectTagDialog;
import com.e4rdx.snote.utils.SNoteManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedList;

public class ChecklistEditor extends BasicNoteEditor {
    private EditText noteInput;
    private JSONArray entrys;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        noteInput = (EditText)findViewById(R.id.editTextNote);
        tag_flowlayout = R.id.checklistEditor_tags_flowlayout;

        if(showTags){
            findViewById(R.id.scrollView_checklisteditor_tags).setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected int getLayoutID() {
        return R.layout.activity_checklist_editor;
    }

    @Override
    public void onEditNote(JSONObject receivedJson) {
        try {
            entrys = receivedJson.getJSONArray("entrys");
            LinearLayout parent = (LinearLayout) findViewById(R.id.noteList);
            for (int i = 0; i < entrys.length(); i++) {
                JSONObject actualEntry = entrys.getJSONObject(i);
                parent.addView(new ChecklistEntry(this, actualEntry.getString("text"), actualEntry.getBoolean("state"), parent));
            }
        } catch (JSONException e){
            e.printStackTrace();
        }
    }

    @Override
    public void onCreatedNote(Bundle b) {

    }

    @Override
    public void onLoadTags(JSONArray tags) {
        try {
            for (int i = 0; i < tags.length(); i++) {
                FlowLayout fl = (FlowLayout) findViewById(R.id.checklistEditor_tags_flowlayout);
                Tag t = new Tag(ChecklistEditor.this, tags.getString(i));
                fl.addView(t);
            }
        } catch (JSONException e){
            e.printStackTrace();
        }
    }

    @Override
    public void onSaveAndExit() {
        entrys = new JSONArray();
        LinearLayout parent = (LinearLayout)findViewById(R.id.noteList);
        for(int i = 0; i < parent.getChildCount(); i++){
            ChecklistEntry current = (ChecklistEntry) parent.getChildAt(i);
            if(current.getVisibility() == View.VISIBLE) {
                entrys.put(current.getJsonData());
            }
        }
        try {
            jsonData.put("entrys", entrys);
            jsonData.put("tags", getTags());
            jsonData.put("type", "checkliste");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Intent i = new Intent(getApplicationContext(), NotebookDisplayer.class);
        i.putExtra("jsonData", jsonData.toString());
        System.out.println(jsonData.toString());
        i.putExtra("edit", editMode);
        i.putExtra("index", index);
        startActivity(i);
    }

    public void addNewTag(View v){
        addTag();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_checklisteditor, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        ScrollView tagEditor = findViewById(R.id.scrollView_checklisteditor_tags);
        switch (item.getItemId()) {
            case R.id.menu_checklisteditor_save:
                onSaveAndExit();
                break;
            case R.id.menu_checklisteditor_toggleTags:
                if (tagEditor.getVisibility() == View.VISIBLE) {
                    tagEditor.setVisibility(View.GONE);
                } else {
                    tagEditor.setVisibility(View.VISIBLE);
                }
                break;
            }
        return super.onOptionsItemSelected(item);
    }

    public void addNote(View v){
        if(!noteInput.getText().toString().matches("")) {
            LinearLayout parent = (LinearLayout) findViewById(R.id.noteList);
            ChecklistEntry note = new ChecklistEntry(this, noteInput.getText().toString(), false, parent);
            parent.addView(note);

            //Scroll after short delay to bottom
            final Handler handler = new Handler(Looper.getMainLooper());
            handler.postDelayed(this::scrollToBottom, 100);

            noteInput.setText("");
        }
        else{
            Toast.makeText(getApplicationContext(), getString(R.string.checklist_enter_text), Toast.LENGTH_LONG).show();
        }
    }

    private void scrollToBottom(){
        //Scroll to bottom
        final ScrollView scrollview = ((ScrollView) findViewById(R.id.checklist_scrollView));
        View lastChild = scrollview.getChildAt(scrollview.getChildCount() - 1);
        int bottom = lastChild.getBottom() + scrollview.getPaddingBottom();
        int sy = scrollview.getScrollY();
        int sh = scrollview.getHeight();
        int delta = bottom - (sy + sh);
        scrollview.smoothScrollBy(0, delta);
    }
}