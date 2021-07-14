package com.e4rdx.snote.activities.checklistEditor;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

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

import com.e4rdx.snote.activities.notebookDisplayer.NotebookDisplayer;
import com.e4rdx.snote.R;
import com.e4rdx.snote.activities.notebookDisplayer.fragments.tags.FlowLayout;
import com.e4rdx.snote.dialogs.SelectTagDialog;
import com.e4rdx.snote.dialogs.TextInputDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedList;

public class ChecklistEditor extends AppCompatActivity {
    private EditText noteInput;
    private String noteName;
    private JSONObject jsonData;
    private JSONArray entrys;
    private boolean editMode;
    private int index;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checklist_editor);

        noteInput = (EditText)findViewById(R.id.editTextNote);
        Bundle extras = getIntent().getExtras();

        entrys = new JSONArray();

        editMode = extras.getBoolean("edit");

        if(editMode){
            JSONObject recievedJson = null;
            try {
                recievedJson = new JSONObject(extras.getString("jsonData"));
                noteName = recievedJson.getString("name");
                entrys = recievedJson.getJSONArray("entrys");
                for(int i = 0; i < entrys.length(); i++){
                    LinearLayout parent = (LinearLayout)findViewById(R.id.noteList);
                    JSONObject actualEntry = entrys.getJSONObject(i);
                    parent.addView(new ChecklistEntry(this, actualEntry.getString("text"), actualEntry.getBoolean("state"), parent));
                }
                index = extras.getInt("index");
                JSONArray tags = recievedJson.getJSONArray("tags");
                for (int i = 0; i < tags.length(); i++){
                    FlowLayout fl = (FlowLayout) findViewById(R.id.checklistEditor_tags_flowlayout);
                    Tag t = new Tag(ChecklistEditor.this, tags.getString(i));
                    fl.addView(t);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        else{
            noteName = extras.getString("name");
        }
        getSupportActionBar().setTitle(noteName);

        jsonData = new JSONObject();
        try {
            jsonData.put("name", noteName);
            jsonData.put("type", "checkliste");
            jsonData.put("entrys", new JSONArray());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void addTag(){
        /*TextInputDialog popup = new TextInputDialog(ChecklistEditor.this, getString(R.string.tags_newTag), getString(R.string.tags_enter_tag_name));
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if(i == DialogInterface.BUTTON_POSITIVE){
                    Tag tag = new Tag(ChecklistEditor.this, popup.getText());
                    FlowLayout fl = (FlowLayout) findViewById(R.id.checklistEditor_tags_flowlayout);
                    fl.addView(tag);
                }
            }
        };
        popup.setupButtons(getString(R.string.create), getString(R.string.cancel), dialogClickListener);
        popup.show();*/

        String[] tags = {"t1", "t2", "t3", "t4", "t5"};
        //boolean[] checkedItems = {true, false, false, true, false}
        SelectTagDialog dialog = new SelectTagDialog(ChecklistEditor.this, "Add tag", tags);
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int state) {
                if(state == DialogInterface.BUTTON_POSITIVE){
                    LinkedList<String> choices = dialog.getChoices();
                    for(int i = 0; i < choices.size(); i++){
                        Tag tag = new Tag(ChecklistEditor.this, choices.get(i));
                        FlowLayout fl = (FlowLayout) findViewById(R.id.checklistEditor_tags_flowlayout);
                        fl.addView(tag);
                    }
                }
            }
        };
        dialog.setupButtons(getString(R.string.create), getString(R.string.cancel), dialogClickListener);
        dialog.create().show();
    }

    private JSONArray getTags(){
        JSONArray tags = new JSONArray();
        FlowLayout fl = (FlowLayout) findViewById(R.id.checklistEditor_tags_flowlayout);
        for(int i = 0; i < fl.getChildCount(); i++){
            Tag current = (Tag) fl.getChildAt(i);
            tags.put(current.getName());
        }
        return tags;
    }

    @Override
    public void onBackPressed() {
        saveNote();
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
                saveNote();
                break;
            case R.id.menu_checklisteditor_toggleTags:
                if (tagEditor.getVisibility() == View.VISIBLE) {
                    tagEditor.setVisibility(View.GONE);
                } else {
                    tagEditor.setVisibility(View.VISIBLE);
                }
                break;
            case R.id.menu_checklistEditor_addTag:
                if (tagEditor.getVisibility() == View.GONE) {
                    tagEditor.setVisibility(View.VISIBLE);
                }
                addTag();
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
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    scrollToBottom();
                }
            }, 100);

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

    private void saveNote(){
        entrys = new JSONArray();
        LinearLayout parent = (LinearLayout)findViewById(R.id.noteList);
        for(int i = 0; i < parent.getChildCount(); i++){
            ChecklistEntry current = (ChecklistEntry) parent.getChildAt(i);
            entrys.put(current.getJsonData());
        }
        try {
            jsonData.put("entrys", entrys);
            jsonData.put("tags", getTags());
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
}