package com.e4rdx.snote.activities.texteditor;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ScrollView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.e4rdx.snote.R;
import com.e4rdx.snote.activities.basicNoteEditor.BasicNoteEditor;
import com.e4rdx.snote.activities.checklistEditor.Tag;
import com.e4rdx.snote.activities.notebookDisplayer.NotebookDisplayer;
import com.e4rdx.snote.activities.notebookDisplayer.fragments.tags.FlowLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class TextEditor extends BasicNoteEditor {
    private EditText textInputField;
    private Menu menu;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        tag_flowlayout = R.id.textEditor_tags_flowlayout;

        if(showTags){
            findViewById(R.id.scrollView_texteditor_tags).setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected int getLayoutID() {
        return R.layout.activity_text_editor;
    }

    @Override
    public void onEditNote(JSONObject receivedJson) {
        try {
            textInputField = (EditText)findViewById(R.id.texteditor_textfield);
            textInputField.setText(receivedJson.getString("text"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onCreatedNote(Bundle b) {

    }

    @Override
    public void onSaveAndExit() {
        String noteText = textInputField.getText().toString();
        try {
            jsonData.put("text", noteText);
            jsonData.put("tags", getTags());
            jsonData.put("type", "text");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Intent i = new Intent(getApplicationContext(), NotebookDisplayer.class);
        i.putExtra("jsonData", jsonData.toString());
        i.putExtra("edit", editMode);
        i.putExtra("index", index);
        startActivity(i);
    }

    @Override
    public void onLoadTags(JSONArray tags) {
        try {
            for(int i = 0; i < tags.length(); i++){
                Tag tag;
                tag = new Tag(TextEditor.this, tags.getString(i));
                FlowLayout fl = (FlowLayout) findViewById(R.id.textEditor_tags_flowlayout);
                fl.addView(tag);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_texteditor, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        ScrollView textEditor = findViewById(R.id.scrollView_texteditor_tags);
        switch (item.getItemId()){
            case R.id.texteditor_save:
                onSaveAndExit();
                return true;
            case  R.id.menu_texteditor_toggleTags:
                if (textEditor.getVisibility() == View.VISIBLE) {
                    textEditor.setVisibility(View.GONE);
                } else {
                    textEditor.setVisibility(View.VISIBLE);
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void addNewTag(View v){
        addTag();
    }
}